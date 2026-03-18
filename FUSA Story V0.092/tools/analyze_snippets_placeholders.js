const fs = require('fs');

const path = 'assets/data/zqs/text_snippets_de_DE_v1.json';
const j = JSON.parse(fs.readFileSync(path, 'utf8'));

const placeholders = new Set();
const examples = new Map();

for (const sn of (j.snippets || [])) {
  const id = sn.snippet_id || '(missing_id)';
  const t = sn.text_template || '';
  for (const m of t.matchAll(/\{[a-zA-Z0-9_]+\}/g)) {
    const ph = m[0];
    placeholders.add(ph);
    if (!examples.has(ph)) examples.set(ph, []);
    const arr = examples.get(ph);
    if (arr.length < 8) arr.push(id);
  }
}

const sorted = [...placeholders].sort();
console.log('PLACEHOLDERS (' + sorted.length + '):');
for (const ph of sorted) {
  console.log(' -', ph, 'examples:', (examples.get(ph) || []).join(', '));
}
