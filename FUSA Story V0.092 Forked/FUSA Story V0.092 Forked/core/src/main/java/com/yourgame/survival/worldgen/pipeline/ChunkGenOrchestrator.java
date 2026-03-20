package com.yourgame.survival.worldgen.pipeline;

import com.yourgame.survival.biome.BiomeSystem;
import com.yourgame.survival.world.Chunk;
import com.yourgame.survival.world.TileLayers;
import com.yourgame.survival.world.World;
import com.yourgame.survival.worldgen.WorldGenConfig;
import com.yourgame.survival.worldgen.WorldGenContext;
import com.yourgame.survival.worldgen.WorldGenerator;
import com.yourgame.survival.worldgen.adjacency.AdjacencyBuilder;
import com.yourgame.survival.worldgen.adjacency.DefaultAdjacencyBuilder;
import com.yourgame.survival.worldgen.adjacency.DefaultTransitionMaskBuilder;
import com.yourgame.survival.worldgen.biome.BiomeClassifier;
import com.yourgame.survival.worldgen.biome.DefaultBiomeClassifier;
import com.yourgame.survival.worldgen.deco.DecoScatter;
import com.yourgame.survival.worldgen.deco.DefaultDecoScatter;
import com.yourgame.survival.worldgen.noise.FbmNoiseSampler;
import com.yourgame.survival.worldgen.noise.NoiseSampler;
import com.yourgame.survival.worldgen.paint.DefaultEdgeGroundBlender;
import com.yourgame.survival.worldgen.paint.DefaultTilePainter;
import com.yourgame.survival.worldgen.paint.EdgeGroundBlender;
import com.yourgame.survival.worldgen.paint.TilePainter;
import com.yourgame.survival.worldgen.roads.RoadAdjacencyBaker;
import com.yourgame.survival.worldgen.roads.RoadPlanner;
import com.yourgame.survival.worldgen.roads.WorldRoadPlanner;
import com.yourgame.survival.worldgen.water.DefaultWaterPostProcessor;
import com.yourgame.survival.worldgen.water.WaterPostProcessor;

/**
 * Orchestrates chunk generation by calling modules in order.
 *
 */
public final class ChunkGenOrchestrator implements WorldGenerator {
  private final WorldGenContext ctx;

  // Modules
  private final NoiseSampler noise;
  private final BiomeClassifier classifier;
  private final TilePainter painter;
  private final WaterPostProcessor water;
  private final EdgeGroundBlender edgeBlend;
  private final RoadPlanner roads;
  private final RoadAdjacencyBaker roadAdj;
  private final AdjacencyBuilder adjacency;
  private final DefaultTransitionMaskBuilder transitions;
  private final DecoScatter deco;

  public ChunkGenOrchestrator(WorldGenContext ctx) {
    this(ctx, null);
  }

  public ChunkGenOrchestrator(WorldGenContext ctx, com.yourgame.survival.world.Biome forcedBiome) {
    this.ctx = ctx;

    // Default module graph
    this.noise = new FbmNoiseSampler();
    this.classifier = new DefaultBiomeClassifier();

    this.painter = (forcedBiome != null)
        ? new DefaultTilePainter(noise, classifier, forcedBiome)
        : new DefaultTilePainter(noise, classifier);

    this.water = new DefaultWaterPostProcessor(noise, classifier);
    this.edgeBlend = new DefaultEdgeGroundBlender(noise, classifier);

    WorldRoadPlanner roadPlanner = new WorldRoadPlanner(noise, classifier);
    this.roads = roadPlanner;
    this.roadAdj = new RoadAdjacencyBaker(roadPlanner);

    this.adjacency = new DefaultAdjacencyBuilder(noise, classifier);
    this.transitions = new DefaultTransitionMaskBuilder(noise, classifier);
    this.deco = new DefaultDecoScatter();
  }

  /** Convenience factory used when wiring into World/ChunkStore. */
  public static ChunkGenOrchestrator create(long seed, BiomeSystem biomes) {
    return create(seed, biomes, null);
  }

  /** Editor/preview: force a single biome for all generated tiles. */
  public static ChunkGenOrchestrator create(long seed, BiomeSystem biomes, com.yourgame.survival.world.Biome forcedBiome) {
    WorldGenConfig cfg = WorldGenConfig.loadOrDefault();
    WorldGenContext ctx = new WorldGenContext(seed, biomes, cfg);
    return new ChunkGenOrchestrator(ctx, forcedBiome);
  }

  @Override
  public Chunk generate(int cx, int cy) {
    TileLayers layers = new TileLayers(World.CHUNK_SIZE);

    painter.paint(layers, cx, cy, ctx);
    water.process(layers, cx, cy, ctx);
    edgeBlend.blend(layers, cx, cy, ctx);

    if (ctx.config.roadsEnabled) {
      roads.buildRoadMask(layers, cx, cy, ctx);
      roadAdj.bake(layers, cx, cy, ctx);
    }

    adjacency.build(layers, cx, cy, ctx);
    transitions.build(layers, cx, cy, ctx);
    deco.scatter(layers, cx, cy, ctx);

    return new Chunk(cx, cy, layers);
  }
}
