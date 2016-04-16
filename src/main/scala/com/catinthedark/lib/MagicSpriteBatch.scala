package com.catinthedark.lib

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{TextureRegion, SpriteBatch}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.{Matrix4, Rectangle}

class MagicSpriteBatch(debugOn: => Boolean) extends SpriteBatch {
  val debug = new ShapeRenderer()

  def drawWithDebug(t: TextureRegion, viewPos: Rectangle, physPos: Rectangle, centerX: Boolean = true, centerY: Boolean = true, angle: Float = 0f): Unit = {
    draw(t, 
      viewPos.x - (if (centerX) t.getRegionWidth / 2 else 0),
      viewPos.y - (if (centerX) t.getRegionHeight / 2 else 0),
      t.getRegionWidth / 2f,
      t.getRegionHeight / 2f,
      t.getRegionWidth,
      t.getRegionHeight,
      1f, 1f, angle, true)

    if (debugOn) debug.rect(
      physPos.x - (if (centerX) physPos.width / 2 else 0),
      physPos.y - (if (centerX) physPos.height / 2 else 0),
      physPos.width, physPos.height)
  }

  def drawWithDebugTex(t: Texture, viewPos: Rectangle, physPos: Rectangle, centerX: Boolean = true, centerY: Boolean = true): Unit = {
    draw(t, viewPos.x - (if (centerX) t.getWidth / 2 else 0),
      viewPos.y - (if (centerX) t.getHeight / 2 else 0))

    if (debugOn) debug.rect(
      physPos.x - (if (centerX) physPos.width / 2 else 0),
      physPos.y - (if (centerX) physPos.height / 2 else 0),
      physPos.width, physPos.height)
  }

  def managed(f: MagicSpriteBatch => Unit): Unit = {
    debug.begin(ShapeType.Line)
    begin()
    f(this)
    end()
    debug.end()
  }

  def drawCentered(tex: TextureRegion, x: Float, y: Float, centerX: Boolean = true, centerY: Boolean = true) =
    draw(tex,
      if (centerX) x - tex.getRegionWidth / 2 else x,
      if (centerY) y - tex.getRegionHeight / 2 else y)

  def drawCircleCentered(t: Texture, x: Float, y: Float, rad: Float, centerX: Boolean = true, centerY: Boolean = true) = {
    draw(t, x - (if (centerX) t.getWidth / 2 else 0),
      y - (if (centerX) t.getHeight / 2 else 0))

    if (debugOn) debug.circle(x, y, rad)
  }

  override def setProjectionMatrix(projection: Matrix4): Unit = {
    super.setProjectionMatrix(projection)
    debug.setProjectionMatrix(projection)
  }
}
