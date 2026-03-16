# ZQS_REFRESH_CONTEXT.md (Read-this-to-resume)

> Zweck: **Ein einziges Dokument**, das nach einem Context-Reset gelesen wird, damit die Umsetzung exakt an der richtigen Stelle weitergeht.
> 
> Wenn du später sagst: **"lies das"**, dann ist damit genau diese Datei gemeint.

---

## 0) HARTE REGELN (nicht verhandelbar)
- **NICHT** kompilieren.
- **NICHT** starten.
- **NICHT** debuggen.
- **NICHT** committen.
- **Keine Fallback-Systeme** („läuft trotzdem“) – fehlende DB/Keys müssen als Fehler sichtbar werden.
- **Pro DB genau eine JSON** unter `assets/data/zqs/`.
- Fehlendes Konzept muss als `// DUMMY SPACE (NNN) ...` im Code markiert und in `DUMMIES_ZQS.md` gelistet werden.
- Konflikte: **Flow 4 Master Alignment** hat letzte Autorität (`flow 4/zqs_master_alignment_v_1_1.md`).

---

## 1) ZIEL (in einem Satz)
ZQS (ZufallsQuestSystem) als **eigenes, persistentes Modul** implementieren; `WanderQuestGuySystem` ist **nur Docking-NPC** (Spawn/Move/Popup) und delegiert vollständig an ZQS.

---

## 2) KANONISCHE KONZEPTQUELLEN (nur diese)
Ordner: `.../flow 4/`
- `WanderQuestGuy_ZQS_WorkflowPlan.md` (Phasen/Resume-Regeln – bindend)
- `zqs_master_alignment_v_1_1.md` (Master, hat Vorrang)
- `zqs_wander_quest_guy_plan1.0.md` (Systemplan)
- `zqs_reward_logik_referenz_v1.md` (Rewards)
- `zqs_quest_inhaltslogik_referenz_v1.md` (Freigaben/Subtypen)
- `zqs_snippetkatalog_v1.md` + `zqs_textpool_szenarien_analyse1.md` + `zqs_textpool_starter_v1.md` (Text/Snippets)

---

## 3) PROJEKT-STATUSDATEIEN (das ist die Wahrheit im Repo)
Lies in dieser Reihenfolge:
1. `READ_FIRST_ZQS.md` (Regeln/Referenzen)
2. `STATE_ZQS.json` (aktueller Schritt + next_step)
3. `CHECKLIST_ZQS.md` (Fortschritt)
4. `ERRORS.md` (Workflow-Abweichungen)
5. `DUMMIES_ZQS.md` (alles, was noch fehlt)
6. Danach die Phase-spezifischen Spezifikationen:
   - `MODEL_ZQS.md`, `SAVE_ZQS.md`, `GENERATOR_ZQS.md`, `TEXT_ZQS.md`, `FILES_ZQS.md`, `IMPLEMENT_ZQS.md`, `FINALCHECK_ZQS.md`

Hinweis: `STATE_ZQS.json.next_step` steht aktuell auf **PHASE_08/FinalCheck**, aber FinalCheck muss **FAIL** sein (siehe Abschnitt 7), weil zentrale Sollpunkte fehlen.

---

## 4) WAS WURDE BEREITS GEMACHT (IST-Stand)

### 4.1 Workflow-/Dokurahmen
- Workflow-Rootdateien angelegt: `ERRORS.md`, `STATE_ZQS.json`, `CHECKLIST_ZQS.md`, `READ_FIRST_ZQS.md`, `DUMMIES_ZQS.md`, usw.
- PHASE-Spezifikationen geschrieben: `MODEL_ZQS.md`, `SAVE_ZQS.md`, `GENERATOR_ZQS.md`, `TEXT_ZQS.md`, `FILES_ZQS.md`, `IMPLEMENT_ZQS.md`, `FINALCHECK_ZQS.md`.

### 4.2 ZQS DB-Assets (kanonisch; kein Fallback)
Unter `assets/data/zqs/` (je DB 1 JSON):
- `catalog_runtime_v1.json` (befüllt)
- `blueprints_v1.json` (mind. collect resource + collect harvestable)
- `reward_profiles_v1.json`
- `text_snippets_de_DE_v1.json` (mind. harvestable assignment middle snippet vorhanden)

### 4.3 Save-Struktur (ZQS-Block)
- `ZqsSaveBlock` + `ZqsSaveIO` implementiert.
- `SaveManager` schreibt/liest Root-Key **"zqs"**.
- `GameScreen` hält ein `ZqsSaveBlock zqsSave` und reicht es SaveManager rein.
- OfferBuffer ist **nicht persistent** (Master).

### 4.4 Textsystem (produktiv in quest/zqs/text)
Neu unter `core/.../quest/zqs/text/`:
- `SnippetDef`, `SnippetPool`, `ConversationContext`, `ZqsSnippetLoader`, `ZqsTextAssembler`
- Strikte Filterselektion, **keine Fallbacks**.

### 4.5 Generatorstruktur (teilweise)
Neu:
- Runtime Typen/Blöcke: `QuestType`, `QuestSubtype`, `QuestStatus`, `RewardState`, `RewardTextMode`, `TargetBlock`, `RewardBlock`
- Generator scaffolding: `QuestBlueprintDef`, `RewardCalculator`

### 4.6 History
- Runtime History: `quest/zqs/history/QuestHistoryIndex`
- Save<->Runtime Codec: `QuestHistoryCodec`

### 4.7 QuestLog View-Brücke
- `QuestLog.Entry` um `logbookEntryNr` + `finalStatus` erweitert.
- `SaveManager.loadSlot(...)` baut QuestLog **bevorzugt aus ZQS Logbook** auf (wenn vorhanden), damit die UI nicht auf Legacy hängt.

### 4.8 Docking (Step7)
- Neue Fassade: `quest/zqs/runtime/ZqsRuntime`
- Neue Dock-Schicht: `quest/zqs/dock/WanderQuestGuyDock`
- `GameScreen` erstellt `ZqsRuntime` + `WanderQuestGuyDock` und bindet ihn an `WanderQuestGuySystem`.
- `WanderQuestGuySystem` redet **nur** mit Dock.

Wichtig: In `ZqsRuntime` hängt aktuell noch ein **legacy** `ZqsSystem` als Übergangsbackend (Generator-Monolith), bis generator/* Pipeline fertig ist.

---

## 5) WAS NOCH FEHLT (SOLL-Lücken, grob priorisiert)
**Blocker / Muss als nächstes:**
1) **(702) Accept Persistenz:** `ZqsRuntime.acceptOffer` muss beim Accept in `ZqsSaveBlock.playerQuestDb` + `zqs.logbook` + `zqs.questHistoryIndex` schreiben (inkl. logbookNr Vergabe).
2) **(701) Runtime Import aus Save:** `ZqsRuntime.bind(...)` muss `knowledge/playerQuestDb/logbook/history` aus `ZqsSaveBlock` in Runtime-Modelle importieren.
3) **Generator „richtig“ (Master/GENERATOR_ZQS.md):** Eligibility/Filterkette/RepeatRules + deterministische QuestID (kein Hash-Mix).
4) **RewardProfiles Integration (403):** reward_text_mode + Distribution (item/currency/mixed) aus `reward_profiles_v1.json`.
5) **catalog-Modul sauber:** eigenes Package `quest/zqs/catalog/*` statt `runtime/ZqsDb` als Sammelstelle.

---

## 6) EXAKTE DUMMY-LISTE (wo nachzuschauen)
Die vollständige Dummy-Liste steht in: **`DUMMIES_ZQS.md`**.
Wichtigste offene Dummies aktuell: **701/702/703**.

---

## 7) FINALCHECK-STATUS (wichtige Klarstellung)
`STATE_ZQS.json.next_step` zeigt **PHASE_08/FinalCheck**.
Das heißt NICHT „fertig“, sondern:
- FinalCheck darf als Audit laufen,
- muss aber derzeit **FAIL** sein, weil zentrale Sollpunkte fehlen (siehe Abschnitt 5).

---

## 8) WIE MAN KÜNFTIG „KONZEPT vs. STAND“ LIEST

### Konzept lesen
- Alles ZQS-Relevante wird **nur** aus `flow 4/` abgeleitet.
- Bei Konflikt: Master Alignment gewinnt.

### Stand lesen
- `STATE_ZQS.json` sagt dir, welcher Schritt „formal“ als nächstes dran ist.
- `CHECKLIST_ZQS.md` sagt dir, was bereits abgehakt wurde.
- `DUMMIES_ZQS.md` ist die exakte Liste dessen, was noch fehlt.
- Code-Wahrheit steckt in:
  - `core/.../quest/zqs/runtime/ZqsRuntime.java` (Fassade)
  - `core/.../quest/zqs/dock/WanderQuestGuyDock.java` (Dock)
  - `core/.../quest/zqs/save/*` (Persistenz)
  - `core/.../quest/zqs/text/*` (Text)
  - `core/.../quest/zqs/generator/*` (Generator scaffolding)
  - `core/.../quest/zqs/history/*` (History)

---

## 9) „WEITER“-ANWEISUNG NACH DEM RESET (konkret)
Wenn du nach dem Reset nur „weiter“ sagst, ist der korrekte nächste technische Block:

> Implementiere **DUMMY (702)** Accept-Persistenz in `ZqsRuntime`:
> - Record in `zqsSave.playerQuestDb.records` anlegen
> - LogbookEntryNr aus `zqsSave.logbook.next_logbook_entry_nr` vergeben + inkrementieren
> - `zqsSave.questHistoryIndex.active_quest_ids` pflegen
> - `generated_text_ids` + `accepted_text` setzen (TextAssembler-Outputs)
> - Kein OfferBuffer persistieren.

Danach **DUMMY (701)** Import aus Save.

---
