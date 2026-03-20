# 04_DEPENDENCY_MAP.md

(Empty — will be filled during Step 5.2 analysis.)

## STEP 5.2 — Dependency seed [2026-03-20 13:54:01]
- Systems touched (heuristic):

- chestStore.
- dayNight.
- entities.
- inv.
- merchants.
- pricingEditor.
- questGuy.
- questLog.
- worldMap.
- worldNodes.
- zqsRt.

## STEP 5.2 — Dependency seed [2026-03-20 13:55:57]
- Systems touched (heuristic):

- chestStore.
- dayNight.
- entities.
- inv.
- merchants.
- pricingEditor.
- questGuy.
- questLog.
- worldMap.
- worldNodes.
- zqsRt.

## STEP 5.2 — Decision: implement SaveLoadController as GameScreen inner class
- Reason: avoids changing visibility of numerous private members while preserving behavior (no compile/run available).
- Risk: does not reduce file count immediately; will be addressed by Step 5.8 cleanup.


## STEP 5.3 — Dependency seed [2026-03-20 14:50:14]
- Systems touched (heuristic):

- entities.
- game.audio
- progress.

## STEP 5.3 — Decision: implement ProjectileCombatController as GameScreen inner class
- Reason: avoid widening access to many private members (no compile/run).
- Extraction will still remove arrow logic from main GameScreen body; top-level split can be done later once host interface exists.


## STEP 5.4 — Dependency seed [2026-03-20 15:53:34]
- Systems touched (heuristic):


## STEP 5.4 — Dependency seed [2026-03-20 15:54:23]
- Systems touched (heuristic):


## STEP 5.4 — Dependency seed [2026-03-20 15:55:41]
- Systems touched (heuristic):


## STEP 5.4 — Decision: implement PlayerController as GameScreen inner class
- Reason: avoid widening visibility; extraction is method-level first; field moves later.


## STEP 5.5 — Dependency seed [2026-03-20 16:47:29]
- Systems touched (heuristic):

- enemyZones.
- entities.

## STEP 5.5 — Dependency seed [2026-03-20 16:48:23]
- Systems touched (heuristic):

- enemyZones.
- entities.

## STEP 5.5 — Dependency seed [2026-03-20 16:49:37]
- Systems touched (heuristic):

- enemyZones.
- entities.

## STEP 5.5 — Decision: implement WorldMapController as GameScreen inner class
- Reason: avoid visibility changes and keep travel wiring stable.


## STEP 5.6 — Dependency seed [2026-03-20 17:38:33]
- Systems touched (heuristic):

- batch.
- entities.
- font.
- Gdx.input
- inv.
- progress.
- uiRegions.
- wallet.

## STEP 5.6 — Decision: implement GameUiController as GameScreen inner class
- Reason: minimal risk without widening private visibility; field moves postponed.


## STEP 5.7 — Dependency seed [2026-03-20 18:26:22]
- Systems touched (heuristic):

- batch.
- cam.
- dnMaskFbo.
- dnMaskRegion.
- Gdx.gl
- GL20
- shape.
- uiCam.

## STEP 5.7 — Decision: implement GameRenderer as GameScreen inner class
- Reason: minimal risk; keep audio (updateDayNightMusic) in GameScreen.

