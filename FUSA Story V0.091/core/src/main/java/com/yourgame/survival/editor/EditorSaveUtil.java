package com.yourgame.survival.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Editor save helpers: backups + atomic writes. */
public final class EditorSaveUtil {
  private EditorSaveUtil() {}

  public static String timestamp() {
    return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
  }

  /** Backup a local (Gdx.files.local) file if it exists. Returns backup path or null. */
  public static String backupLocalIfExists(String localPath) {
    try {
      FileHandle fh = Gdx.files.local(localPath);
      if (fh == null || !fh.exists()) return null;

      Path src = fh.file().toPath();
      String bakName = src.getFileName().toString() + ".bak." + timestamp();
      Path dst = src.resolveSibling(bakName);
      Files.copy(src, dst, StandardCopyOption.COPY_ATTRIBUTES);
      return dst.toString();
    } catch (Throwable ignored) {
      return null;
    }
  }

  /** Backup a local file to a specific backup path (best-effort). */
  public static void backupLocalTo(String localPath, String backupLocalPath) {
    try {
      FileHandle fh = Gdx.files.local(localPath);
      if (fh == null || !fh.exists()) return;
      FileHandle out = Gdx.files.local(backupLocalPath);
      out.parent().mkdirs();
      Files.copy(fh.file().toPath(), out.file().toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
    } catch (Throwable ignored) {}
  }

  /** Atomic write string to Gdx local file path (temp + move). */
  public static void atomicWriteStringLocal(String localPath, String content) throws IOException {
    FileHandle fh = Gdx.files.local(localPath);
    fh.parent().mkdirs();

    Path target = fh.file().toPath();
    Path tmp = target.resolveSibling(target.getFileName().toString() + ".tmp");

    Files.writeString(tmp, content == null ? "" : content, StandardCharsets.UTF_8);

    // Try atomic move first; fall back to replace-existing.
    try {
      Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    } catch (Throwable ignored) {
      Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  /** Atomic write PNG (temp + move). */
  public static void atomicWritePngLocal(String localPath, Pixmap pm) throws IOException {
    if (pm == null) return;

    FileHandle fh = Gdx.files.local(localPath);
    fh.parent().mkdirs();

    Path target = fh.file().toPath();
    Path tmp = target.resolveSibling(target.getFileName().toString() + ".tmp.png");

    // PixmapIO needs a FileHandle. Write to temp, then move.
    FileHandle tmpFh = Gdx.files.absolute(tmp.toString());
    PixmapIO.writePNG(tmpFh, pm);

    try {
      Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    } catch (Throwable ignored) {
      Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
    }
  }
}

