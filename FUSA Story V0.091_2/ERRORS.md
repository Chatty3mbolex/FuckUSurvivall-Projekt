# ERRORS.md (ZQS)

## 2026-03-16 08:06 (Europe/Berlin)
- Phase: PHASE_01 / PHASE_02
- Schritt: Workflow-Compliance
- Betroffene Bereiche:
  - `core/src/main/java/com/yourgame/survival/quest/zqs/**`
  - `core/src/main/java/com/yourgame/survival/systems/WanderQuestGuySystem.java`
  - `core/src/main/java/com/yourgame/survival/screens/GameScreen.java`
  - `assets/data/zqs/**`
- Fehlerbeschreibung:
  - Umsetzung/Produktionscode wurde begonnen, bevor PHASE_01 (vollständige Ist-Analyse + Lückenanalyse-Kontrolle) und PHASE_02 (Pflichtfelder schriftlich vollständig) gemäß Workflowplan vollständig abgeschlossen waren.
- Wiederholungszähler:
  - workflow_deviation: 1
- Nächster Versuch:
  - STOP Coding.
  - Workflow nach Plan: PHASE_01 01.02/01.03 vervollständigen, danach PHASE_02 schriftlich komplettieren.

## 2026-03-16 15:30 (Europe/Berlin)
- Phase: PHASE_07 / PHASE_08
- Schritt: Step-Transition / FinalCheck
- Betroffene Bereiche:
  - `STATE_ZQS.json`
- Fehlerbeschreibung:
  - `STATE_ZQS.json.next_step` wurde auf `PHASE_08/FinalCheck` gesetzt, obwohl laut FINALCHECK_ZQS.md noch zentrale Sollpunkte fehlen (Generator-Eligibility/QuestID/RepeatRules, RewardProfiles-Integration, Persist-Accept/Import, catalog-Package). Der FinalCheck darf natürlich laufen, muss aber als **FAIL** dokumentiert werden und muss anschließend den fehlenden Schritten folgen.
- Wiederholungszähler:
  - workflow_deviation: 1
- Nächster Versuch:
  - FinalCheck als Audit durchführen und explizit FAIL markieren.
  - Danach `STATE_ZQS.json.next_step` auf die fehlenden Implementationsschritte setzen (keine „Pseudo-Finalisierung“).
