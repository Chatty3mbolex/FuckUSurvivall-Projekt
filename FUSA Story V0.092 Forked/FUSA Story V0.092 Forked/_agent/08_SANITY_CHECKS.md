# 08_SANITY_CHECKS.md

- INIT: _agent scaffold created; no code sanity checks executed yet.

## STEP 5.2 Sanity [2026-03-20 14:38:46]
- Brace balance check: OK (open==close)
- SaveManager.saveSlot call block vs snapshot: OK (trim-normalized diff=0)
- SaveManager.loadSlot call block vs snapshot: OK (trim-normalized diff=0)
- Delegation: public doSave/doLoad delegate to saveLoad: OK (regex checks)


## STEP 5.3 Sanity [2026-03-20 15:44:27]\n- Brace balance check: open=1146 close=1146\n- Delegation checks:\n  - Bow fire uses projCombat.fireBowArrow: True\n  - Bow cooldown uses projCombat.bowCooldownT/setBowCooldownT: True\n  - Tick uses projCombat.tickCooldown + projCombat.tickArrows: True\n  - Render uses projCombat.drawArrowsWorld: True\n- No remaining GameScreen.this.onKill in projectile code: True\n

## STEP 5.4 Sanity [2026-03-20 16:40:49]\n- Brace balance: open=1151 close=1151\n- Wrapper delegation present: updateMouseWorld=True collectInputSnapshot=True actionReach=True pickupNearby=True\n- PlayerController contains copied bodies (see source patches).\n

## STEP 5.5 Sanity [2026-03-20 17:20:53]\n- Brace balance: open=1157 close=1157\n- Wrapper delegation present: debugWorldMapTravel=True areaEnemyZonesTick=True areaClear=True areaAdd=True areaWorldMapState=True\n- EnemyZone struct moved under WorldMapController (no top-level EnemyZone class remains).\n

## STEP 5.6 Sanity [2026-03-20 18:08:52]\n- Brace balance: open=1168 close=1168\n- Wrapper delegation present: hotbar=True craft=True shop=True inv=True wallet=True skills=True\n

## STEP 5.7 Sanity note
- Previous regex falsely reported updateDayNightMusic inside renderer; brace-scan confirmed updateDayNightMusic is OUTSIDE GameRenderer block (OK per plan).

## STEP 5.7 REDO Sanity
- Re-extracted GameRenderer with v2 script (wrappers replaced before insertion).
- Verified: updateDayNightMusic line is outside renderer brace range.
- Verified: no 'renderer.renderWorld' call inside GameRenderer block.


## STEP 5.8 Sanity [2026-03-20 18:55:17]\n- Brace balance: open=1173 close=1173\n- Removed wrappers: decl renderWorld=2 decl renderUI=2 decl updateDnMask=2 decl rebuildDnMaskFbo=2 (all must be 0)\n- Call sites now use renderer.*: renderWorldCalls=1 renderUICalls=1\n

## STEP 5.8 UI-wrapper cleanup Sanity [2026-03-20 19:00:06]
- Brace balance: open=1163 close=1163
- UI wrappers removed (sig2 counts should be 0); call sites should be uiCtl.*

- drawHotbarHud: wrappers(sig2)= bareCalls=0 uiCtlCalls=
- drawBuildPanel: wrappers(sig2)= bareCalls=0 uiCtlCalls=
- drawCraftPanel: wrappers(sig2)= bareCalls=0 uiCtlCalls=
- drawShopMenuAtMerchant: wrappers(sig2)= bareCalls=0 uiCtlCalls=
- drawQuestLogHubPanel: wrappers(sig2)= bareCalls=0 uiCtlCalls=
- drawQuestLogOpenQuestsPanel: wrappers(sig2)= bareCalls=0 uiCtlCalls=
- drawQuestLogDoneQuestsPanel: wrappers(sig2)= bareCalls=0 uiCtlCalls=
- drawInventoryGrid: wrappers(sig2)= bareCalls=0 uiCtlCalls=
- drawWalletMenu: wrappers(sig2)= bareCalls=0 uiCtlCalls=
- drawSkillMenu: wrappers(sig2)= bareCalls=0 uiCtlCalls=

## STEP 5.8 WorldMap/Player wrapper cleanup Sanity [2026-03-20 19:05:38]
- Brace balance: open=1131 close=1131
- Top-level wrapper decls removed for worldMapCtl/playerCtl (debugWorldMapTravel/areaEnemyZonesTick/areaClearEnemyZones/areaAddEnemyZone/areaWorldMapState + updateMouseWorld/collectInputSnapshot/actionReach/pickupNearby)

