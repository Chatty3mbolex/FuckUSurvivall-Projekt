package com.yourgame.survival.tools.asseteditor.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.yourgame.survival.tools.asseteditor.scan.ProjectRoot;

import java.io.File;

public final class MetaIO {
  private MetaIO() {}

  private static final Json JSON = new Json();

  public static FileHandle resolveTarget(String regionName) {
    File root = ProjectRoot.find();

    // Prefer assets/atlas/src/<region>.meta.json ("where original came from")
    File srcPngFs = new File(root, "assets/atlas/src/" + regionName + ".png");
    if (srcPngFs.exists() || Gdx.files.internal("atlas/src/" + regionName + ".png").exists()) {
      return Gdx.files.absolute(new File(root, "assets/atlas/src/" + regionName + ".meta.json").getAbsolutePath());
    }

    // Fallback next to atlas
    return Gdx.files.absolute(new File(root, "assets/atlas/" + regionName + ".meta.json").getAbsolutePath());
  }

  public static CollisionMeta load(String regionName) {
    FileHandle f = resolveTarget(regionName);
    if (!f.exists()) return null;
    try {
      return JSON.fromJson(CollisionMeta.class, f.readString("UTF-8"));
    } catch (Throwable t) {
      return null;
    }
  }

  public static boolean save(CollisionMeta meta) {
    if (meta == null || meta.regionName == null) return false;
    FileHandle f = resolveTarget(meta.regionName);
    try {
      f.writeString(JSON.prettyPrint(meta), false, "UTF-8");
      return true;
    } catch (Throwable t) {
      return false;
    }
  }
}
