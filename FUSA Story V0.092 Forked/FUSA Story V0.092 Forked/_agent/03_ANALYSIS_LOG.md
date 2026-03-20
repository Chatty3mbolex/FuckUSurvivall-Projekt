# 03_ANALYSIS_LOG.md

## INIT
- Created _agent memory scaffold per master plan.
- Next: Step 5.2 SaveLoadController — symbol map + save-key extraction.

## STEP 5.2 — pre-analysis artifacts
- Snapshot excerpt saved: _agent/snapshots/gamescreen_saveload_excerpt_8550_8730.txt
- String literal list saved: _agent/snapshots/saveload_string_literals.txt (count=104; crude extraction)
- Next: build explicit symbol dependency list for clearAllStateForLoad/doSave/doLoad (fields + methods + helper calls), write into 13_SYMBOL_MAP.md and 04_DEPENDENCY_MAP.md.


## Tool approval (Step 5.2)
- Reviewed _agent/tools/extract_method_body_and_deps.ps1.
- Inputs: FilePath, MethodName, OutDir.
- Outputs: method body + token list + dotted identifier list in OutDir.
- Side effects: writes only into OutDir.
- OK to run.


## STEP 5.2 — analysis pass 1.1
- Wrote SaveManager call snapshots:
  - _agent/snapshots/step52_SaveManager_saveSlot_call.txt
  - _agent/snapshots/step52_SaveManager_loadSlot_call.txt
- Appended call-site documentation to _agent/14_SAVE_COMPAT_NOTES.md.
- Appended focused token lists to _agent/13_SYMBOL_MAP.md.
- Next: second analysis pass (stabilization) — verify no new symbols appear; then draft injection design for SaveLoadController + update checklist_todo for step 5.2 sub-edits/sanity/rollback.


## STEP 5.2 — analysis pass 2 (stabilization)
- Generated pass2 snapshots under _agent/snapshots/step52_methods_pass2/.
- Compared pass1 vs pass2 tokens/dotted identifiers: _agent/snapshots/step52_pass_compare.txt => all newTokens/newDotted = 0.
- Interpretation: Symbol map is stable enough to proceed to injection design (Loop B pass condition met).


## STEP 5.2 — injection design completed
- Decision: implement SaveLoadController as inner class of GameScreen to avoid widening access to private state.
- Design documented in _agent/14_SAVE_COMPAT_NOTES.md.
- Next: extraction edits with backups + sanity checks.


## STEP 5.3 — analysis pass 1
- Wrote symbol hit list: _agent/snapshots/step53_arrow_symbol_hits.txt
- Wrote method snapshots + tokens/dotted: _agent/snapshots/step53_methods/ for fireBowArrow, tickArrows, arrowHitSegment, onKill.
- Appended to _agent/13_SYMBOL_MAP.md and _agent/04_DEPENDENCY_MAP.md.
- Next: pass 2 stabilization (compare tokens/dotted), then injection design.


## STEP 5.3 — analysis pass 2 (stabilization)
- Generated pass2 snapshots under _agent/snapshots/step53_methods_pass2/.
- Compared pass1 vs pass2 tokens/dotted: _agent/snapshots/step53_pass_compare.txt => all newTokens/newDotted = 0.
- Interpretation: stable enough to proceed to injection design.


## STEP 5.3 — Injection / extraction design for ProjectileCombatController [2026-03-20 15:02:25]

### Goal
Extract the bow/arrow projectile subsystem from GameScreen:
- Arrow state arrays + constants:
  - ARROW_MAX, ITEM_ARROW, ARROW_HIT_RADIUS
  - arrowAlive/arrowX/arrowY/arrowVx/arrowVy/arrowTravel/arrowRange/arrowDmg
  - bowCooldownT
  - arrowLastKill
- Methods:
  - fireBowArrow(...)
  - tickArrows(float dt)
  - arrowHitSegment(...)
  - drawArrowsWorld()
- onKill(CombatSystem.Kill k) will be moved LAST (per plan) into the controller.

### Constraint: private-field coupling
These methods touch many GameScreen members (entities, inv, px/py, game.audio, shape/cam, etc.).
No compile/run available.

### Safe approach (minimal visibility changes)
- Implement ProjectileCombatController as an **inner class** of GameScreen first:
  - private final ProjectileCombatController proj = new ProjectileCombatController();
  - Inner class keeps access to private fields without widening visibility.

### Delegation contract
GameScreen delegates:
- bow cooldown ticking (currently near main tick): call proj.tickCooldown(dt) or keep as inline, but arrow tick must call proj.tickArrows(dt).
- arrow rendering: proj.drawArrowsWorld()
- arrow firing: proj.fireBowArrow(...) or wrapper method.

### Sanity checklist
- Only one definition of arrow arrays/consts.
- No behavior changes inside the extracted methods (copy 1:1).
- Call order:
  - bowCooldown decrement
  - tickArrows(dt) invocation point unchanged
  - drawArrowsWorld() invocation point unchanged
- onKill side effects unchanged (string literals + order).

### Rollback
- Backup GameScreen.java into _agent/backups/STEP_5.3/ before edits.
- If any compile-time ambiguity arises: rollback and stop with BLOCKED_STEP_FIX_LIMIT.

## STEP 5.4 — analysis pass 1
- Wrote symbol hit list: _agent/snapshots/step54_player_symbol_hits.txt
- Wrote method snapshots + tokens/dotted: _agent/snapshots/step54_methods/ for updateMouseWorld, collectInputSnapshot, actionReach, pickupNearby.
- Appended to _agent/13_SYMBOL_MAP.md and _agent/04_DEPENDENCY_MAP.md.
- Next: pass 2 stabilization (compare tokens/dotted), then injection design.


## STEP 5.4 — analysis pass 2 (stabilization)
- Generated pass2 snapshots under _agent/snapshots/step54_methods_pass2/.
- Compared pass1 vs pass2 tokens/dotted: _agent/snapshots/step54_pass_compare.txt => all newTokens/newDotted = 0.
- Interpretation: stable enough to proceed to injection design.


## STEP 5.4 — Injection / extraction design for PlayerController [2026-03-20 16:19:19]

### Goal
Extract player-centric input + interaction logic from GameScreen:
- Mouse world tracking:
  - fields: mouseWorldX/mouseWorldY, cursor hide state
  - method: updateMouseWorld(boolean clampToRing)
- Input snapshot pipeline:
  - method: collectInputSnapshot(float delta, InputState out)
  - keep InputState as is (static inner class) for now
- Interaction helpers:
  - actionReach(int equippedItemId)
  - pickupNearby()

### Constraints
- These methods touch many GameScreen members (cam/uiCam, px/py, entities, inv/wallet/priceBook, audio, settings).
- No compile/run available.

### Safe approach
- Implement PlayerController as **inner class** of GameScreen first:
  - private final PlayerController playerCtl = new PlayerController();
- Keep original data fields (mouseWorldX/Y, inputState, etc.) initially in GameScreen to avoid large field moves.
- Move methods first; move fields later in Step 5.8.

### Delegation contract
- Replace direct calls:
  - updateMouseWorld(...) -> playerCtl.updateMouseWorld(...)
  - collectInputSnapshot(delta, inputState) -> playerCtl.collectInputSnapshot(delta, inputState)
  - ctionReach(...) call sites -> playerCtl.actionReach(...) (or keep wrapper)
  - pickupNearby() -> playerCtl.pickupNearby()

### Sanity checklist
- Call order unchanged.
- Same string literals, same thresholds.
- No extra allocations.

### Rollback
- Backup GameScreen.java into _agent/backups/STEP_5.4/ before edits.

## STEP 5.5 — analysis pass 1
- Wrote symbol hit list: _agent/snapshots/step55_worldmap_symbol_hits.txt
- Wrote method snapshots + tokens/dotted: _agent/snapshots/step55_methods/ for debugWorldMapTravel, areaEnemyZonesTick, areaClearEnemyZones, areaAddEnemyZone, areaWorldMapState.
- Appended to _agent/13_SYMBOL_MAP.md and _agent/04_DEPENDENCY_MAP.md.
- Next: pass 2 stabilization (compare tokens/dotted), then injection design.


## STEP 5.5 — analysis pass 2 (stabilization)
- Generated pass2 snapshots under _agent/snapshots/step55_methods_pass2/.
- Compared pass1 vs pass2 tokens/dotted: _agent/snapshots/step55_pass_compare.txt => all newTokens/newDotted = 0.
- Interpretation: stable enough to proceed to injection design.


## STEP 5.5 — Injection / extraction design for WorldMapController [2026-03-20 17:00:04]

### Goal
Extract world map travel + authored area-related worldmap state from GameScreen:
- WorldMap state and travel wiring:
  - field: worldMap (WorldMapState)
  - field: worldMapTravel (WorldMapTravelController)
  - method: debugWorldMapTravel(Dir4 dir)
  - debugTravelNorth/East/South/West delegates
- Authored enemy zones (AREA_MODE):
  - enemyZones list + EnemyZone inner class
  - areaEnemyZonesTick(dt)
  - areaClearEnemyZones()
  - areaAddEnemyZone(...)
- Expose world map state to loaders:
  - areaWorldMapState()

### Constraints
- Touches many systems: entities, worldNodes, area loader, WorldMapTravelController, area tile streaming.
- No compile/run.

### Safe approach
- Implement WorldMapController as **inner class** first.
- Keep fields worldMap and worldMapTravel in GameScreen initially to reduce churn.
- Move methods + EnemyZone structures into controller; use wrappers in GameScreen.

### Delegation contract
- GameScreen wrappers:
  - debugTravel* -> worldMapCtl.debugTravel*(...)
  - debugWorldMapTravel -> worldMapCtl.debugWorldMapTravel
  - areaEnemyZonesTick -> worldMapCtl.areaEnemyZonesTick
  - areaClearEnemyZones/areaAddEnemyZone -> worldMapCtl
  - areaWorldMapState -> worldMapCtl.areaWorldMapState

### Sanity checklist
- Travel call site order unchanged (worldMapTravel.travel(this, worldMap, dir, seed)).
- Enemy zone tick semantics unchanged.
- areaWorldMapState returns same instance.

### Rollback
- Backup GameScreen.java into _agent/backups/STEP_5.5/ before edits.

## STEP 5.6 — analysis pass 1
- Wrote symbol hit list: _agent/snapshots/step56_ui_symbol_hits.txt
- Wrote method snapshots + tokens/dotted: _agent/snapshots/step56_methods/ for:
  - drawHotbarHud, drawBuildPanel, drawCraftPanel, drawShopMenuAtMerchant,
  - drawQuestLogHubPanel, drawQuestLogOpenQuestsPanel, drawQuestLogDoneQuestsPanel,
  - drawInventoryGrid, drawWalletMenu, drawSkillMenu
- Appended to _agent/13_SYMBOL_MAP.md and _agent/04_DEPENDENCY_MAP.md.
- Next: pass 2 stabilization (compare tokens/dotted), then injection design.


## STEP 5.6 — analysis pass 2 (stabilization)
- Generated pass2 snapshots under _agent/snapshots/step56_methods_pass2/.
- Compared pass1 vs pass2 tokens/dotted: _agent/snapshots/step56_pass_compare.txt => all newTokens/newDotted = 0.
- Interpretation: stable enough to proceed to injection design.


## STEP 5.6 — Injection / extraction design for GameUiController [2026-03-20 17:46:26]

### Goal
Extract UI rendering + UI panel/menu logic from GameScreen into a dedicated controller:
- HUD:
  - drawHotbarHud
- Panels/Menus:
  - drawBuildPanel
  - drawCraftPanel
  - drawShopMenuAtMerchant
  - drawQuestLogHubPanel
  - drawQuestLogOpenQuestsPanel
  - drawQuestLogDoneQuestsPanel
  - drawInventoryGrid
  - drawWalletMenu
  - drawSkillMenu

### Constraints
- Heavy coupling to GameScreen fields: uiRegions, batch, font, UI_FONT_SCALE, UI coords, flags (shopOpen/craftOpen/etc.), drag state.
- No compile/run.

### Safe approach
- Implement GameUiController as **inner class** first.
- Move methods first (copy 1:1), keep UI state fields in GameScreen initially.
- Replace original methods with wrappers delegating to uiCtl.

### Delegation contract
- Add: private final GameUiController uiCtl = new GameUiController();
- Replace each method body with: uiCtl.<method>();

### Sanity checklist
- All wrappers call uiCtl.
- No method duplicates beyond wrapper + inner implementation.
- Brace balance open==close.
- No changes to string literals or numeric constants.

### Rollback
- Backup GameScreen.java to _agent/backups/STEP_5.6/.

## STEP 5.7 — analysis pass 1
- Wrote symbol hit list: _agent/snapshots/step57_renderer_symbol_hits.txt
- Wrote method snapshots + tokens/dotted: _agent/snapshots/step57_methods/ for:
  - renderWorld, renderUI
  - updateDnMask, rebuildDnMaskFbo
  - updateDayNightMusic (NOTE: must NOT move into renderer)
- Appended to _agent/13_SYMBOL_MAP.md and _agent/04_DEPENDENCY_MAP.md.
- Next: pass 2 stabilization, then injection design.


## STEP 5.7 — analysis pass 2 (stabilization)
- Generated pass2 snapshots under _agent/snapshots/step57_methods_pass2/.
- Compared pass1 vs pass2 tokens/dotted: _agent/snapshots/step57_pass_compare.txt => all newTokens/newDotted = 0.
- Interpretation: stable enough to proceed to injection design.


## STEP 5.7 — Injection / extraction design for GameRenderer [2026-03-20 18:30:26]

### Goal
Extract render responsibilities from GameScreen:
- World rendering:
  - renderWorld(float delta, int selectedTool)
- UI rendering:
  - renderUI()
- Day/Night lamp mask rendering:
  - updateDnMask()
  - rebuildDnMaskFbo(int w, int h)

### Hard rule (plan)
- updateDayNightMusic(float delta) MUST remain outside renderer.
  - Renderer may compute daylight factors, but MUST NOT call audio.

### Constraints
- Heavy coupling to:
  - batch/shape, cam/uiCam, dnMaskFbo/dnMaskRegion, dayNight values, uiRegions/font.
- No compile/run.

### Safe approach
- Implement GameRenderer as **inner class** first:
  - private final GameRenderer renderer = new GameRenderer();
- Move renderWorld/renderUI/updateDnMask/rebuildDnMaskFbo into inner class (copy 1:1).
- Replace original GameScreen methods with wrappers delegating to renderer.
- Keep updateDayNightMusic in GameScreen and call it from the same places as before.

### Delegation contract
- renderWorld(...) -> renderer.renderWorld(...)
- renderUI() -> renderer.renderUI()
- updateDnMask() -> renderer.updateDnMask()
- rebuildDnMaskFbo(...) -> renderer.rebuildDnMaskFbo(...)

### Sanity checklist
- Wrapper methods exist and delegate.
- updateDayNightMusic is NOT moved.
- Brace balance open==close.

### Rollback
- Backup GameScreen.java into _agent/backups/STEP_5.7/.
