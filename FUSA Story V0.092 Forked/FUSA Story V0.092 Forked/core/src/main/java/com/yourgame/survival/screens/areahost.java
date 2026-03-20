package com.yourgame.survival.screens;

import com.yourgame.survival.data.ChestStore;
import com.yourgame.survival.entity.Entities;
import com.yourgame.survival.world.World;
import com.yourgame.survival.worldmap.WorldMapState;

/**
 * Narrow runtime host API for Area/WorldMap loaders.
 *
 * Purpose:
 * - Break direct compile-time dependency on {@code GameScreen} from loaders.
 * - Provide a controlled seam for swapping area content without exposing internal fields.
 */
public interface AreaHost {

  long areaGetWorldSeed();

  long areaWorldSeed();

  void areaResetWorld(long seed);

  void areaSetPlayerWorldPos(float wx, float wy);

  void areaClearAreaLocalEntities();

  void areaSetTreePresentBits(int w, int h, byte[] bits);

  void areaSetTreeCutBits(int w, int h, byte[] bits);

  void areaClearEnemyZones();

  void areaAddEnemyZone(int cxTile, int cyTile, int rTiles, int min, int max, int respawnDaysMin, int respawnDaysMax);

  void areaDebugToastOnce(String msg);

  World areaWorld();

  Entities areaEntities();

  WorldMapState areaWorldMapState();

  ChestStore areaChestStore();
}

