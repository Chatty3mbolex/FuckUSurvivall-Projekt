/*
  FuckUSurvival Roadmap Local Server
  - serves /roadmap/index.html and assets
  - provides write access to:
      roadmap/ROADMAP_DB.json
      CHECKLIST_TODO.md
      STATE.json
  - no external dependencies

  Usage (from project root: FuckUSurvival/):
    node roadmap/server.js

  Then open:
    http://localhost:8011/roadmap/index.html
*/

const http = require('http');
const fs = require('fs');
const path = require('path');
const url = require('url');

const PROJECT_ROOT = path.resolve(__dirname, '..');
const ROADMAP_DIR = path.resolve(PROJECT_ROOT, 'roadmap');
const ROADMAP_DB_PATH = path.resolve(ROADMAP_DIR, 'ROADMAP_DB.json');
const CHECKLIST_PATH = path.resolve(PROJECT_ROOT, 'CHECKLIST_TODO.md');
const STATE_PATH = path.resolve(PROJECT_ROOT, 'STATE.json');
const ROADMAP_INDEX_PATH = path.resolve(PROJECT_ROOT, 'ROADMAP_INDEX.md');
const FEATURES_DB_PATH = path.resolve(ROADMAP_DIR, 'FEATURES_DB.json');

// Bind to IPv6 any-address so "localhost" (often ::1 on Windows) works.
// Node will also accept IPv4-mapped connections in this mode.
const HOST = '::';
const PORT = 8011;

function readText(p) {
  return fs.readFileSync(p, 'utf8');
}

function writeTextAtomic(p, content) {
  const tmp = p + '.tmp';
  fs.writeFileSync(tmp, content, 'utf8');
  fs.renameSync(tmp, p);
}



function buildRoadmapIndex(db) {
  const tasks = [...(db.tasks || [])].sort((a, b) => (a.timeline || 0) - (b.timeline || 0));
  const lines = [
    `# ROADMAP_INDEX (v${db.version || '?.???'})`,
    '',
    'Die Roadmap ist die CHECKLIST_TODO.md. Dieser Index ist nur zum schnellen Springen (ID-Suche).',
    ''
  ];
  for (const t of tasks) {
    const label = String(t.label || '').trim();
    lines.push(`- ${t.id}${label ? ` – ${label}` : ''}`);
  }
  lines.push('');
  return lines.join('\n');
}

function writeIndexAtomic(db) {
  try {
    writeTextAtomic(ROADMAP_INDEX_PATH, buildRoadmapIndex(db));
  } catch {
    // non-fatal
  }
}

function safeJsonParse(s) {
  try { return JSON.parse(s); } catch { return null; }
}

function parseChecklist(md) {
  const lines = md.split(/\r?\n/);
  const done = new Set();
  const all = new Set();
  const doneSub = new Set();
  const allSub = new Set();

  for (const ln of lines) {
    // Node
    let m = ln.match(/^\s*-\s*\[(x| )\]\s+(NODE_\d+_[A-Z0-9_]+)\s*$/i);
    if (m) {
      const id = m[2].trim();
      all.add(id);
      if (m[1].toLowerCase() === 'x') done.add(id);
      continue;
    }

    // Subtask (additional task inside node)
    m = ln.match(/^\s*-\s*\[(x| )\]\s+(NODE_\d+_[A-Z0-9_]+__AT_\d{3})\s*:\s*(.*)$/i);
    if (m) {
      const sid = m[2].trim();
      allSub.add(sid);
      if (m[1].toLowerCase() === 'x') doneSub.add(sid);
      continue;
    }
  }

  return { raw: md, doneIds: done, allIds: all, doneSubIds: doneSub, allSubIds: allSub };
}

function computeNextAndLast(tasks, doneSet) {
  const ordered = [...tasks].sort((a, b) => (a.timeline || 0) - (b.timeline || 0));
  let next = null;
  let last = null;
  for (const t of ordered) {
    if (doneSet.has(t.id)) last = t.id;
    else if (!next) next = t.id;
  }
  return { NEXT_NODE: next || '—', LAST_COMPLETED_NODE: last || '—' };
}

function sanitizeAdditionalText(text) {
  let t = String(text || '').replace(/\r?\n/g, ' ').trim();
  if (t.length > 180) t = t.slice(0, 180).trim();
  // strip control chars
  t = t.replace(/[\u0000-\u001F\u007F]/g, '');
  return t;
}

function nextAdditionalId(nodeId, task) {
  const subs = Array.isArray(task.additionalTasks) ? task.additionalTasks : [];
  let maxN = 0;
  for (const st of subs) {
    const sid = String(st.id || '');
    const mm = sid.match(/__AT_(\d{3})$/);
    if (mm) maxN = Math.max(maxN, parseInt(mm[1], 10));
  }
  const n = maxN + 1;
  return `${nodeId}__AT_${String(n).padStart(3, '0')}`;
}

function buildChecklistFromDb(db, doneSet) {
  const tasks = [...db.tasks].sort((a, b) => (a.timeline || 0) - (b.timeline || 0));
  const lines = ['# CHECKLIST_TODO.md'];

  for (const t of tasks) {
    lines.push(`- [${doneSet.has(t.id) ? 'x' : ' '}] ${t.id}`);
    const subs = Array.isArray(t.additionalTasks) ? t.additionalTasks : [];
    for (const st of subs) {
      const sid = String(st.id || '').trim();
      const txt = String(st.text || '').trim();
      if (!sid) continue;
      lines.push(`  - [${st.done ? 'x' : ' '}] ${sid}: ${txt}`);
    }
  }

  lines.push('');
  return lines.join('\n');
}



function renumberTimeline(tasks) {
  const ordered = [...tasks].sort((a, b) => (a.timeline || 0) - (b.timeline || 0));
  for (let i = 0; i < ordered.length; i++) {
    ordered[i].timeline = (i + 1) * 10;
  }
}

function nextNodeNumber(tasks) {
  let maxN = 0;
  for (const t of tasks) {
    const m = String(t.id || '').match(/^NODE_(\d+)_/);
    if (m) maxN = Math.max(maxN, parseInt(m[1], 10));
  }
  return maxN + 1;
}

function slugFromLabel(label) {
  let s = String(label || '').trim().toUpperCase();
  s = s.replace(/[^A-Z0-9]+/g, '_');
  s = s.replace(/^_+|_+$/g, '');
  if (!s) s = 'NEW_NODE';
  if (s.length > 32) s = s.slice(0, 32).replace(/_+$/g, '');
  return s || 'NEW_NODE';
}

function buildUpdatedState(existingStateText, db, nextLast) {
  let obj = safeJsonParse(existingStateText);
  if (!obj || typeof obj !== 'object') {
    obj = {
      PROJECT: db.project,
      VERSION_NOTE: `Roadmap server export (DB v${db.version})`,
      NOTES: ''
    };
  }
  obj.NEXT_NODE = nextLast.NEXT_NODE;
  obj.LAST_COMPLETED_NODE = nextLast.LAST_COMPLETED_NODE;
  if (typeof obj.VERSION_NOTE !== 'string') obj.VERSION_NOTE = `Roadmap server export (DB v${db.version})`;
  return JSON.stringify(obj, null, 2) + '\n';
}

function writeDbAtomic(db) {
  writeTextAtomic(ROADMAP_DB_PATH, JSON.stringify(db, null, 2) + '\n');
  writeIndexAtomic(db);

}



function loadFeaturesDb() {
  try {
    const s = readText(FEATURES_DB_PATH);
    const o = JSON.parse(s);
    if (o && typeof o === 'object') {
      o.entries = Array.isArray(o.entries) ? o.entries : [];
      return o;
    }
  } catch { }

  const today = new Date().toISOString().slice(0, 10);
  return {
    project: 'FuckUSurvival',
    version: '0.028',
    updated: today,
    categories: ['features', 'besonderheiten', 'kernstruktur', 'visualisierung', 'andere'],
    statusLegend: {
      done: 'GRÜN = fertig, kein weiterer Code/Work nötig',
      wip: 'GELB = läuft/teilweise, aber noch nicht fertig oder mit Placeholdern',
      placeholder: 'ROT = Template/Placeholder (noch keine echte Implementierung)'
    },
    entries: []
  };
}

function writeFeaturesDbAtomic(featuresDb) {
  const today = new Date().toISOString().slice(0, 10);
  if (!featuresDb || typeof featuresDb !== 'object') featuresDb = loadFeaturesDb();
  featuresDb.entries = Array.isArray(featuresDb.entries) ? featuresDb.entries : [];
  featuresDb.updated = featuresDb.updated || today;
  writeTextAtomic(FEATURES_DB_PATH, JSON.stringify(featuresDb, null, 2) + '\n');
}

function normalizeFeatureEntry(e) {
  const out = Object.assign({}, e || {});
  out.id = String(out.id || '').trim();
  out.title = String(out.title || '').trim();
  out.category = String(out.category || 'andere').toLowerCase().trim();
  out.status = String(out.status || 'placeholder').toLowerCase().trim();
  out.ist = String(out.ist || '');
  out.soll = String(out.soll || '');
  out.notes = String(out.notes || '');
  out.roadmapRefs = Array.isArray(out.roadmapRefs) ? out.roadmapRefs.map(x => String(x).trim()).filter(Boolean) : [];

  const validCats = new Set(['features', 'besonderheiten', 'kernstruktur', 'visualisierung', 'andere']);
  if (!validCats.has(out.category)) out.category = 'andere';

  const validStatus = new Set(['done', 'wip', 'placeholder']);
  if (!validStatus.has(out.status)) out.status = 'placeholder';

  if (!out.id) out.id = 'F_' + Date.now();
  return out;
}

function guessCategoryForRoadmapTask(task) {
  const s = ((task.label || '') + ' ' + (task.description || '') + ' ' + (task.keywords || []).join(' ')).toLowerCase();
  if (s.includes('atlas') || s.includes('texture') || s.includes('sprite') || s.includes('ui asset')) return 'visualisierung';
  if (s.includes('asset editor') || s.includes('asset-index') || s.includes('collision')) return 'besonderheiten';
  if (s.includes('biome') || s.includes('schema') || s.includes('zone') || s.includes('mask') || s.includes('runtime hook') || s.includes('editor')) return 'besonderheiten';
  if (s.includes('server') || s.includes('multiplayer') || s.includes('client')) return 'features';
  if (s.includes('build') || s.includes('compile') || s.includes('release') || s.includes('backup') || s.includes('package')) return 'kernstruktur';
  return 'andere';
}

function upsertFeature(featuresDb, entry) {
  const e = normalizeFeatureEntry(entry);
  featuresDb.entries = Array.isArray(featuresDb.entries) ? featuresDb.entries : [];
  const idx = featuresDb.entries.findIndex(x => String(x.id) === e.id);
  const now = new Date().toISOString();
  e.updatedAt = now;
  if (idx >= 0) {
    const prev = featuresDb.entries[idx] || {};
    e.createdAt = prev.createdAt || now;
    featuresDb.entries[idx] = e;
  } else {
    e.createdAt = now;
    featuresDb.entries.push(e);
  }
  featuresDb.updated = new Date().toISOString().slice(0, 10);
  return e;
}

function autoscanFeaturesMerge(featuresDb, roadmapDb, checklistDoneIds) {
  // Merge/update a small set of deterministic AUTO_* entries based on files.
  // No destructive deletes.
  try {
    const assetsDir = path.resolve(PROJECT_ROOT, 'assets');

    // helpers
    const readJsonIf = (p) => {
      try {
        const s = readText(p);
        return JSON.parse(s);
      } catch {
        return null;
      }
    };

    // Items
    const itemsPath = path.resolve(assetsDir, 'data', 'items.json');
    const itemsObj = readJsonIf(itemsPath);
    if (itemsObj && Array.isArray(itemsObj.items)) {
      const types = {};
      for (const it of itemsObj.items) {
        const t = String(it.type || '?');
        types[t] = (types[t] || 0) + 1;
      }
      upsertFeature(featuresDb, {
        id: 'AUTO_ITEMS',
        category: 'features',
        title: 'Item-Datenbank (Resources/Tools/Weapons/Build/Food/Currency)',
        status: 'wip',
        ist: 'assets/data/items.json existiert (Items + Icons + Tags). Implementationsgrad im Code nicht aus diesem Zip ableitbar.',
        soll: 'Item-System vollständig nutzbar (Inventar, Stacks, Icons, Tool/Weapon-Handling, Build-Placeables, Currency).',
        notes: `Count: ${itemsObj.items.length}
Types: ${Object.keys(types).sort().map(k=>k+'='+types[k]).join(', ')}
File: assets/data/items.json`,
        roadmapRefs: ['NODE_93_SMOKE_TEST_GAME_CLIENT']
      });
    }

    // Recipes
    const recipesPath = path.resolve(assetsDir, 'data', 'recipes.json');
    const recipesObj = readJsonIf(recipesPath);
    if (recipesObj && Array.isArray(recipesObj.recipes)) {
      const tags = {};
      for (const r of recipesObj.recipes) {
        const ts = Array.isArray(r.tags) ? r.tags : [];
        for (const t of ts) tags[t] = (tags[t] || 0) + 1;
      }
      upsertFeature(featuresDb, {
        id: 'AUTO_RECIPES',
        category: 'features',
        title: 'Crafting/Processing Rezepte (Workbench/Smelt/Cook)',
        status: 'wip',
        ist: 'assets/data/recipes.json definiert Outputs/Inputs/Tags (smelt, cook etc.).',
        soll: 'Crafting-UI + Stations (Workbench/Smelter/Campfire) nutzen Rezepte korrekt inkl. Progress/Time/Feedback.',
        notes: `Count: ${recipesObj.recipes.length}
Tags: ${Object.keys(tags).sort().map(k=>k+'='+tags[k]).join(', ')}
File: assets/data/recipes.json`,
        roadmapRefs: ['NODE_93_SMOKE_TEST_GAME_CLIENT']
      });
    }

    // Skills
    const skillsPath = path.resolve(assetsDir, 'data', 'skills.json');
    const skillsObj = readJsonIf(skillsPath);
    if (skillsObj && Array.isArray(skillsObj.skills)) {
      upsertFeature(featuresDb, {
        id: 'AUTO_SKILLS',
        category: 'features',
        title: 'Skill-System (20 Skills, maxLevel=50, linear scaling)',
        status: 'wip',
        ist: 'assets/data/skills.json definiert Skill-Liste + Effects (z.B. miningSpeed, maxHp).',
        soll: 'Skills sind ingame sichtbar, leveln über Aktionen, und wirken direkt auf Gameplay (Speed, Drain, Damage).',
        notes: `Count: ${skillsObj.skills.length}
File: assets/data/skills.json
IDs: ${skillsObj.skills.map(s=>s.id).join(', ')}`,
        roadmapRefs: ['NODE_96_SAVE_LOAD_REGRESSION']
      });
    }

    // Tutorial
    const tutorialPath = path.resolve(assetsDir, 'tutorial', 'tutorial.json');
    const tutObj = readJsonIf(tutorialPath);
    if (tutObj && Array.isArray(tutObj.pages)) {
      upsertFeature(featuresDb, {
        id: 'AUTO_TUTORIAL',
        category: 'features',
        title: 'Tutorial Pages (Movement/Combat/Inventory/Build/Pause)',
        status: 'wip',
        ist: 'assets/tutorial/tutorial.json existiert (Controls-Text).',
        soll: 'Tutorial ist ingame verfügbar, korrekt lokalisiert, und deckt alle relevanten Systeme ab.',
        notes: `Pages: ${tutObj.pages.length}
Titles: ${tutObj.pages.map(p=>p.title).join(', ')}
File: assets/tutorial/tutorial.json`,
        roadmapRefs: ['NODE_93_SMOKE_TEST_GAME_CLIENT']
      });
    }

    // Biomes
    const biomesPath = path.resolve(assetsDir, 'config', 'biomes.json');
    const biomeObj = readJsonIf(biomesPath);
    if (biomeObj && biomeObj.biomes) {
      const names = Object.keys(biomeObj.biomes);
      upsertFeature(featuresDb, {
        id: 'AUTO_BIOMES',
        category: 'besonderheiten',
        title: 'Biome-System (Schema-driven: spawnRules, POIs, roads, edges, masks)',
        status: 'wip',
        ist: 'assets/config/biomes.json enthält BiomeDefs inkl. spawnRules, roadsEnabled, edgeWidthTiles, POI templates, treeJitter/allowTreeOverlap.',
        soll: 'Biome-System ist Editor+Runtime konsistent (Schema round-trip, Preview, Runtime Hook, Save/Load, Korrupthandling).',
        notes: `Biome count: ${names.length}
Biomes: ${names.join(', ')}
File: assets/config/biomes.json`,
        roadmapRefs: ['NODE_10_SCHEMA_MODEL','NODE_50_PREVIEW_FROM_SCHEMA','NODE_60_RUNTIME_HOOK','NODE_96_SAVE_LOAD_REGRESSION']
      });
    }

    // Spawn settings
    const spPath = path.resolve(assetsDir, 'config', 'spawn_settings.json');
    const spObj = readJsonIf(spPath);
    if (spObj && spObj.nodeSpawner && spObj.nodeSpawner.biomes) {
      const bi = Object.keys(spObj.nodeSpawner.biomes);
      upsertFeature(featuresDb, {
        id: 'AUTO_SPAWN_SETTINGS',
        category: 'kernstruktur',
        title: 'Spawn Settings (probability weights per biome)',
        status: 'wip',
        ist: 'assets/config/spawn_settings.json definiert nodeSpawner step/nearbyRadius + weights (tree/rock/iron).',
        soll: 'Spawner nutzt die Settings konsistent mit Biome-System (keine Widersprüche zwischen biomes.json und spawn_settings.json).',
        notes: `Spawner step: ${String(spObj.nodeSpawner.step)}
Biomes: ${bi.length} (${bi.join(', ')})
File: assets/config/spawn_settings.json`,
        roadmapRefs: ['NODE_60_RUNTIME_HOOK']
      });
    }

    // Visual assets: atlas + ui + fonts + terrain
    const atlasPath = path.resolve(assetsDir, 'atlas', 'game.atlas');
    if (fs.existsSync(atlasPath)) {
      const atlasDir = path.dirname(atlasPath);
      const pngs = fs.readdirSync(atlasDir).filter(f => /^game.*\.png$/i.test(f));
      upsertFeature(featuresDb, {
        id: 'AUTO_ATLAS',
        category: 'visualisierung',
        title: 'Texture Atlas (game.atlas + game*.png)',
        status: 'wip',
        ist: 'assets/atlas/game.atlas + textures vorhanden. Pack-Pipeline existiert laut Roadmap (packAtlas).',
        soll: 'Atlas enthält alle Regions die Code/UI referenziert; packAtlas reproduzierbar; keine Missing-Regions im Runtime.',
        notes: `Atlas: assets/atlas/game.atlas
Textures: ${pngs.join(', ')}`,
        roadmapRefs: ['NODE_90_BUILD_AND_RUN','NODE_94_ASSET_AUDIT_REMOVE_DEBUG_ART']
      });
    }

    const uiDir = path.resolve(assetsDir, 'ui');
    if (fs.existsSync(uiDir)) {
      const uiFiles = fs.readdirSync(uiDir).filter(f => /\.png$/i.test(f)).sort();
      upsertFeature(featuresDb, {
        id: 'AUTO_UI',
        category: 'visualisierung',
        title: 'UI Assets (title/mainmenu background/cursor)',
        status: 'wip',
        ist: 'assets/ui/*.png vorhanden (title.png, mainmenu_bg.png, cursor_crosshair.png).',
        soll: 'UI Screens verwenden diese Assets ohne Missing-File-Exceptions und ohne Debug-Placeholder.',
        notes: `UI files: ${uiFiles.join(', ')}
Dir: assets/ui`,
        roadmapRefs: ['NODE_93_SMOKE_TEST_GAME_CLIENT','NODE_94_ASSET_AUDIT_REMOVE_DEBUG_ART']
      });
    }

    const fontPath = path.resolve(assetsDir, 'fonts', 'ui.ttf');
    if (fs.existsSync(fontPath)) {
      upsertFeature(featuresDb, {
        id: 'AUTO_FONT',
        category: 'visualisierung',
        title: 'UI Font (ui.ttf)',
        status: 'wip',
        ist: 'assets/fonts/ui.ttf vorhanden.',
        soll: 'Font wird in UI konsistent genutzt (Glyph coverage, scaling, no fallback warnings).',
        notes: 'File: assets/fonts/ui.ttf',
        roadmapRefs: ['NODE_93_SMOKE_TEST_GAME_CLIENT']
      });
    }

    const terrainDir = path.resolve(assetsDir, 'terrain');
    if (fs.existsSync(terrainDir)) {
      const files = fs.readdirSync(terrainDir).filter(f => /\.png$/i.test(f)).sort();
      upsertFeature(featuresDb, {
        id: 'AUTO_TERRAIN',
        category: 'visualisierung',
        title: 'Terrain Tilesets (grass/dirt/flooded etc.)',
        status: 'wip',
        ist: 'assets/terrain/*.png vorhanden.',
        soll: 'Terrain tiles werden korrekt im World-Render genutzt (tiling, transitions, no seams).',
        notes: `Terrain sets: ${files.join(', ')}
Dir: assets/terrain`,
        roadmapRefs: ['NODE_93_SMOKE_TEST_GAME_CLIENT']
      });
    }

    // Start scripts
    const scripts = fs.readdirSync(PROJECT_ROOT).filter(f => /^START_\d+_.*\.bat$/i.test(f)).sort();
    upsertFeature(featuresDb, {
      id: 'AUTO_START_SCRIPTS',
      category: 'kernstruktur',
      title: 'Start-Skripte (Roadmap/AssetIndex/Game/BiomeEditor/AssetEditor)',
      status: 'wip',
      ist: 'START_*.bat existieren und rufen Gradle tasks / roadmap server auf.',
      soll: 'Alle Start-Skripte laufen in einer frischen unzip-Umgebung ohne manuelle Pfadfixes.',
      notes: `Files: ${scripts.join(', ')}`,
      roadmapRefs: ['NODE_90_BUILD_AND_RUN','NODE_92_SMOKE_TEST_ASSET_EDITOR','NODE_93_SMOKE_TEST_GAME_CLIENT']
    });

    // Settings (may be non-json)
    const settingsPath = path.resolve(assetsDir, 'settings.json');
    if (fs.existsSync(settingsPath)) {
      let raw = '';
      try { raw = readText(settingsPath).trim(); } catch { raw = ''; }
      upsertFeature(featuresDb, {
        id: 'AUTO_SETTINGS',
        category: 'kernstruktur',
        title: 'Settings Persistenz (Audio Volumes)',
        status: 'wip',
        ist: 'assets/settings.json existiert; Format ist nicht garantiert JSON (Preferences/LibGDX oder Bug).',
        soll: 'Options/Audio Settings persistent und robust (gültiges Format, fallback bei Parse errors).',
        notes: `File: assets/settings.json
Raw snippet: ${raw.slice(0, 160)}`,
        roadmapRefs: ['NODE_93_SMOKE_TEST_GAME_CLIENT']
      });
    }
  } catch {
    // ignore scan failures
  }

  // Roadmap nodes mirror entries (non-destructive: update status from checklist)
  const tasks = Array.isArray(roadmapDb.tasks) ? roadmapDb.tasks : [];
  for (const t of tasks) {
    const rid = String(t.id || '').trim();
    if (!rid) continue;
    const entryId = 'RM_' + rid;
    const status = checklistDoneIds.has(rid) ? 'done' : 'placeholder';
    const title = rid + (t.label ? ' – ' + t.label : '');
    upsertFeature(featuresDb, {
      id: entryId,
      category: guessCategoryForRoadmapTask(t),
      title,
      status,
      ist: String(t.description || ''),
      soll: status === 'done' ? 'Node abgeschlossen (Additional Tasks = done) und in CHECKLIST_TODO.md abgehakt.' : 'Node noch offen (siehe Roadmap + Additional Tasks).',
      notes: 'Keywords: ' + (Array.isArray(t.keywords) ? t.keywords.join(', ') : ''),
      roadmapRefs: [rid]
    });
  }

  featuresDb.updated = new Date().toISOString().slice(0, 10);
  return featuresDb;
}

function loadAll() {
  const dbText = readText(ROADMAP_DB_PATH);
  const db = JSON.parse(dbText);

  let checklistText = '';
  try { checklistText = readText(CHECKLIST_PATH); } catch { checklistText = ''; }
  const checklist = parseChecklist(checklistText);

  let stateText = '';
  try { stateText = readText(STATE_PATH); } catch { stateText = ''; }
  const stateObj = safeJsonParse(stateText) || {};

  return {
    db,
    checklist: {
      raw: checklistText,
      allIds: [...checklist.allIds].sort(),
      doneIds: [...checklist.doneIds].sort(),
      allSubIds: [...checklist.allSubIds].sort(),
      doneSubIds: [...checklist.doneSubIds].sort()
    },
    state: {
      raw: stateText,
      NEXT_NODE: stateObj.NEXT_NODE || null,
      LAST_COMPLETED_NODE: stateObj.LAST_COMPLETED_NODE || null
    }
  };
}

function json(res, status, obj) {
  const body = JSON.stringify(obj);
  res.writeHead(status, {
    'Content-Type': 'application/json; charset=utf-8',
    'Cache-Control': 'no-store'
  });

  res.end(body);
}

function text(res, status, body, contentType = 'text/plain; charset=utf-8') {
  res.writeHead(status, {
    'Content-Type': contentType,
    'Cache-Control': 'no-store'
  });
  res.end(body);
}

function readBody(req) {
  return new Promise((resolve, reject) => {
    let data = '';
    req.on('data', chunk => {
      data += chunk;
      if (data.length > 2 * 1024 * 1024) {
        reject(new Error('Body too large'));
        req.destroy();
      }
    });
    req.on('end', () => resolve(data));
    req.on('error', reject);
  });
}

function mimeTypeFor(filePath) {
  const ext = path.extname(filePath).toLowerCase();
  if (ext === '.html') return 'text/html; charset=utf-8';
  if (ext === '.js') return 'text/javascript; charset=utf-8';
  if (ext === '.css') return 'text/css; charset=utf-8';
  if (ext === '.json') return 'application/json; charset=utf-8';
  if (ext === '.svg') return 'image/svg+xml';
  if (ext === '.png') return 'image/png';
  if (ext === '.jpg' || ext === '.jpeg') return 'image/jpeg';
  if (ext === '.webp') return 'image/webp';
  return 'application/octet-stream';
}

function serveStatic(req, res, pathname) {
  const safePath = path.normalize(path.join(PROJECT_ROOT, pathname));
  if (!safePath.startsWith(PROJECT_ROOT)) {
    return text(res, 403, 'Forbidden');
  }

  let filePath = safePath;
  if (fs.existsSync(filePath) && fs.statSync(filePath).isDirectory()) {
    filePath = path.join(filePath, 'index.html');
  }

  if (!fs.existsSync(filePath) || !fs.statSync(filePath).isFile()) {
    return text(res, 404, 'Not found');
  }

  const ct = mimeTypeFor(filePath);
  try {
    const buf = fs.readFileSync(filePath);
    res.writeHead(200, { 'Content-Type': ct, 'Cache-Control': 'no-store' });
    res.end(buf);
  } catch (e) {
    text(res, 500, String(e));
  }
}

function allAdditionalDone(task) {
  const subs = Array.isArray(task.additionalTasks) ? task.additionalTasks : [];
  return !subs.some(st => !st.done);
}

async function handleApi(req, res, pathname) {
  if (req.method === 'GET' && pathname === '/api/ping') {
    return json(res, 200, { ok: true, server: 'fuckusurvival-roadmap', port: PORT });
  }

  if (req.method === 'GET' && pathname === '/api/data') {
    try {
      const data = loadAll();
      return json(res, 200, { ok: true, ...data });
    } catch (e) {
      return json(res, 500, { ok: false, error: String(e) });
    }
  }

  // Toggle node completion (blocked if subtasks not done)
  if (req.method === 'POST' && pathname === '/api/task') {
    try {
      const bodyText = await readBody(req);
      const payload = safeJsonParse(bodyText);
      if (!payload || typeof payload !== 'object') return json(res, 400, { ok: false, error: 'Invalid JSON' });

      const id = String(payload.id || '').trim();
      const done = !!payload.done;
      if (!/^NODE_\d+_[A-Z0-9_]+$/.test(id)) return json(res, 400, { ok: false, error: 'Invalid id' });

      const db = JSON.parse(readText(ROADMAP_DB_PATH));
      const tasks = db.tasks || [];
      const task = tasks.find(t => t.id === id);
      if (!task) return json(res, 400, { ok: false, error: 'Task id not found in ROADMAP_DB.json' });

      if (done && !allAdditionalDone(task)) {
        return json(res, 400, { ok: false, error: 'Node has additional tasks not done. Complete them before marking node done.' });
      }

      let checklistText = '';
      try { checklistText = readText(CHECKLIST_PATH); } catch { checklistText = ''; }
      const parsed = parseChecklist(checklistText);
      const doneSet = new Set(parsed.doneIds);
      if (done) doneSet.add(id); else doneSet.delete(id);

      // Ensure checklist contains exactly DB tasks (and their subtasks)
      const newChecklist = buildChecklistFromDb(db, doneSet);
      writeTextAtomic(CHECKLIST_PATH, newChecklist);

      // Update STATE.json NEXT/LAST
      let stateText = '';
      try { stateText = readText(STATE_PATH); } catch { stateText = ''; }
      const nextLast = computeNextAndLast(tasks, doneSet);
      const newState = buildUpdatedState(stateText, db, nextLast);
      writeTextAtomic(STATE_PATH, newState);

      const data = loadAll();
      return json(res, 200, { ok: true, updated: { id, done }, ...data });
    } catch (e) {
      return json(res, 500, { ok: false, error: String(e) });
    }
  }

  // Add a new subtask to a node
  if (req.method === 'POST' && pathname === '/api/node/additional') {
    try {
      const bodyText = await readBody(req);
      const payload = safeJsonParse(bodyText);
      if (!payload || typeof payload !== 'object') return json(res, 400, { ok: false, error: 'Invalid JSON' });

      const nodeId = String(payload.nodeId || '').trim();
      const textIn = sanitizeAdditionalText(payload.text || '');
      if (!/^NODE_\d+_[A-Z0-9_]+$/.test(nodeId)) return json(res, 400, { ok: false, error: 'Invalid nodeId' });
      if (!textIn) return json(res, 400, { ok: false, error: 'Empty text' });

      const db = JSON.parse(readText(ROADMAP_DB_PATH));
      const tasks = db.tasks || [];
      const task = tasks.find(t => t.id === nodeId);
      if (!task) return json(res, 400, { ok: false, error: 'Node not found in ROADMAP_DB.json' });

      task.additionalTasks = Array.isArray(task.additionalTasks) ? task.additionalTasks : [];
      const sid = nextAdditionalId(nodeId, task);
      task.additionalTasks.push({ id: sid, text: textIn, done: false, createdAt: new Date().toISOString() });

      // Load checklist done nodes, auto-uncheck this node if it was done
      let checklistText = '';
      try { checklistText = readText(CHECKLIST_PATH); } catch { checklistText = ''; }
      const parsed = parseChecklist(checklistText);
      const doneSet = new Set(parsed.doneIds);
      if (doneSet.has(nodeId)) doneSet.delete(nodeId);

      writeDbAtomic(db);
      const newChecklist = buildChecklistFromDb(db, doneSet);
      writeTextAtomic(CHECKLIST_PATH, newChecklist);

      let stateText = '';
      try { stateText = readText(STATE_PATH); } catch { stateText = ''; }
      const nextLast = computeNextAndLast(tasks, doneSet);
      const newState = buildUpdatedState(stateText, db, nextLast);
      writeTextAtomic(STATE_PATH, newState);

      const data = loadAll();
      return json(res, 200, { ok: true, added: { nodeId, subtaskId: sid }, ...data });
    } catch (e) {
      return json(res, 500, { ok: false, error: String(e) });
    }
  }



  // Create a new node
  if (req.method === 'POST' && pathname === '/api/node/new') {
    try {
      const bodyText = await readBody(req);
      const payload = safeJsonParse(bodyText);
      if (!payload || typeof payload !== 'object') return json(res, 400, { ok: false, error: 'Invalid JSON' });

      const label = String(payload.label || '').trim();
      if (!label) return json(res, 400, { ok: false, error: 'Empty label' });

      const importance = String(payload.importance || 'wichtig').trim();
      const description = String(payload.description || '').trim();
      const dependsOn = Array.isArray(payload.dependsOn) ? payload.dependsOn.map(x => String(x).trim()).filter(Boolean) : [];
      const keywords = Array.isArray(payload.keywords) ? payload.keywords.map(x => String(x).trim()).filter(Boolean) : [];
      const insertAfter = String(payload.insertAfter || '').trim();

      const db = JSON.parse(readText(ROADMAP_DB_PATH));
      const tasks = db.tasks || [];

      const num = nextNodeNumber(tasks);
      const slug = slugFromLabel(label);
      let id = `NODE_${num}_${slug}`;
      if (tasks.some(t => t.id === id)) {
        let k = 2;
        while (tasks.some(t => t.id === `${id}_${k}`)) k++;
        id = `${id}_${k}`;
      }

      const node = {
        id,
        label,
        description,
        importance,
        timeline: 99999,
        dependsOn,
        keywords,
        additionalTasks: []
      };

      const ordered = [...tasks].sort((a, b) => (a.timeline || 0) - (b.timeline || 0));
      let idx = ordered.length;
      if (insertAfter) {
        const pos = ordered.findIndex(t => t.id === insertAfter);
        if (pos >= 0) idx = pos + 1;
      }
      ordered.splice(idx, 0, node);
      db.tasks = ordered;
      renumberTimeline(db.tasks);

      writeDbAtomic(db);

      let checklistText = '';
      try { checklistText = readText(CHECKLIST_PATH); } catch { checklistText = ''; }
      const parsed = parseChecklist(checklistText);
      const doneSet = new Set(parsed.doneIds);
      doneSet.delete(id);

      const newChecklist = buildChecklistFromDb(db, doneSet);
      writeTextAtomic(CHECKLIST_PATH, newChecklist);

      let stateText = '';
      try { stateText = readText(STATE_PATH); } catch { stateText = ''; }
      const nextLast = computeNextAndLast(db.tasks, doneSet);
      const newState = buildUpdatedState(stateText, db, nextLast);
      writeTextAtomic(STATE_PATH, newState);

      const data = loadAll();
      return json(res, 200, { ok: true, created: { id }, ...data });
    } catch (e) {
      return json(res, 500, { ok: false, error: String(e) });
    }
  }

  // Toggle a subtask (if undone -> node becomes undone)
  if (req.method === 'POST' && pathname === '/api/node/additional/toggle') {
    try {
      const bodyText = await readBody(req);
      const payload = safeJsonParse(bodyText);
      if (!payload || typeof payload !== 'object') return json(res, 400, { ok: false, error: 'Invalid JSON' });

      const nodeId = String(payload.nodeId || '').trim();
      const subtaskId = String(payload.subtaskId || '').trim();
      const done = !!payload.done;
      if (!/^NODE_\d+_[A-Z0-9_]+$/.test(nodeId)) return json(res, 400, { ok: false, error: 'Invalid nodeId' });
      if (!/^NODE_\d+_[A-Z0-9_]+__AT_\d{3}$/.test(subtaskId)) return json(res, 400, { ok: false, error: 'Invalid subtaskId' });

      const db = JSON.parse(readText(ROADMAP_DB_PATH));
      const tasks = db.tasks || [];
      const task = tasks.find(t => t.id === nodeId);
      if (!task) return json(res, 400, { ok: false, error: 'Node not found in ROADMAP_DB.json' });

      task.additionalTasks = Array.isArray(task.additionalTasks) ? task.additionalTasks : [];
      const st = task.additionalTasks.find(x => String(x.id) === subtaskId);
      if (!st) return json(res, 400, { ok: false, error: 'Subtask not found' });
      st.done = done;

      let checklistText = '';
      try { checklistText = readText(CHECKLIST_PATH); } catch { checklistText = ''; }
      const parsed = parseChecklist(checklistText);
      const doneSet = new Set(parsed.doneIds);

      if (!done && doneSet.has(nodeId)) doneSet.delete(nodeId);

      writeDbAtomic(db);
      const newChecklist = buildChecklistFromDb(db, doneSet);
      writeTextAtomic(CHECKLIST_PATH, newChecklist);

      let stateText = '';
      try { stateText = readText(STATE_PATH); } catch { stateText = ''; }
      const nextLast = computeNextAndLast(tasks, doneSet);
      const newState = buildUpdatedState(stateText, db, nextLast);
      writeTextAtomic(STATE_PATH, newState);

      const data = loadAll();
      return json(res, 200, { ok: true, updated: { nodeId, subtaskId, done }, ...data });
    } catch (e) {
      return json(res, 500, { ok: false, error: String(e) });
    }
  }



  // -------- FEATURES DB API --------
  if (req.method === 'GET' && pathname === '/api/features') {
    try {
      const features = loadFeaturesDb();
      // include minimal roadmap snapshot for cross-linking
      const db = JSON.parse(readText(ROADMAP_DB_PATH));

      let checklistText = '';
      try { checklistText = readText(CHECKLIST_PATH); } catch { checklistText = ''; }
      const parsed = parseChecklist(checklistText);
      const doneIds = new Set(parsed.doneIds);

      return json(res, 200, {
        ok: true,
        features,
        roadmap: {
          version: db.version,
          tasks: db.tasks || [],
          doneIds: [...doneIds]
        }
      });
    } catch (e) {
      return json(res, 500, { ok: false, error: String(e) });
    }
  }

  if (req.method === 'POST' && pathname === '/api/features/upsert') {
    try {
      const bodyText = await readBody(req);
      const payload = safeJsonParse(bodyText);
      if (!payload || typeof payload !== 'object') return json(res, 400, { ok: false, error: 'Invalid JSON' });

      const entry = normalizeFeatureEntry(payload.entry);
      const features = loadFeaturesDb();
      upsertFeature(features, entry);
      writeFeaturesDbAtomic(features);

      return json(res, 200, { ok: true, features });
    } catch (e) {
      return json(res, 500, { ok: false, error: String(e) });
    }
  }

  if (req.method === 'POST' && pathname === '/api/features/delete') {
    try {
      const bodyText = await readBody(req);
      const payload = safeJsonParse(bodyText);
      if (!payload || typeof payload !== 'object') return json(res, 400, { ok: false, error: 'Invalid JSON' });

      const id = String(payload.id || '').trim();
      if (!id) return json(res, 400, { ok: false, error: 'Missing id' });

      const features = loadFeaturesDb();
      features.entries = Array.isArray(features.entries) ? features.entries : [];
      features.entries = features.entries.filter(x => String(x.id) !== id);
      features.updated = new Date().toISOString().slice(0, 10);
      writeFeaturesDbAtomic(features);

      return json(res, 200, { ok: true, features });
    } catch (e) {
      return json(res, 500, { ok: false, error: String(e) });
    }
  }

  if (req.method === 'POST' && pathname === '/api/features/importRoadmap') {
    try {
      const features = loadFeaturesDb();

      const rdb = JSON.parse(readText(ROADMAP_DB_PATH));
      let checklistText = '';
      try { checklistText = readText(CHECKLIST_PATH); } catch { checklistText = ''; }
      const parsed = parseChecklist(checklistText);
      const doneIds = new Set(parsed.doneIds);

      // add/update RM_ entries
      const tasks = Array.isArray(rdb.tasks) ? rdb.tasks : [];
      for (const t of tasks) {
        const rid = String(t.id || '').trim();
        if (!rid) continue;
        const entryId = 'RM_' + rid;
        const status = doneIds.has(rid) ? 'done' : 'placeholder';
        upsertFeature(features, {
          id: entryId,
          category: guessCategoryForRoadmapTask(t),
          title: rid + (t.label ? ' – ' + t.label : ''),
          status,
          ist: String(t.description || ''),
          soll: status === 'done' ? 'Node abgeschlossen (Additional Tasks = done) und in CHECKLIST_TODO.md abgehakt.' : 'Node noch offen (siehe Roadmap + Additional Tasks).',
          notes: 'Keywords: ' + (Array.isArray(t.keywords) ? t.keywords.join(', ') : ''),
          roadmapRefs: [rid]
        });
      }

      writeFeaturesDbAtomic(features);
      return json(res, 200, { ok: true, features });
    } catch (e) {
      return json(res, 500, { ok: false, error: String(e) });
    }
  }

  if (req.method === 'POST' && pathname === '/api/features/autoscan') {
    try {
      const features = loadFeaturesDb();

      const rdb = JSON.parse(readText(ROADMAP_DB_PATH));
      let checklistText = '';
      try { checklistText = readText(CHECKLIST_PATH); } catch { checklistText = ''; }
      const parsed = parseChecklist(checklistText);
      const doneIds = new Set(parsed.doneIds);

      const merged = autoscanFeaturesMerge(features, rdb, doneIds);
      writeFeaturesDbAtomic(merged);

      return json(res, 200, { ok: true, features: merged });
    } catch (e) {
      return json(res, 500, { ok: false, error: String(e) });
    }
  }

  // Rebuild checklist + state from DB (keeps subtasks)
  if (req.method === 'POST' && pathname === '/api/export') {
    try {
      const db = JSON.parse(readText(ROADMAP_DB_PATH));
      const tasks = db.tasks || [];

      let checklistText = '';
      try { checklistText = readText(CHECKLIST_PATH); } catch { checklistText = ''; }
      const parsed = parseChecklist(checklistText);
      const doneSet = new Set(parsed.doneIds);

      const newChecklist = buildChecklistFromDb(db, doneSet);
      writeTextAtomic(CHECKLIST_PATH, newChecklist);

      let stateText = '';
      try { stateText = readText(STATE_PATH); } catch { stateText = ''; }
      const nextLast = computeNextAndLast(tasks, doneSet);
      const newState = buildUpdatedState(stateText, db, nextLast);
      writeTextAtomic(STATE_PATH, newState);

      const data = loadAll();
      return json(res, 200, { ok: true, written: ['ROADMAP_DB.json', 'CHECKLIST_TODO.md', 'STATE.json'], ...data });
    } catch (e) {
      return json(res, 500, { ok: false, error: String(e) });
    }
  }

  return json(res, 404, { ok: false, error: 'Unknown API route' });
}

const server = http.createServer(async (req, res) => {
  const parsedUrl = new URL(req.url, 'http://localhost');
  const pathname = decodeURIComponent(parsedUrl.pathname || '/');

  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET,POST,OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
  if (req.method === 'OPTIONS') {
    res.writeHead(204);
    return res.end();
  }

  if (pathname.startsWith('/api/')) {
    return handleApi(req, res, pathname);
  }

  if (pathname === '/' || pathname === '') {
    res.writeHead(302, { Location: '/roadmap/index.html' });
    return res.end();
  }

  return serveStatic(req, res, pathname);
});

server.listen(PORT, HOST, () => {
  console.log(`[Roadmap] Serving ${PROJECT_ROOT}`);
  console.log(`[Roadmap] http://localhost:${PORT}/roadmap/index.html`);
  console.log(`[Roadmap] http://127.0.0.1:${PORT}/roadmap/index.html`);
  console.log('[Roadmap] Writes enabled for ROADMAP_DB.json + CHECKLIST_TODO.md + STATE.json + FEATURES_DB.json');
});

server.on('error', (err) => {
  if (err && err.code === 'EADDRINUSE') {
    console.error(`[Roadmap] ERROR: Port ${PORT} is already in use.`);
    console.error('[Roadmap] Close the other server (python http.server etc.) and start again.');
    process.exit(1);
  }
  console.error('[Roadmap] Server error:', err);
  process.exit(1);
});
