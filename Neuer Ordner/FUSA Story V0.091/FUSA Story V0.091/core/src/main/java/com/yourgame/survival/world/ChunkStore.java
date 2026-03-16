package com.yourgame.survival.world;

import com.yourgame.survival.util.PackCoord;
import com.yourgame.survival.worldgen.WorldGenerator;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Simple in-memory chunk cache for Block 3. */
public final class ChunkStore {
  /** Hard cap to avoid unbounded RAM growth during streaming. */
  private static final int MAX_CHUNKS = 768;

  private final LinkedHashMap<Long, Chunk> chunks = new LinkedHashMap<>(1024, 0.75f, true) {
    @Override
    protected boolean removeEldestEntry(Map.Entry<Long, Chunk> eldest) {
      return size() > MAX_CHUNKS;
    }
  };

  private final WorldGenerator gen;

  // Generation queue (generated off-thread; committed on main thread in tickGenerationBudget()).
  private final ArrayDeque<Long> genQueue = new ArrayDeque<>();
  private final HashSet<Long> queued = new HashSet<>();

  // Completed chunks from the worker thread (thread-safe handoff).
  private final ConcurrentLinkedQueue<Chunk> completed = new ConcurrentLinkedQueue<>();

  // Single worker to keep generation deterministic and avoid CPU spikes.
  private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
    Thread t = new Thread(r, "chunkgen-worker");
    t.setDaemon(true);
    return t;
  });

  public ChunkStore(WorldGenerator gen) {
    this.gen = gen;
    worker.submit(this::workerLoop);
  }

  /** Enqueue chunk generation if missing (non-blocking). */
  public void request(int cx, int cy) {
    long k = PackCoord.key(cx, cy);
    if (chunks.containsKey(k)) return;

    synchronized (genQueue) {
      if (queued.contains(k)) return;
      queued.add(k);
      genQueue.addLast(k);
    }
  }

  /** Commit up to maxNew completed chunks into the cache (call once per frame). */
  public int tickGenerationBudget(int maxNew) {
    int budget = Math.max(0, maxNew);
    int made = 0;

    while (budget > 0) {
      Chunk c = completed.poll();
      if (c == null) break;

      long k = PackCoord.key(c.cx, c.cy);
      if (!chunks.containsKey(k)) {
        chunks.put(k, c);
        made++;
        budget--;
      }
    }

    return made;
  }

  private void workerLoop() {
    for (;;) {
      Long kObj;
      synchronized (genQueue) {
        kObj = genQueue.pollFirst();
        if (kObj != null) {
          queued.remove(kObj);
        }
      }

      if (kObj == null) {
        try { Thread.sleep(2); } catch (InterruptedException ignored) {}
        continue;
      }

      long k = kObj;
      int cx = (int) (k >> 32);
      int cy = (int) k;

      try {
        Chunk c = gen.generate(cx, cy);
        completed.add(c);
      } catch (Throwable ignored) {
        // swallow; keep worker alive
      }
    }
  }

  /** Ensure chunk exists: generates if missing (simulation/streaming path). */
  public Chunk get(int cx, int cy) {
    long k = PackCoord.key(cx, cy);
    Chunk c = chunks.get(k);
    if (c != null) return c;
    c = gen.generate(cx, cy);
    chunks.put(k, c);
    return c;
  }

  /** Peek chunk: returns existing chunk or null, does NOT generate (render path). */
  public Chunk peek(int cx, int cy) {
    long k = PackCoord.key(cx, cy);
    return chunks.get(k);
  }

  /** Clear call-site name for ensure semantics. */
  public Chunk ensure(int cx, int cy) {
    return get(cx, cy);
  }
}
