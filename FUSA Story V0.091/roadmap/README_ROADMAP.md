# Roadmap UI (offline, lokal, editierbar)

## Start
- Öffne ein Terminal im Projektordner `FuckUSurvival/`.
- Windows (empfohlen): Doppelklick `roadmap\\start_roadmap_windows.bat`
  - nutzt **Node.js** (falls vorhanden) für **Write-Back** in `STATE.json` + `CHECKLIST_TODO.md`
  - Fallback: Python-Webserver (read-only)
- Manuell (Node): `node roadmap/server.js`
- Browser: `http://localhost:8011/roadmap/index.html`

## Workflow
- Mit Node-Server: Checkbox „done“ schreibt sofort in:
  - `CHECKLIST_TODO.md`
  - `STATE.json` (NEXT_NODE / LAST_COMPLETED_NODE)
- Ohne Node (read-only): UI kann weiterhin togglen, aber nur lokal im Browser; dann Export nutzen.

## Regeln
- Tasks in der Roadmap sind identisch zu den IDs in `CHECKLIST_TODO.md`.
- Guardrails/Failsafe stehen oben als „Datenbank“.
- Views: **Kategorien**, **Timeline**, **Diagramm (Dependencies)**, **Node-Details (Additional Tasks)**.

## Sicherheit / Scope
- Der Server akzeptiert nur IDs, die in `roadmap/ROADMAP_DB.json` existieren.
- Er schreibt ausschließlich `STATE.json` und `CHECKLIST_TODO.md` im Projektroot.
