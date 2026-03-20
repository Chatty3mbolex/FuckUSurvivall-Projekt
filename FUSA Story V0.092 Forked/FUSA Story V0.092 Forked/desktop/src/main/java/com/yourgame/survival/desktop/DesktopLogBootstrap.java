package com.yourgame.survival.desktop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Desktop-only log bootstrap.
 *
 * Goal: always write a runtime log + crash stacktraces to an ERRORLOG folder while the game runs.
 *
 * Notes:
 * - We intentionally redirect System.out/System.err so we capture println-style debug output too.
 * - We also install a default UncaughtExceptionHandler as a last line of defense.
 */
final class DesktopLogBootstrap {
  private DesktopLogBootstrap() {}

  static void init() {
    try {
      String ts = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

      // Prefer logging inside the actual game/project root (where assets/ lives), not some arbitrary cwd.
      File base = findProjectRoot();
      if (base == null) base = new File(System.getProperty("user.dir"));

      File dir = new File(base, "ERRORLOG");
      if (!dir.exists()) dir.mkdirs();

      File logFile = new File(dir, "runtime_" + ts + ".log");
      FileOutputStream fos = new FileOutputStream(logFile, true);
      PrintStream ps = new PrintStream(fos, true, StandardCharsets.UTF_8);

      // Redirect standard streams.
      System.setOut(ps);
      System.setErr(ps);

      // Make sure uncaught exceptions are written.
      Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
        try {
          System.err.println("\n=== UNCAUGHT EXCEPTION ===");
          System.err.println("Thread: " + ((t != null) ? t.getName() : "<null>"));
          if (e != null) e.printStackTrace(System.err);
          System.err.println("=== END UNCAUGHT EXCEPTION ===\n");
          System.err.flush();
        } catch (Throwable ignored) {
          // Nothing else we can do.
        }
      });

      System.out.println("=== Runtime log started: " + ts + " ===");
      System.out.println("cwd=" + System.getProperty("user.dir"));
      System.out.println("logDir=" + dir.getAbsolutePath());
      System.out.flush();
    } catch (Throwable ignored) {
      // Logging must never prevent the game from starting.
    }
  }

  /** Walk upwards from user.dir to find a folder that contains an 'assets' directory. */
  private static File findProjectRoot() {
    try {
      File d = new File(System.getProperty("user.dir")).getCanonicalFile();
      for (int i = 0; i < 12 && d != null; i++) {
        File assets = new File(d, "assets");
        if (assets.exists() && assets.isDirectory()) return d;
        d = d.getParentFile();
      }
    } catch (Throwable ignored) {}
    return null;
  }
}
