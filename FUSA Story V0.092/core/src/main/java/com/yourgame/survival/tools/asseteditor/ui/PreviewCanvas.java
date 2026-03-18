package com.yourgame.survival.tools.asseteditor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.yourgame.survival.tools.asseteditor.model.CollisionMeta;

/**
 * Shows the selected region source pixmap and overlays auto-collision.
 * Also supports pipette (background color pick) and zoom.
 */
public final class PreviewCanvas extends Actor {
  private Texture tex;
  private TextureRegion region;
  private Pixmap pixmap;

  private String missingName;

  private final ShapeRenderer shapes = new ShapeRenderer();

  private float zoom = 8f; // pixel-art-ish
  private float panX = 0f;
  private float panY = 0f;

  // Crop rect in SOURCE pixel coordinates
  private int cropX = 0, cropY = 0, cropW = 0, cropH = 0;
  private boolean cropEditEnabled = false;
  private int activeHandle = -1; // -1 none, 0..7 handles, 8 = move box
  private int dragStartPX, dragStartPY;
  private int startCropX, startCropY, startCropW, startCropH;

  // Fit-to-window is requested after content is set (executed in draw-thread once).
  private boolean requestFit = false;

  private boolean pipetteMode = false;
  private Color pickedBg = new Color(0, 0, 0, 0);
  private int pickedBgX = -1, pickedBgY = -1;

  private CollisionMeta draft;

  // Preview-only transform state (rotation/flip). Rotation is in 90° steps.
  private int rot90 = 0;
  private boolean flipX = false;
  private boolean flipY = false;

  public interface Listener {
    void onBgPicked(Color bg);
  }
  private Listener listener;

  public PreviewCanvas() {
    addListener(new InputListener() {
      private float lastX, lastY;

      @Override
      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        lastX = x; lastY = y;

        // Crop editing has priority over everything else.
        if (cropEditEnabled && button == com.badlogic.gdx.Input.Buttons.LEFT && pixmap != null) {
          Vector2 p = screenToPixel(x, y);
          int px = MathUtils.clamp((int)p.x, 0, pixmap.getWidth() - 1);
          int py = MathUtils.clamp((int)p.y, 0, pixmap.getHeight() - 1);
          activeHandle = hitTestHandleOrBox(px, py);
          if (activeHandle != -1) {
            dragStartPX = px;
            dragStartPY = py;
            startCropX = cropX; startCropY = cropY; startCropW = cropW; startCropH = cropH;
            return true;
          }
        }
        // Pipette marker/overlay is not rotated yet; keep disabled under transform.
        if (pipetteMode && (rot90 != 0 || flipX || flipY)) return false;
        // When crop edit is active, pipette is disabled.
        if (pipetteMode && cropEditEnabled) return false;
        if (pipetteMode && pixmap != null) {
          Vector2 p = screenToPixel(x, y);
          int px = MathUtils.clamp((int)p.x, 0, pixmap.getWidth() - 1);
          int py = MathUtils.clamp((int)p.y, 0, pixmap.getHeight() - 1);
          int rgba = pixmap.getPixel(px, py);
          int a = (rgba) & 0xff;
          int b = (rgba >>> 8) & 0xff;
          int g = (rgba >>> 16) & 0xff;
          int r = (rgba >>> 24) & 0xff;
          pickedBg.set(r/255f, g/255f, b/255f, a/255f);
          pickedBgX = px; pickedBgY = py;
          if (listener != null) listener.onBgPicked(new Color(pickedBg));
          return true;
        }
        return false;
      }

      @Override
      public void touchDragged(InputEvent event, float x, float y, int pointer) {
        if (cropEditEnabled && activeHandle != -1 && pixmap != null) {
          Vector2 p = screenToPixel(x, y);
          int px = (int)p.x;
          int py = (int)p.y;
          applyCropDrag(px, py);
          clampCropToImage();
          return;
        }

        if (!pipetteMode) {
          float dx = x - lastX;
          float dy = y - lastY;
          panX += dx;
          panY += dy;
          lastX = x; lastY = y;
        }
      }

      @Override
      public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        activeHandle = -1;
      }

      @Override
      public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
        float amt = amountY;
        zoom *= (amt > 0 ? 0.9f : 1.1f);
        zoom = MathUtils.clamp(zoom, 1f, 40f);
        return true;
      }
    });
  }

  public void setListener(Listener l) {
    this.listener = l;
  }

  public void setPipetteMode(boolean on) {
    this.pipetteMode = on;
  }

  public void setCropEditEnabled(boolean enabled) {
    this.cropEditEnabled = enabled;
    if (enabled) this.pipetteMode = false;
  }

  public boolean isCropEditEnabled() {
    return cropEditEnabled;
  }

  public void setCropRect(int x, int y, int w, int h) {
    this.cropX = x;
    this.cropY = y;
    this.cropW = w;
    this.cropH = h;
    clampCropToImage();
  }

  public int getCropX() { return cropX; }
  public int getCropY() { return cropY; }
  public int getCropW() { return cropW; }
  public int getCropH() { return cropH; }

  public int getRot90() { return rot90; }
  public boolean getFlipX() { return flipX; }
  public boolean getFlipY() { return flipY; }

  public void fitToContent() {
    requestFit = true;
  }

  public boolean isPipetteMode() {
    return pipetteMode;
  }

  public void setDraft(CollisionMeta draft) {
    this.draft = draft;
  }

  public void setMissing(String name) {
    this.missingName = name;
    setPixmap(null);
  }

  public void rotate90() {
    rot90 = (rot90 + 1) & 3;
  }

  public void flipX() {
    flipX = !flipX;
  }

  public void flipY() {
    flipY = !flipY;
  }

  /**
   * Applies the current preview transform to a Pixmap and returns a NEW pixmap.
   * Caller owns/disposes the returned pixmap.
   */
  public Pixmap applyTransform(Pixmap src) {
    if (src == null) return null;

    int w0 = src.getWidth();
    int h0 = src.getHeight();
    int w1 = (rot90 % 2 == 0) ? w0 : h0;
    int h1 = (rot90 % 2 == 0) ? h0 : w0;

    Pixmap out = new Pixmap(w1, h1, src.getFormat());

    for (int y = 0; y < h0; y++) {
      for (int x = 0; x < w0; x++) {
        int rgba = src.getPixel(x, y);

        // base rotated coords
        int rx, ry;
        switch (rot90 & 3) {
          case 1: // 90
            rx = h0 - 1 - y;
            ry = x;
            break;
          case 2: // 180
            rx = w0 - 1 - x;
            ry = h0 - 1 - y;
            break;
          case 3: // 270
            rx = y;
            ry = w0 - 1 - x;
            break;
          default: // 0
            rx = x;
            ry = y;
            break;
        }

        // apply flips in output space
        int fx = flipX ? (w1 - 1 - rx) : rx;
        int fy = flipY ? (h1 - 1 - ry) : ry;

        out.drawPixel(fx, fy, rgba);
      }
    }

    return out;
  }

  public Pixmap getPixmap() {
    return pixmap;
  }

  public void setPixmap(Pixmap pm) {
    if (tex != null) tex.dispose();
    if (pixmap != null) pixmap.dispose();
    pixmap = pm;
    if (pm != null) {
      // Guard: creating huge GL textures can hard-crash drivers on some systems.
      if (pm.getWidth() > 4096 || pm.getHeight() > 4096) {
        pixmap.dispose();
        pixmap = null;
        tex = null;
        region = null;
        missingName = "(too large)";
        return;
      }
      missingName = null;
      tex = new Texture(pm);
      tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
      region = new TextureRegion(tex);
      zoom = 8f;
      panX = 0f;
      panY = 0f;
      cropX = 0;
      cropY = 0;
      cropW = pm.getWidth();
      cropH = pm.getHeight();
      requestFit = true;
    } else {
      tex = null;
      region = null;
    }
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    if (requestFit) {
      applyFitToWindow();
      requestFit = false;
    }

    batch.end();

    // Background
    Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
    shapes.setProjectionMatrix(batch.getProjectionMatrix());
    shapes.setTransformMatrix(batch.getTransformMatrix());
    shapes.begin(ShapeRenderer.ShapeType.Filled);
    shapes.setColor(0.08f, 0.08f, 0.08f, 1f);
    shapes.rect(getX(), getY(), getWidth(), getHeight());
    shapes.end();
    Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);

    batch.begin();

    if (missingName != null) {
      // No source image available; keep the background and border only.
      batch.end();
      shapes.setProjectionMatrix(batch.getProjectionMatrix());
      shapes.setTransformMatrix(batch.getTransformMatrix());
      shapes.begin(ShapeRenderer.ShapeType.Line);
      shapes.setColor(0.25f, 0.25f, 0.25f, 1f);
      shapes.rect(getX(), getY(), getWidth(), getHeight());
      shapes.end();
      batch.begin();
      return;
    }

    if (region != null) {
      float rw = region.getRegionWidth();
      float rh = region.getRegionHeight();

      float cx = getX() + getWidth() * 0.5f + panX;
      float cy = getY() + getHeight() * 0.5f + panY;

      float drawW = rw * zoom;
      float drawH = rh * zoom;
      float dx = cx - drawW * 0.5f;
      float dy = cy - drawH * 0.5f;

      float sx = flipX ? -1f : 1f;
      float sy = flipY ? -1f : 1f;
      float rotDeg = (rot90 & 3) * 90f;
      // draw with origin in the middle
      batch.draw(region,
        dx, dy,
        drawW * 0.5f, drawH * 0.5f,
        drawW, drawH,
        sx, sy,
        rotDeg);

      boolean transformed = (rot90 != 0 || flipX || flipY);

      boolean allowOverlays = !transformed && !cropEditEnabled;

      // Overlay collision (disabled under transform because it would be incorrect)
      batch.end();
      shapes.setProjectionMatrix(batch.getProjectionMatrix());
      shapes.setTransformMatrix(batch.getTransformMatrix());
      shapes.begin(ShapeRenderer.ShapeType.Line);
      shapes.setColor(0f, 1f, 0f, 1f);

      if (allowOverlays && draft != null) {
        // rects (tiles)
        if (draft.rects != null && draft.rects.size > 0) {
          for (CollisionMeta.RectI r : draft.rects) {
            float rx = dx + r.x * zoom;
            float ry = dy + r.y * zoom;
            shapes.rect(rx, ry, r.w * zoom, r.h * zoom);
          }
        }
        // polys
        if (draft.polys != null && draft.polys.size > 0) {
          for (CollisionMeta.Poly p : draft.polys) {
            if (p == null || p.pts == null || p.pts.length < 6) continue;
            for (int i = 0; i < p.pts.length; i += 2) {
              int j = (i + 2) % p.pts.length;
              float x1 = dx + p.pts[i] * zoom;
              float y1 = dy + p.pts[i + 1] * zoom;
              float x2 = dx + p.pts[j] * zoom;
              float y2 = dy + p.pts[j + 1] * zoom;
              shapes.line(x1, y1, x2, y2);
            }
          }
        }
      }

      // Pipette marker
      if (allowOverlays && pipetteMode && pickedBgX >= 0) {
        shapes.setColor(1f, 0.4f, 0f, 1f);
        float mx = dx + pickedBgX * zoom;
        float my = dy + pickedBgY * zoom;
        shapes.rect(mx - 2, my - 2, 4, 4);
      }

      // Crop box overlay (disabled under transform)
      if (cropEditEnabled) {
        shapes.setColor(0.2f, 0.75f, 1f, 1f);
        if (!transformed) {
          drawCropOverlayShapesUntransformed(dx, dy);
        } else {
          drawCropOverlayShapesTransformed(dx, dy, drawW, drawH, sx, sy, rotDeg);
        }
      }

      shapes.end();
      batch.begin();

      // Border
      batch.end();
      shapes.begin(ShapeRenderer.ShapeType.Line);
      shapes.setColor(0.25f, 0.25f, 0.25f, 1f);
      shapes.rect(getX(), getY(), getWidth(), getHeight());
      shapes.end();
      batch.begin();
    }
  }

  public void disposeResources() {
    if (tex != null) tex.dispose();
    if (pixmap != null) pixmap.dispose();
    shapes.dispose();
  }

  private Vector2 screenToPixel(float localX, float localY) {
    if (pixmap == null) return new Vector2();

    float rw = pixmap.getWidth();
    float rh = pixmap.getHeight();

    // In actor-local coordinates.
    float cx = getWidth() * 0.5f + panX;
    float cy = getHeight() * 0.5f + panY;
    float drawW = rw * zoom;
    float drawH = rh * zoom;

    // Point relative to draw-center.
    float vx = localX - cx;
    float vy = localY - cy;

    // Inverse rotation (undo draw-time rotation).
    float rotDeg = (rot90 & 3) * 90f;
    if (rotDeg != 0f) {
      float rad = -rotDeg * MathUtils.degreesToRadians;
      float cos = MathUtils.cos(rad);
      float sin = MathUtils.sin(rad);
      float rx = vx * cos - vy * sin;
      float ry = vx * sin + vy * cos;
      vx = rx;
      vy = ry;
    }

    // Inverse flip (undo draw-time negative scaling).
    float sx = flipX ? -1f : 1f;
    float sy = flipY ? -1f : 1f;
    vx *= sx;
    vy *= sy;

    // Back to top-left of the untransformed draw box.
    float ux = vx + drawW * 0.5f;
    float uy = vy + drawH * 0.5f;

    float px = ux / zoom;
    float py = uy / zoom;
    return new Vector2(px, py);
  }

  private void applyFitToWindow() {
    int iw = getImageW();
    int ih = getImageH();
    if (iw <= 0 || ih <= 0) return;
    // If rotated by 90° or 270°, bounding box is swapped.
    int effW = ((rot90 & 1) == 1) ? ih : iw;
    int effH = ((rot90 & 1) == 1) ? iw : ih;
    float vw = getWidth();
    float vh = getHeight();
    if (vw <= 2f || vh <= 2f) return;
    float z = Math.min(vw / effW, vh / effH);
    zoom = MathUtils.clamp(z, 0.1f, 40f);
    panX = 0f;
    panY = 0f;
  }

  private int getImageW() {
    if (pixmap != null) return pixmap.getWidth();
    if (region != null) return region.getRegionWidth();
    return 0;
  }

  private int getImageH() {
    if (pixmap != null) return pixmap.getHeight();
    if (region != null) return region.getRegionHeight();
    return 0;
  }

  private void clampCropToImage() {
    int iw = getImageW();
    int ih = getImageH();
    if (iw <= 0 || ih <= 0) { cropX = cropY = cropW = cropH = 0; return; }
    if (cropW <= 0) cropW = iw;
    if (cropH <= 0) cropH = ih;
    if (cropW > iw) cropW = iw;
    if (cropH > ih) cropH = ih;
    if (cropX < 0) cropX = 0;
    if (cropY < 0) cropY = 0;
    if (cropX + cropW > iw) cropX = iw - cropW;
    if (cropY + cropH > ih) cropY = ih - cropH;
  }

  private int hitTestHandleOrBox(int px, int py) {
    float rPx = Math.max(2f, 8f / Math.max(0.1f, zoom));
    int x0 = cropX, y0 = cropY, x1 = cropX + cropW, y1 = cropY + cropH;
    int mx = (x0 + x1) / 2;
    int my = (y0 + y1) / 2;
    int[][] pts = new int[][]{
      {x0, y0}, {mx, y0}, {x1, y0},
      {x0, my}, {x1, my},
      {x0, y1}, {mx, y1}, {x1, y1}
    };
    for (int i = 0; i < pts.length; i++) {
      float dx = px - pts[i][0];
      float dy = py - pts[i][1];
      if (dx * dx + dy * dy <= rPx * rPx) return i;
    }
    if (px >= x0 && px <= x1 && py >= y0 && py <= y1) return 8;
    return -1;
  }

  private void applyCropDrag(int px, int py) {
    int dx = px - dragStartPX;
    int dy = py - dragStartPY;

    int x = startCropX;
    int y = startCropY;
    int w = startCropW;
    int h = startCropH;
    int x2 = x + w;
    int y2 = y + h;

    switch (activeHandle) {
      case 8: // move
        cropX = x + dx;
        cropY = y + dy;
        cropW = w;
        cropH = h;
        return;
      case 0: x += dx; y += dy; break;
      case 1: y += dy; break;
      case 2: x2 += dx; y += dy; break;
      case 3: x += dx; break;
      case 4: x2 += dx; break;
      case 5: x += dx; y2 += dy; break;
      case 6: y2 += dy; break;
      case 7: x2 += dx; y2 += dy; break;
      default: return;
    }

    int nx = Math.min(x, x2);
    int ny = Math.min(y, y2);
    int nw = Math.abs(x2 - x);
    int nh = Math.abs(y2 - y);
    cropX = nx;
    cropY = ny;
    cropW = Math.max(1, nw);
    cropH = Math.max(1, nh);
  }

  private void drawCropOverlayShapesUntransformed(float dx, float dy) {
    float x = dx + cropX * zoom;
    float y = dy + cropY * zoom;
    float w = cropW * zoom;
    float h = cropH * zoom;

    shapes.rect(x, y, w, h);

    float hs = 6f;
    float[][] pts = new float[][]{
      {x, y}, {x + w * 0.5f, y}, {x + w, y},
      {x, y + h * 0.5f}, {x + w, y + h * 0.5f},
      {x, y + h}, {x + w * 0.5f, y + h}, {x + w, y + h}
    };
    for (float[] p : pts) {
      shapes.rect(p[0] - hs * 0.5f, p[1] - hs * 0.5f, hs, hs);
    }
  }

  private void drawCropOverlayShapesTransformed(float dx, float dy, float drawW, float drawH,
                                               float sx, float sy, float rotDeg) {
    // Build 8 handle points + rect corners in untransformed draw space, then apply same flip+rotation
    // around the draw-center as the sprite draw call.
    float ox = dx + drawW * 0.5f;
    float oy = dy + drawH * 0.5f;

    float x0 = dx + cropX * zoom;
    float y0 = dy + cropY * zoom;
    float x1 = dx + (cropX + cropW) * zoom;
    float y1 = dy + (cropY + cropH) * zoom;
    float mx = (x0 + x1) * 0.5f;
    float my = (y0 + y1) * 0.5f;

    float[][] pts = new float[][]{
      {x0, y0}, {mx, y0}, {x1, y0},
      {x0, my}, {x1, my},
      {x0, y1}, {mx, y1}, {x1, y1}
    };

    // Rect corners (for drawing the box)
    float[][] corners = new float[][]{
      {x0, y0}, {x1, y0}, {x1, y1}, {x0, y1}
    };

    // Precompute rotation
    float rad = rotDeg * MathUtils.degreesToRadians;
    float cos = MathUtils.cos(rad);
    float sin = MathUtils.sin(rad);

    // Draw box as 4 lines between transformed corners
    float[] c = new float[8];
    for (int i = 0; i < 4; i++) {
      float[] p = transformDrawPoint(corners[i][0], corners[i][1], ox, oy, sx, sy, cos, sin);
      c[i * 2] = p[0];
      c[i * 2 + 1] = p[1];
    }
    shapes.line(c[0], c[1], c[2], c[3]);
    shapes.line(c[2], c[3], c[4], c[5]);
    shapes.line(c[4], c[5], c[6], c[7]);
    shapes.line(c[6], c[7], c[0], c[1]);

    // Handles as small screen-space rects centered at transformed handle points
    float hs = 6f;
    for (float[] p : pts) {
      float[] tp = transformDrawPoint(p[0], p[1], ox, oy, sx, sy, cos, sin);
      shapes.rect(tp[0] - hs * 0.5f, tp[1] - hs * 0.5f, hs, hs);
    }
  }

  private float[] transformDrawPoint(float x, float y, float ox, float oy,
                                    float sx, float sy, float cos, float sin) {
    float vx = x - ox;
    float vy = y - oy;
    // Apply same flip scaling as sprite draw
    vx *= sx;
    vy *= sy;
    // Apply rotation
    float rx = vx * cos - vy * sin;
    float ry = vx * sin + vy * cos;
    return new float[]{ox + rx, oy + ry};
  }

}
