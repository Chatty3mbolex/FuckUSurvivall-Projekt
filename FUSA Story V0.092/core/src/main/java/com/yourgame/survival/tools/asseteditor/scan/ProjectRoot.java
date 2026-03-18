package com.yourgame.survival.tools.asseteditor.scan;

import java.io.File;

/**
 * Resolves the project root on desktop in a launch-location independent way.
 * Uses Gradle wrapper / build markers instead of relying on folder names.
 */
public final class ProjectRoot {
  private ProjectRoot() {}

  /** Resolves a project-relative path to an absolute filesystem path. */
  public static String resolve(String rel) {
    return new File(find(), rel).getAbsolutePath();
  }

  public static File find() {
    // We must be robust to arbitrary folder names (e.g. "FuckUSurvival Stable 0.050"),
    // and to being launched from anywhere (IDE, cmd, double-click, shortcuts).
    //
    // Strategy:
    // 1) Try user.dir and the executable/jar directory (if available).
    // 2) Walk upwards and look for Gradle wrapper markers (gradlew / gradlew.bat) or settings.gradle*.
    // 3) If not found, look one level down (common when launching from a parent workspace folder).
    // 4) Last resort: user.dir.

    File userDir = new File(System.getProperty("user.dir", "."));
    File codeDir = null;
    try {
      java.net.URL url = ProjectRoot.class.getProtectionDomain().getCodeSource().getLocation();
      if (url != null) {
        File f = new File(url.toURI());
        codeDir = f.isFile() ? f.getParentFile() : f;
      }
    } catch (Throwable ignored) {
      codeDir = null;
    }

    File root = tryFindFrom(userDir);
    if (root != null) return root;

    if (codeDir != null) {
      root = tryFindFrom(codeDir);
      if (root != null) return root;
    }

    return userDir;
  }

  private static File tryFindFrom(File base) {
    if (base == null) return null;

    // 1) Upwards scan
    File cur = base;
    for (int i = 0; i < 10 && cur != null; i++) {
      if (looksLikeProjectRoot(cur)) return cur;
      cur = cur.getParentFile();
    }

    // 2) One-level-down scan (workspace folder that contains the project)
    File[] kids = base.listFiles();
    if (kids != null) {
      for (File k : kids) {
        if (!k.isDirectory()) continue;
        if (looksLikeProjectRoot(k)) return k;
      }
    }

    return null;
  }

  private static boolean looksLikeProjectRoot(File dir) {
    if (dir == null || !dir.isDirectory()) return false;

    // Gradle wrapper markers (preferred)
    if (new File(dir, "gradlew.bat").exists()) return true;
    if (new File(dir, "gradlew").exists()) return true;

    // Build markers (fallback)
    if (new File(dir, "settings.gradle").exists()) return true;
    if (new File(dir, "settings.gradle.kts").exists()) return true;
    if (new File(dir, "build.gradle").exists()) return true;
    if (new File(dir, "build.gradle.kts").exists()) return true;

    return false;
  }
}
