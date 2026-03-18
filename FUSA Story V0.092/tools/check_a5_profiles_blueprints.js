const fs = require('fs');

const bp = JSON.parse(fs.readFileSync('assets/data/zqs/blueprints_v1.json', 'utf8'));
const rp = JSON.parse(fs.readFileSync('assets/data/zqs/reward_profiles_v1.json', 'utf8'));

const profiles = new Map((rp.profiles || []).map(p => [p.rewardProfileId, p]));
const knownFormula = new Set(['collect', 'deliver', 'craft', 'find', 'escort']);
const knownText = new Set(['item', 'currency', 'mixed', 'failed']);

let ok = true;
function fail(msg) {
  ok = false;
  console.log('FAIL:', msg);
}

// Validate profiles
if (!Array.isArray(rp.profiles) || rp.profiles.length === 0) {
  fail('reward_profiles_v1.json: profiles[] missing/empty');
} else {
  const seen = new Set();
  for (const p of rp.profiles) {
    const id = (p && p.rewardProfileId) ? String(p.rewardProfileId) : '';
    if (!id) { fail('profile missing rewardProfileId'); continue; }
    if (seen.has(id)) fail(`duplicate rewardProfileId '${id}'`);
    seen.add(id);

    const ft = (p.rewardFormulaType ?? '').toString().trim().toLowerCase();
    if (!knownFormula.has(ft)) fail(`profile '${id}': invalid rewardFormulaType '${p.rewardFormulaType}'`);

    const tm = (p.rewardTextMode ?? '').toString().trim().toLowerCase();
    if (!knownText.has(tm)) fail(`profile '${id}': invalid rewardTextMode '${p.rewardTextMode}'`);
  }
}

// Validate blueprints
if (!Array.isArray(bp.blueprints) || bp.blueprints.length === 0) {
  fail('blueprints_v1.json: blueprints[] missing/empty');
} else {
  for (const b of bp.blueprints) {
    const bid = (b && b.blueprintId) ? String(b.blueprintId) : '';
    if (!bid) { fail('blueprint missing blueprintId'); continue; }

    const rpid = (b.rewardProfileId ?? '').toString().trim();
    if (!rpid) { fail(`blueprint '${bid}': missing rewardProfileId`); continue; }
    if (!profiles.has(rpid)) fail(`blueprint '${bid}': references unknown rewardProfileId '${rpid}'`);
  }
}

if (ok) {
  console.log('OK: profiles+blueprints consistent (rewardProfileId resolves; formula/text modes are valid).');
  process.exit(0);
} else {
  process.exit(1);
}
