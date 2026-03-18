package com.yourgame.survival.worldgen;

import com.yourgame.survival.world.Chunk;

/**
 * WorldGen entrypoint: deterministic generation of a single chunk.
 *
 * Guardrail: MUST be deterministic by (seed,cx,cy) and MUST NOT touch any global caches directly.
 */
public interface WorldGenerator {
  Chunk generate(int cx, int cy);
}
