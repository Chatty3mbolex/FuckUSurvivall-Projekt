package com.yourgame.survival.worldmap;

import com.yourgame.survival.screens.GameScreen;

/**
 * Loads an AreaTemplate into the runtime world.
 *
 * This is intentionally an INTERFACE (abstraction) and not implemented directly in GameScreen.
 *
 * Why?
 * - We are mid-pivot from procedural world streaming -> hand-authored areas.
 * - GameScreen currently owns a procedural {@code World} instance and chunk streaming.
 * - The new system needs a clean seam where we can later plug in:
 *   - tilemap loading
 *   - spawn point selection
 *   - transition effects
 *   - quest-gated exits
 *   without rewriting savegame/state logic.
 *
 * Current alpha scaffolding behavior goal:
 * - Keep the code compiling and structurally correct.
 * - Do NOT change gameplay yet unless explicitly wired.
 */
public interface AreaWorldLoader {

  /**
   * Load the given template into the game.
   *
   * @param gs GameScreen instance to mutate (world, nodes, merchants, etc.).
   * @param templateId stable template id from savegame (e.g. HOME, GEN_GRASSLAND ...).
   * @param enteredFrom direction we used to enter the area (can be null for initial load).
   *
   * @return true if load succeeded, false if template is missing/unloadable.
   */
  boolean loadInto(GameScreen gs, String templateId, Dir4 enteredFrom);
}
