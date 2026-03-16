package com.yourgame.survival.entity;

/**
 * Minimal SoA-ish container (Block 4 bootstrap).
 * Not optimized yet; just stable + predictable.
 */
public final class Entities {
  public static final int MAX = 2048;

  public final boolean[] alive = new boolean[MAX];
  public final EntityType[] type = new EntityType[MAX];
  public final float[] x = new float[MAX];
  public final float[] y = new float[MAX];
  public final float[] hp = new float[MAX];
  public final float[] hpMax = new float[MAX];

  // Motion hint for animation (world units / second)
  public final float[] vx = new float[MAX];
  public final float[] vy = new float[MAX];

  // Last facing direction for animation: 0=N,1=E,2=S,3=W
  public final byte[] dir = new byte[MAX];

  // Facing lock timer (seconds). While > 0, we keep current dir to avoid jitter at sector borders.
  public final float[] faceLock = new float[MAX];

  // Generic AI timer (seconds) for simple wandering behaviors
  public final float[] aiT = new float[MAX];

  // Secondary AI timer (seconds) (used for additional behavior timers)
  public final float[] aiT2 = new float[MAX];

  // Generic extra payload
  public final float[] rot = new float[MAX];
  public final float[] aiF0 = new float[MAX]; // generic float payload (e.g. animal wander speed)
  public final float[] aiF1 = new float[MAX]; // generic float payload (e.g. alertness / ramps)
  public final int[] data0 = new int[MAX];

  // ITEM_DROP payload
  public final int[] itemId = new int[MAX];
  public final int[] itemAmount = new int[MAX];

  // Home position for zone-bound wandering (set at spawn, used by AI to stay in zone)
  public final float[] homeX = new float[MAX];
  public final float[] homeY = new float[MAX];
  public final float[] wanderRadius = new float[MAX]; // 0 = unlimited (legacy behavior)

  // Entity flags (bitmask)
  public static final byte FLAG_ALWAYS_ACTIVE = 1;
  public final byte[] flags = new byte[MAX];

  public int spawn(EntityType t, float px, float py) {
    for (int i=0;i<MAX;i++) {
      if (!alive[i]) {
        alive[i] = true;
        type[i] = t;
        x[i] = px;
        y[i] = py;
        hpMax[i] = defaultHp(t);
        hp[i] = hpMax[i];
        rot[i] = 0f;
        vx[i] = 0f;
        vy[i] = 0f;
        dir[i] = 2; // default face South
        faceLock[i] = 0f;
        aiT[i] = 0f;
        aiT2[i] = 0f;
        flags[i] = 0;
        aiF0[i] = 0f;
        aiF1[i] = 0f;
        data0[i] = -1;
        itemId[i] = -1;
        itemAmount[i] = 0;
        homeX[i] = px;
        homeY[i] = py;
        wanderRadius[i] = 0f;
        return i;
      }
    }
    return -1;
  }

  public int spawnDrop(int itemId, int amount, float px, float py) {
    int e = spawn(EntityType.ITEM_DROP, px, py);
    if (e >= 0) {
      this.itemId[e] = itemId;
      this.itemAmount[e] = amount;
      this.hpMax[e] = 1;
      this.hp[e] = 1;
    }
    return e;
  }

  public void kill(int e) {
    if (e < 0 || e >= MAX) return;
    alive[e] = false;
  }

  public void setAlwaysActive(int e, boolean on) {
    if (e < 0 || e >= MAX) return;
    if (on) flags[e] |= FLAG_ALWAYS_ACTIVE;
    else flags[e] &= ~FLAG_ALWAYS_ACTIVE;
  }

  public boolean isAlwaysActive(int e) {
    if (e < 0 || e >= MAX) return false;
    return (flags[e] & FLAG_ALWAYS_ACTIVE) != 0;
  }

  private static float defaultHp(EntityType t) {
    return switch (t) {
      case PLAYER -> 100f;
      case ORK_GRUNT -> 35f;
      case ANIMAL_DEER -> 20f;
      case MERCHANT_ELF, MERCHANT_WANDERING -> 9999f;
      case WANDER_QUEST_GUY -> 9999f;
      case NODE_TREE -> 20f;
      case NODE_STUMP -> 10f;
      case NODE_ROCK -> 25f;
      case NODE_ORE_IRON -> 25f;
      case NODE_BUSH -> 10f;
      case NODE_FISH_SPOT -> 12f;
      case BUILD_CHEST, POI_CHEST_HIDDEN -> 20f;
      case BUILD_WORKBENCH -> 20f;
      case BUILD_BED -> 20f;
      case BUILD_CAMPFIRE -> 20f;
      case BUILD_LAMP -> 20f;
      case LANDMARK_CASTLE, LANDMARK_BRIDGE -> 9999f;
      case ITEM_DROP -> 1f;
    };
  }
}
