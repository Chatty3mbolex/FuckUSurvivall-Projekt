# 12_FILE_TOUCH_MATRIX.md

STEP | FILE | WHY_TOUCHED | PROVED_BY_SYMBOL
---- | ---- | ----------- | ---------------
INIT | _agent/* | master plan required memory scaffold | plan section 3/4
5.2 | core/src/main/java/com/yourgame/survival/screens/GameScreen.java | Extract SaveLoadController as inner class + delegate doSave/doLoad | symbols: clearAllStateForLoad/doSave/doLoad
5.3 | core/src/main/java/com/yourgame/survival/screens/GameScreen.java | Extract ProjectileCombatController as inner class + delegate arrow/combat calls | symbols: ARROW_* + tickArrows/arrowHitSegment/fireBowArrow/onKill
5.4 | core/src/main/java/com/yourgame/survival/screens/GameScreen.java | Extract PlayerController as inner class + delegate input/mouse/reach/pickup | symbols: updateMouseWorld/collectInputSnapshot/actionReach/pickupNearby
5.5 | core/src/main/java/com/yourgame/survival/screens/GameScreen.java | Extract WorldMapController as inner class + delegate worldmap travel/enemyZones | symbols: debugWorldMapTravel + enemyZones + areaWorldMapState
5.6 | core/src/main/java/com/yourgame/survival/screens/GameScreen.java | Extract GameUiController as inner class + delegate UI draw methods | symbols: draw* UI methods
5.7 | core/src/main/java/com/yourgame/survival/screens/GameScreen.java | Extract GameRenderer as inner class + delegate renderWorld/renderUI/mask | symbols: renderWorld/renderUI/updateDnMask/rebuildDnMaskFbo
5.8 | core/src/main/java/com/yourgame/survival/screens/GameScreen.java | Remove redundant wrapper methods by updating call sites to controllers directly; keep behavior | symbols: renderWorld/renderUI wrappers
