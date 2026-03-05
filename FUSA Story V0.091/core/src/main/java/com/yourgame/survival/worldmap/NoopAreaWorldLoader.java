package com.yourgame.survival.worldmap;

import com.yourgame.survival.screens.GameScreen;

/**
 * Placeholder implementation used during scaffolding.
 *
 * Decision:
 * - We ship a no-op loader so that the WorldMap runtime can be integrated safely
 *   without immediately changing the procedural world.
 *
 * Later we will replace this with a real loader (e.g. Tiled/JSON) that:
 * - creates or swaps the world/tiles
 * - sets player spawn point according to the enteredFrom edge
 * - rebuilds worldNodes and other area-local systems
 */
public final class NoopAreaWorldLoader implements AreaWorldLoader {
  @Override
  public boolean loadInto(GameScreen gs, String templateId, Dir4 enteredFrom) {
    // Intentionally does nothing.
    // This keeps the current procedural world alive.
    return true;
  }
}
