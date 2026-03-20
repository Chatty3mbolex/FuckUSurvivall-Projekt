# 14_SAVE_COMPAT_NOTES.md

(Empty — will be filled during Step 5.2 SaveLoadController extraction.)

## STEP 5.2 — Save/Load compatibility notes [2026-03-20 14:05:46]

### SaveManager.saveSlot call-site (GameScreen)
(Do not change argument order/meaning)

`	ext
8639:       SaveManager.saveSlot(
8640:           slot,
8641:           name,
8642:           worldSeed,
8643:           px, py,
8644:           inv,
8645:           wallet,
8646:           progress,
8647:           needs,
8648:           dayNight.t,
8649:           dayIndex,
8650:           hotbar,
8651:           hotbarSel,
8652:           hasBoat,
8653:           hasClimb,
8654:           invAnchorX,
8655:           invAnchorY,
8656:           questHubAnchorX,
8657:           questHubAnchorY,
8658:           questOpenAnchorX,
8659:           questOpenAnchorY,
8660:           questDoneAnchorX,
8661:           questDoneAnchorY,
8662:           entities,
8663:           chestStore,
8664:           worldNodes,
8665:           merchants,
8666:           questGuy,
8667:           questLog,
8668:           zqsSave,
8669:           worldMap
8670:       );
`

### SaveManager.loadSlot call-site (GameScreen)
(Do not change argument order/meaning)

`	ext
8694:       boolean ok = SaveManager.loadSlot(
8695:           slot,
8696:           seed,
8697:           pxpy,
8698:           inv,
8699:           wallet,
8700:           progress,
8701:           needs,
8702:           dt,
8703:           dIdx,
8704:           hb,
8705:           hbSel,
8706:           boat,
8707:           climb,
8708:           uiPos,
8709:           entities,
8710:           chestStore,
8711:           worldNodes,
8712:           null,
8713:           null,
8714:           questLog,
8715:           zqsSave,
8716:           worldMap
8717:       );
`


## STEP 5.2 — Injection / extraction design for SaveLoadController [2026-03-20 14:24:48]

### Goal
- Extract clearAllStateForLoad(), doSave(...), doLoad(int slot) out of GameScreen into screens/SaveLoadController.java.
- Preserve:
  - SaveManager.saveSlot argument order and semantics (see snapshots)
  - SaveManager.loadSlot argument order and semantics (see snapshots)
  - load pipeline ordering: clearAllStateForLoad() -> loadSlot() -> restore scalars -> resetWorld() -> area reload -> rebind systems -> post-load resets.

### Constraint: private-field coupling
These methods access many GameScreen private fields and call private helpers (e.g., esetWorld, pplyMinedRockTilesFromSave, egenShopOffers, etc.).
Java rules mean a separate class cannot access private members.

### Safe approach (minimal visibility changes)
- Make SaveLoadController an **inner class** of GameScreen (e.g., private final class SaveLoadController).
  - This keeps access to private members without changing field visibility.
  - Extraction still reduces cognitive load; file-size reduction comes later (Step 5.8 cleanup) and/or we can later convert to top-level once a host interface exists.

### Delegation contract
- GameScreen owns: private final SaveLoadController saveLoad = new SaveLoadController();
- GameScreen.doSave(int slot) becomes: saveLoad.doSave(slot);
- GameScreen.doSave(int slot, String name) becomes: saveLoad.doSave(slot, name);
- GameScreen.doLoad(int slot) becomes: saveLoad.doLoad(slot);
- clearAllStateForLoad() becomes saveLoad.clearAllStateForLoad(); (or kept private inside controller).

### Non-negotiable: Save compatibility
- The SaveManager.saveSlot(...) and SaveManager.loadSlot(...) call blocks MUST be copied 1:1 (no reorder).
- Any string literals used for toast messages should remain identical.

### Sanity checklist for the extraction
- After extraction:
  - The exact SaveManager.saveSlot( argument block matches _agent/snapshots/step52_SaveManager_saveSlot_call.txt.
  - The exact SaveManager.loadSlot( argument block matches _agent/snapshots/step52_SaveManager_loadSlot_call.txt.
  - No new SaveManager API introduced.
  - GameScreen delegates; old bodies removed.

### Rollback
- Before edits: backup GameScreen.java into _agent/backups/STEP_5.2/.
- If any ambiguity arises: rollback file and stop with BLOCKED_SAVE_COMPAT_RISK.
