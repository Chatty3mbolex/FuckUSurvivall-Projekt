package com.yourgame.survival.systems;

import com.yourgame.survival.entity.Entities;
import com.yourgame.survival.entity.EntityType;
import com.yourgame.survival.world.World;

/**
 * WanderQuestGuySystem
 *
 * IMPORTANT: Do not compile/run while implementing (per OpenClaw PLAN).
 *
 * Responsibility (planned):
 * - Ensure exactly one Wander_Quest_Guy exists in HOME area (per savegame).
 * - Wander around castle vicinity (merchant-like wandering).
 * - Attention behavior: stop + look at player when near.
 * - Provide 0-3 quests on interaction; quests refresh every 1-6h real time.
 * - Persist state + offered/accepted quests via SaveManager.
 *
 * This is a TEMPLATE placeholder; implementation will be filled step-by-step.
 */
public final class WanderQuestGuySystem {

    public static final String ENTITY_CODE_NAME = "Wander_Quest_Guy";

    // Deterministic-ish movement RNG (xorshift64* like MerchantSystem).
    private long moveRngState;

    // Cached entity slot (best-effort; validated each tick)
    private int cachedE = -1;

    // Offer state (runtime only; persistence comes in Task 11)
    private float refreshT = 0f;
    private String greeting = "";
    private final com.yourgame.survival.quest.QuestDef[] offers = new com.yourgame.survival.quest.QuestDef[3];
    private int offerCount = 0;

    // ZQS dock (concept flow 4). WQG must only talk to dock.
    private com.yourgame.survival.quest.zqs.dock.WanderQuestGuyDock zqsDock;

    public interface ZqsContextProvider {
        com.yourgame.survival.quest.zqs.runtime.ZqsConversationContext buildCtx();
    }

    private ZqsContextProvider ctxProvider;

    /** Bind ZQS dock created/owned by GameScreen. */
    public void bindZqsDock(com.yourgame.survival.quest.zqs.dock.WanderQuestGuyDock dock) {
        this.zqsDock = dock;
    }

    public void bindZqsContextProvider(ZqsContextProvider p) {
        this.ctxProvider = p;
    }

    public long exportMoveRngState() { return moveRngState; }
    public float exportRefreshT() { return refreshT; }

    public String exportOfferId(int idx) {
        if (idx < 0 || idx >= offerCount) return "";
        com.yourgame.survival.quest.QuestDef q = offers[idx];
        return (q != null) ? q.id : "";
    }

    public int exportOfferCount() { return offerCount; }

    public void importFromSave(com.badlogic.gdx.utils.JsonValue qsys) {
        if (qsys == null) return;
        try {
            moveRngState = qsys.getLong("moveRng", moveRngState);
            refreshT = qsys.getFloat("refreshT", refreshT);
            offerCount = Math.max(0, Math.min(3, qsys.getInt("offerCount", offerCount)));
            for (int i = 0; i < offers.length; i++) offers[i] = null;
            com.badlogic.gdx.utils.JsonValue ids = qsys.get("offerId");
            // Offers are runtime-only (OfferBuffer is NOT persisted). After load, offers must be regenerated.
            offerCount = 0;
            for (int i = 0; i < offers.length; i++) offers[i] = null;
            // greeting is UI-only; reroll on open
            greeting = "";
            cachedE = -1;
        } catch (Throwable ignored) {
            // tolerate corrupt/older saves
        }
    }

    public WanderQuestGuySystem(long worldSeed) {
        long s = worldSeed ^ 0x5155455354475559L; // "QUESTGUY" (ish)
        this.moveRngState = (s != 0L) ? s : 0x9E3779B97F4A7C15L;
    }

    /** Tick called from GameScreen update loop. */
    public void tick(Entities es, World world,
                     float playerX, float playerY,
                     float playerReach,
                     float dt,
                     boolean isHomeArea) {
        if (es == null || world == null) return;
        if (dt <= 0f) return;

        // Requirement (Task 8): only run in HOME.
        if (!isHomeArea) return;

        int e = findOrSpawn(es, world);
        if (e < 0) return;

        // Stop + look at player when within action reach.
        float stop = playerReach + com.yourgame.survival.entity.EntityMetrics.radius(EntityType.WANDER_QUEST_GUY);
        float stopR2 = stop * stop;
        float dx = playerX - es.x[e];
        float dy = playerY - es.y[e];
        float d2 = dx * dx + dy * dy;
        if (d2 <= stopR2) {
            es.vx[e] = 0f;
            es.vy[e] = 0f;
            es.dir[e] = dirFrom(dx, dy, es.dir[e]);
            return;
        }

        // Wander around the HOME landmark zone.
        // Leash: steer back when >80% of wander radius.
        float wr = es.wanderRadius[e];
        if (wr <= 1e-3f) wr = 220f;

        float hdx = es.x[e] - es.homeX[e];
        float hdy = es.y[e] - es.homeY[e];
        float homeDist2 = hdx * hdx + hdy * hdy;

        // pick/keep heading for some seconds
        es.aiT[e] -= dt;
        if (es.aiT[e] <= 0f) {
            es.aiT[e] = 2.5f + nextFloat01() * 4.0f;
            es.rot[e] = nextFloat01() * (float) (Math.PI * 2.0);
        }

        float speed = 26f;

        float desiredVx = (float) Math.cos(es.rot[e]) * speed;
        float desiredVy = (float) Math.sin(es.rot[e]) * speed;

        if (homeDist2 > (wr * wr * 0.64f)) {
            // Past 80% of wander radius - steer back toward home.
            float homeDist = (float) Math.sqrt(homeDist2);
            float pull = Math.min(1f, (homeDist / wr - 0.6f) / 0.4f);
            float invH = (homeDist > 1e-3f) ? (1f / homeDist) : 1f;
            float toHomeX = (-hdx) * invH;
            float toHomeY = (-hdy) * invH;
            desiredVx = desiredVx * (1f - pull) + toHomeX * speed * pull;
            desiredVy = desiredVy * (1f - pull) + toHomeY * speed * pull;
        }

        es.dir[e] = dirFrom(desiredVx, desiredVy, es.dir[e]);

        float accel = 10f;
        float a = Math.min(1f, accel * dt);
        es.vx[e] = es.vx[e] + (desiredVx - es.vx[e]) * a;
        es.vy[e] = es.vy[e] + (desiredVy - es.vy[e]) * a;

        float nx = es.x[e] + es.vx[e] * dt;
        float ny = es.y[e] + es.vy[e] * dt;

        // Movement probes MUST NOT generate chunks.
        boolean water = world.isWaterAtWorldPeek(nx, ny, true);
        boolean coll = world.isBlockedAtWorldPeek(nx, ny, true);
        int bid = world.biomeIdAtWorldPeek(nx, ny, com.yourgame.survival.world.Biome.GRASSLAND.id & 0xff);
        var biome = com.yourgame.survival.world.Biome.byId(bid);
        boolean blocked = water || coll || biome == com.yourgame.survival.world.Biome.LAVA;

        if (!blocked) {
            es.x[e] = nx;
            es.y[e] = ny;
        } else {
            // bounce: repick soon
            es.vx[e] *= 0.35f;
            es.vy[e] *= 0.35f;
            es.aiT[e] = Math.min(es.aiT[e], 0.2f);
        }
    }

    /** Debug-only: force refresh available quests (Shift+F6). */
    public void forceRefreshDebug() {
        refreshT = 0f;
        rollOffers();
    }

    /** Called by UI when opening the popup to re-roll greeting (not offers). */
    public void onPopupOpened() {
        if (zqsDock == null) return;
        rollOffers();
        com.yourgame.survival.quest.zqs.runtime.ZqsConversationContext ctx =
            (ctxProvider != null) ? ctxProvider.buildCtx() : new com.yourgame.survival.quest.zqs.runtime.ZqsConversationContext();
        greeting = zqsDock.buildGreeting(ctx, offerCount);
    }

    public String greeting() { return greeting; }
    public int offerCount() { return offerCount; }
    public com.yourgame.survival.quest.QuestDef offer(int idx) {
        if (idx < 0 || idx >= offerCount) return null;
        return offers[idx];
    }

    public boolean acceptOffer(int idx, com.yourgame.survival.quest.QuestLog log, long runtimeSec) {
        if (log == null) return false;
        com.yourgame.survival.quest.QuestDef q = offer(idx);
        if (q == null) return false;

        // Persist accept through dock.
        if (zqsDock != null) {
            return zqsDock.acceptOffer(q.id, log, runtimeSec);
        }
        return log.accept(q, runtimeSec);
    }

    private int findOrSpawn(Entities es, World world) {
        // Validate cached.
        if (cachedE >= 0 && cachedE < Entities.MAX && es.alive[cachedE] && es.type[cachedE] == EntityType.WANDER_QUEST_GUY) {
            return cachedE;
        }

        // Find existing.
        for (int i = 0; i < Entities.MAX; i++) {
            if (!es.alive[i]) continue;
            if (es.type[i] != EntityType.WANDER_QUEST_GUY) continue;
            cachedE = i;
            return i;
        }

        // Spawn near HOME castle/bridge reserved zone.
        // This mirrors the placement math in JsonAreaWorldLoader.placeHomeCastleAndBridge(...)
        final int centerTx = 174 + (36 / 2); // 192
        final int centerTy = 160 + (28 / 2); // 174
        final float cx = (centerTx + 0.5f) * World.TILE_WORLD;
        final float cy = (centerTy + 0.5f) * World.TILE_WORLD;

        // Try a few offsets around the castle forecourt.
        for (int attempt = 0; attempt < 40; attempt++) {
            float ox = (-60f + nextFloat01() * 120f);
            float oy = (-170f + nextFloat01() * 110f);
            float wx = cx + ox;
            float wy = cy + oy;

            boolean water = world.isWaterAtWorldPeek(wx, wy, true);
            boolean coll = world.isBlockedAtWorldPeek(wx, wy, true);
            int bid = world.biomeIdAtWorldPeek(wx, wy, com.yourgame.survival.world.Biome.GRASSLAND.id & 0xff);
            var biome = com.yourgame.survival.world.Biome.byId(bid);
            boolean blocked = water || coll || biome == com.yourgame.survival.world.Biome.LAVA;
            if (blocked) continue;

            int e = es.spawn(EntityType.WANDER_QUEST_GUY, wx, wy);
            if (e >= 0) {
                es.setAlwaysActive(e, true);
                // Home leash + wander radius.
                es.homeX[e] = wx;
                es.homeY[e] = wy;
                es.wanderRadius[e] = 220f;
                cachedE = e;
                return e;
            }
        }

        return -1;
    }

    private void rollOffers() {
        // Offer refresh timing is governed by ZQS persisted NQ timer state.
        refreshT = 0f;

        // WQG is docking-only: desired offer count is fixed; generator decides the outcome.
        offerCount = 3;
        for (int i = 0; i < offers.length; i++) offers[i] = null;

        com.yourgame.survival.quest.zqs.runtime.ZqsConversationContext ctx = new com.yourgame.survival.quest.zqs.runtime.ZqsConversationContext();
        if (ctxProvider != null) ctx = ctxProvider.buildCtx();

        if (zqsDock == null) return;

        // Ask dock for generated offers (legacy UI QuestDefs).
        com.yourgame.survival.quest.QuestDef[] defs = zqsDock.generateOffers(ctx, offerCount);
        for (int i = 0; i < offers.length; i++) offers[i] = null;
        int m = (defs != null) ? Math.min(defs.length, offers.length) : 0;
        for (int i = 0; i < m; i++) offers[i] = defs[i];
        offerCount = m;
    }

    private String randomGreeting() {
        String[] gs = new String[] {
            "Ah, du schon wieder. Gut.",
            "Komm näher. Ich hab' da was.",
            "Wenn du Zeit hast: Arbeit wartet.",
            "Keine Panik. Nur Quests.",
            "Heute ist ein guter Tag, um was zu erledigen."
        };
        int idx = (int) Math.floor(nextFloat01() * gs.length);
        if (idx < 0) idx = 0;
        if (idx >= gs.length) idx = gs.length - 1;
        return gs[idx];
    }

    private long nextLongMove() {
        long x = moveRngState;
        x ^= x >>> 12;
        x ^= x << 25;
        x ^= x >>> 27;
        moveRngState = x;
        return x * 2685821657736338717L;
    }

    private float nextFloat01() {
        return ((nextLongMove() >>> 40) & 0xFFFFFFL) / (float) (1 << 24);
    }

    private static byte dirFrom(float dx, float dy, byte last) {
        float ax = Math.abs(dx);
        float ay = Math.abs(dy);
        if (ax < 1e-3f && ay < 1e-3f) return last;
        if (ax > ay) return (byte) (dx > 0 ? 1 : 3);
        return (byte) (dy > 0 ? 0 : 2);
    }
}
