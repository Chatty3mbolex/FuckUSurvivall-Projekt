const fs = require('fs');

const cat = JSON.parse(fs.readFileSync('assets/data/zqs/catalog_runtime_v1.json', 'utf8'));

let ok = true;
function fail(msg) { ok = false; console.log('FAIL:', msg); }

function checkEntry(kind, e) {
  if (e == null || typeof e !== 'object') { fail(`${kind}: entry is not object`); return; }
  const hasId = Object.prototype.hasOwnProperty.call(e, 'id');
  if (!hasId) fail(`${kind}: entry missing id`);

  const name = (e.name ?? '').toString();
  if (!name) fail(`${kind}: entry '${e.id}' missing/empty name`);

  const vc = e.valueCopper;
  if (typeof vc !== 'number') fail(`${kind}: entry '${e.id}' missing valueCopper`);

  if (!Array.isArray(e.tags)) fail(`${kind}: entry '${e.id}' tags must be array`);

  const sl = e.slIdMax;
  if (typeof sl !== 'number') fail(`${kind}: entry '${e.id}' missing slIdMax`);

  const kr = e.knownRequired;
  if (typeof kr !== 'boolean') fail(`${kind}: entry '${e.id}' missing knownRequired`);
}

const requiredKinds = ['pois', 'npcs', 'regions'];
for (const k of requiredKinds) {
  if (!Array.isArray(cat[k])) fail(`catalog: missing array '${k}'`);
}

for (const k of ['resources','items','harvestables','livings','pois','npcs','regions']) {
  if (!Array.isArray(cat[k])) continue;
  for (const e of cat[k]) checkEntry(k, e);
}

if (ok) {
  console.log('OK: catalog_runtime_v1.json meets A5 minimum fields for entries (including pois/npcs/regions).');
  process.exit(0);
} else {
  process.exit(1);
}
