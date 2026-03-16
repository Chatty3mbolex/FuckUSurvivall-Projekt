# FINALCHECK_ZQS.md — Abschlusskontrolle (PHASE_08)

> Zweck: Checklisten für Vollständigkeit, Scope und Dokumentation.
> Wird am Ende der Umsetzung ausgeführt und dokumentiert.

---

## 08.01 — Vollständigkeitsprüfung (gegen Soll-Liste)

Prüfen und dokumentieren (mit Dateiverweisen), dass vorhanden ist:

1) **Kataloge**
- Runtime-Katalog DB vorhanden: `assets/data/zqs/catalog_runtime_v1.json`
- Loader + Runtime-Struktur vorhanden: `quest/zqs/catalog/*`

2) **Known-State**
- `PlayerKnowledgeState` vorhanden + Save/Load im ZQS-Block

3) **Blueprint-System**
- Blueprint-DB vorhanden: `assets/data/zqs/blueprints_v1.json`
- Loader/Registry vorhanden

4) **Generator**
- Eligibility + Filterkette + RepeatRules implementiert
- deterministische QuestID implementiert

5) **Reward-System**
- RewardProfile DB vorhanden: `assets/data/zqs/reward_profiles_v1.json`
- Rewardformeln implementiert gemäß Reward-Referenz

6) **Textsystem**
- Snippet-DB vorhanden: `assets/data/zqs/text_snippets_de_DE_v1.json`
- Snippet-Auswahl + Assembler implementiert gemäß `TEXT_ZQS.md`

7) **History**
- `QuestHistoryIndex` vorhanden + Save/Load

8) **Logbuchnummern**
- persistenter Zähler + logbook entries vorhanden

9) **Save-Struktur**
- SaveBlock `zqs` implementiert gemäß `SAVE_ZQS.md`
- Migration/Defaults implementiert

10) **NPC-Docking**
- WanderQuestGuy dockt nur an und erzeugt selbst keine Questlogik

---

## 08.02 — Scope-Prüfung

Prüfen und dokumentieren:
- nicht kompiliert
- nicht gestartet
- nicht debuggt
- nicht committed
- kein Fremd-Refactor außerhalb ZQS-Umfang
- keine ungeplanten Änderungen an Kampf/Worldgen/UI außer Dockingpunkte

---

## 08.03 — Dokumentationsprüfung

Prüfen:
- `READ_FIRST_ZQS.md` aktuell
- `STATE_ZQS.json` korrekt (Phase/Step/next_step)
- `CHECKLIST_ZQS.md` konsistent
- `ERRORS.md` enthält relevante Abweichungen/Fehler
- `DECISIONS_ZQS.md` enthält nur umgesetzte Architekturentscheidungen
- `DUMMIES_ZQS.md` listet alle verbleibenden Dummies

---

## Abschlussregel

PHASE_08 gilt als bestanden, wenn alle drei Blöcke (08.01–08.03) dokumentiert als erfüllt markiert sind.
