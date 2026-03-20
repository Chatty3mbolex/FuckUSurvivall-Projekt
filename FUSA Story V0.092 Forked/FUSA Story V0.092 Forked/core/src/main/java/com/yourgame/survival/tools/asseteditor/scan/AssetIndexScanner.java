package com.yourgame.survival.tools.asseteditor.scan;

import com.yourgame.survival.tools.asseteditor.model.AssetKind;
import com.yourgame.survival.tools.asseteditor.model.IndexEntry;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builds the IST index by scanning the actual Java source files.
 * No manual indexing. Names are extracted from atlas.findRegion("...") style calls.
 */
public final class AssetIndexScanner {
  private AssetIndexScanner() {}
  private static final Pattern LITERAL_CALL = Pattern.compile("\\b(?:findRegions?|findRegion|req|region)\\s*\\(\\s*\\\"([^\\\"]+)\\\"");
  public static List<IndexEntry> scan(File projectRoot) {
    File src = new File(projectRoot, "core/src/main/java");
    List<File> javaFiles = new ArrayList<>();
    collectJava(src, javaFiles);

    // De-dup by name, keep best kind if repeated.
    Map<String, IndexEntry> out = new HashMap<>();

    for (File f : javaFiles) {
      String rel = rel(projectRoot, f);
      AssetKind kindHint = kindFromPath(rel);

      String text = readAll(f);
      if (text == null) continue;

      Matcher m = LITERAL_CALL.matcher(text);
      while (m.find()) {
        String name = m.group(1);
        if (name == null || name.trim().isEmpty()) continue;
        name = name.trim();

        IndexEntry prev = out.get(name);
        AssetKind kind = kindHint;
        if (prev != null) {
          // prefer TILE/ENTITY/UI over OTHER
          kind = prefer(prev.kind, kindHint);
        }
        out.put(name, new IndexEntry(name, kind, rel));
      }
    }

    List<IndexEntry> list = new ArrayList<>(out.values());
    list.sort(Comparator.comparing(a -> a.name));
    return list;
  }

  public static final class ExportBundle {
    public final List<IndexEntry> entries;
    public ExportBundle(List<IndexEntry> entries) {
      this.entries = entries;
    }
  }

  /**
   * Builds an export list that includes:
   * - all atlas regions
   * - all code-referenced regions (including ones missing in atlas)
   */
  public static ExportBundle buildExport(File projectRoot, TextureAtlas atlas) {
    Map<String, IndexEntry> out = new HashMap<>();

    // 1) Atlas regions are the base truth.
    if (atlas != null) {
      for (TextureAtlas.AtlasRegion r : atlas.getRegions()) {
        if (r == null || r.name == null) continue;
        String n = r.name.trim();
        if (n.isEmpty()) continue;
        out.put(n, new IndexEntry(n, AssetKind.OTHER, "atlas", false));
      }
    }

    // 2) Code scan overlays kind + adds missing entries.
    List<IndexEntry> scanned = scan(projectRoot);
    if (scanned != null) {
      for (IndexEntry e : scanned) {
        if (e == null || e.name == null) continue;
        String n = e.name.trim();
        if (n.isEmpty()) continue;

        IndexEntry prev = out.get(n);
        if (prev == null) {
          out.put(n, new IndexEntry(n, e.kind, e.foundIn, true));
        } else {
          AssetKind k = prefer(prev.kind, e.kind);
          out.put(n, new IndexEntry(n, k, e.foundIn != null ? e.foundIn : prev.foundIn, false));
        }
      }
    }

    List<IndexEntry> list = new ArrayList<>(out.values());
    list.sort(Comparator.comparing(a -> a.name));
    return new ExportBundle(list);
  }

  /** Writes index.json, index.csv and an embedded index.html into the given folder. */
  public static void writeExport(File outDir, ExportBundle b) {
    if (outDir == null || b == null || b.entries == null) return;
    outDir.mkdirs();

    File json = new File(outDir, "index.json");
    File csv = new File(outDir, "index.csv");
    File html = new File(outDir, "index.html");

    String jsonText = toJson(b.entries);
    writeText(json, jsonText);
    writeText(csv, toCsv(b.entries));
    writeText(html, toHtmlEmbedded(jsonText));
  }

  private static void writeText(File f, String s) {
    try {
      java.nio.file.Files.createDirectories(f.getParentFile().toPath());
      java.nio.file.Files.writeString(f.toPath(), s == null ? "" : s, StandardCharsets.UTF_8);
    } catch (Throwable ignored) {}
  }

  private static String escJson(String s) {
    if (s == null) return "";
    StringBuilder sb = new StringBuilder(s.length() + 16);
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      switch (c) {
        case '\\': sb.append("\\\\"); break;
        case '"': sb.append("\\\""); break;
        case '\n': sb.append("\\n"); break;
        case '\r': sb.append("\\r"); break;
        case '\t': sb.append("\\t"); break;
        default: sb.append(c);
      }
    }
    return sb.toString();
  }

  private static String toJson(List<IndexEntry> entries) {
    StringBuilder sb = new StringBuilder(entries.size() * 128);
    sb.append('[');
    boolean first = true;
    for (IndexEntry e : entries) {
      if (e == null) continue;
      if (!first) sb.append(',');
      first = false;
      sb.append('{');
      sb.append("\"name\":\"").append(escJson(e.name)).append("\",");
      sb.append("\"kind\":\"").append(e.kind == null ? "OTHER" : e.kind.name()).append("\",");
      sb.append("\"foundIn\":\"").append(escJson(e.foundIn)).append("\",");
      sb.append("\"missingInAtlas\":").append(e.missingInAtlas ? "true" : "false");
      sb.append('}');
    }
    sb.append(']');
    return sb.toString();
  }

  private static String toCsv(List<IndexEntry> entries) {
    StringBuilder sb = new StringBuilder(entries.size() * 64);
    sb.append("name,kind,missingInAtlas,foundIn\n");
    for (IndexEntry e : entries) {
      if (e == null) continue;
      sb.append('"').append((e.name == null ? "" : e.name).replace("\"", "\"\"")).append('"').append(',');
      sb.append(e.kind == null ? "OTHER" : e.kind.name()).append(',');
      sb.append(e.missingInAtlas ? "true" : "false").append(',');
      sb.append('"').append((e.foundIn == null ? "" : e.foundIn).replace("\"", "\"\"")).append('"');
      sb.append('\n');
    }
    return sb.toString();
  }

  private static String toHtmlEmbedded(String jsonText) {
    // Minimal, single-file searchable index.
    return "<!doctype html>\n" +
        "<meta charset=\"utf-8\">\n" +
        "<title>Asset Index</title>\n" +
        "<style>body{font-family:system-ui,Segoe UI,Arial;margin:12px} input,select{font-size:14px;padding:6px} table{border-collapse:collapse;width:100%;margin-top:10px} td,th{border:1px solid #ddd;padding:6px;font-size:12px} tr:nth-child(even){background:#f7f7f7} .missing{color:#b00020;font-weight:600}</style>\n" +
        "<h3>Asset Index</h3>\n" +
        "<div>" +
        "<input id=\"q\" placeholder=\"search...\" style=\"width:320px\"> " +
        "<select id=\"k\"><option value=\"ALL\">ALL</option><option value=\"IST\">IST</option><option value=\"TEMPLATE\">TEMPLATE</option></select>" +
        "</div>\n" +
        "<table><thead><tr><th>Name</th><th>Kind</th><th>Status</th><th>Found In</th></tr></thead><tbody id=\"tb\"></tbody></table>\n" +
        "<script>\n" +
        "const DATA=" + (jsonText == null ? "[]" : jsonText) + ";\n" +
        "const q=document.getElementById('q');\n" +
        "const k=document.getElementById('k');\n" +
        "const tb=document.getElementById('tb');\n" +
        "function render(){\n" +
        "  const qs=(q.value||'').toLowerCase().trim();\n" +
        "  const ks=k.value;\n" +
        "  tb.innerHTML='';\n" +
        "  for(const e of DATA){\n" +
        "    const miss=!!e.missingInAtlas;\n" +
        "    if(ks==='IST' && miss) continue;\n" +
        "    if(ks==='TEMPLATE' && !miss) continue;\n" +
        "    if(qs && !(e.name||'').toLowerCase().includes(qs)) continue;\n" +
        "    const tr=document.createElement('tr');\n" +
        "    const status=miss?'TEMPLATE':'IST';\n" +
        "    tr.innerHTML=`<td class='${miss?'missing':''}'>${e.name||''}</td><td>${e.kind||''}</td><td>${status}</td><td>${e.foundIn||''}</td>`;\n" +
        "    tb.appendChild(tr);\n" +
        "  }\n" +
        "}\n" +
        "q.addEventListener('input',render);\n" +
        "k.addEventListener('change',render);\n" +
        "render();\n" +
        "</script>\n";
  }

  private static AssetKind prefer(AssetKind a, AssetKind b) {
    return rank(b) > rank(a) ? b : a;
  }

  private static int rank(AssetKind k) {
    if (k == AssetKind.TILE) return 4;
    if (k == AssetKind.ENTITY) return 3;
    if (k == AssetKind.UI) return 2;
    return 1;
  }

    private static AssetKind kindFromPath(String rel) {
    String r = rel.replace('\\', '/');
    // Deterministic kind mapping by registry file / package location.
    if (r.contains("/render/TilesetRegions")) return AssetKind.TILE;
    if (r.contains("/world/TileIds") || r.contains("/world/TileLayers")) return AssetKind.TILE;
    if (r.contains("/render/EntityRegions")) return AssetKind.ENTITY;
    if (r.contains("/data/Item")) return AssetKind.ENTITY; // no ITEM enum; treat as ENTITY-like
    if (r.contains("/render/UiRegions")) return AssetKind.UI;
    return AssetKind.OTHER;
  }


  private static void collectJava(File dir, List<File> out) {
    if (dir == null || !dir.exists()) return;
    File[] kids = dir.listFiles();
    if (kids == null) return;
    for (File k : kids) {
      if (k.isDirectory()) collectJava(k, out);
      else if (k.getName().endsWith(".java")) out.add(k);
    }
  }

  private static String readAll(File f) {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
      StringBuilder sb = new StringBuilder((int)Math.min(f.length(), 1024 * 1024));
      char[] buf = new char[8192];
      int n;
      while ((n = br.read(buf)) >= 0) sb.append(buf, 0, n);
      return sb.toString();
    } catch (Throwable t) {
      return null;
    }
  }

  private static String rel(File root, File f) {
    try {
      String rp = root.getCanonicalPath();
      String fp = f.getCanonicalPath();
      if (fp.startsWith(rp)) {
        String r = fp.substring(rp.length());
        if (r.startsWith(File.separator)) r = r.substring(1);
        return r;
      }
    } catch (Throwable ignored) {}
    return f.getPath();
  }
}
