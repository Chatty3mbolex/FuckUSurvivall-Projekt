package com.yourgame.survival.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.yourgame.survival.tools.asseteditor.scan.ProjectRoot;

import java.io.File;

/**
 * Loads atlases from the project filesystem first (desktop/editor), with a safe fallback.
 *
 * Layout (target):
 * - assets/atlas/static.atlas
 * - assets/atlas/living.atlas
 * - assets/atlas/ui.atlas
 *
 * Fallback:
 * - assets/atlas/game.atlas (legacy single-atlas)
 */
public final class AtlasLoader {
  private AtlasLoader() {}

  public static TextureAtlas loadPreferFs(String atlasBaseName) {
    FileHandle fh = null;

    // 1) Prefer filesystem atlas in project root.
    try {
      File root = ProjectRoot.find();
      if (root != null) {
        File fsAtlas = new File(root, "assets/atlas/" + atlasBaseName + ".atlas");
        if (fsAtlas.exists()) {
          fh = Gdx.files.absolute(fsAtlas.getAbsolutePath());
        }
      }
    } catch (Throwable ignored) {
      fh = null;
    }

    // 2) Fallback to internal packaged asset.
    if (fh == null) {
      try {
        String internalPath = "atlas/" + atlasBaseName + ".atlas";
        if (Gdx.files.internal(internalPath).exists()) {
          fh = Gdx.files.internal(internalPath);
        }
      } catch (Throwable ignored) {
        fh = null;
      }
    }

    // 3) NO legacy fallback.
    // If an atlas is missing, we fail fast so missing/incorrect asset pipelines are obvious.
    if (fh == null) {
      throw new IllegalStateException("Missing atlas: '" + atlasBaseName + "' (expected assets/atlas/" + atlasBaseName + ".atlas)");
    }

    return new TextureAtlas(fh);
  }
}
