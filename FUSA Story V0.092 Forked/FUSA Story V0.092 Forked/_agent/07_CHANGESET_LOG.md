# 07_CHANGESET_LOG.md

- INIT: created `_agent/*` memory scaffold files.
- STEP 5.2: GameScreen.java — added inner SaveLoadController; delegated doSave/doLoad; removed old clearAllStateForLoad body (save/load order preserved).
- STEP 5.3: GameScreen.java — added inner ProjectileCombatController; delegated bow/arrow tick/render; moved onKill logic into controller (wrapper retained).
- STEP 5.3: GameScreen.java — projectile subsystem moved into inner ProjectileCombatController; call sites delegated; onKill moved into controller.
- STEP 5.4: GameScreen.java — added inner PlayerController; moved player-centric methods and delegated wrappers.
- STEP 5.4: GameScreen.java — player-centric methods moved into inner PlayerController; wrappers delegate.
- STEP 5.5: GameScreen.java — added inner WorldMapController; moved enemy zones + travel debug and delegated wrappers.
- STEP 5.6: GameScreen.java — added inner GameUiController; moved UI methods and delegated wrappers.
- STEP 5.6: GameScreen.java — UI methods moved into inner GameUiController; wrappers delegate.
- STEP 5.7: GameScreen.java — added inner GameRenderer; moved render/mask methods and delegated wrappers; updateDayNightMusic kept outside.
- STEP 5.7: GameScreen.java — render/mask methods moved into inner GameRenderer; wrappers delegate; updateDayNightMusic stays outside.
