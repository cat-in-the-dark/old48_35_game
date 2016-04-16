package com.catinthedark.shapeshift.view

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

class HudBar(val max: Int, vertical: Boolean = false) {
  def render(shapeRenderer: ShapeRenderer, value: Int, pos: Vector2, wh: Vector2): Unit = {
    val (w, h) = if (vertical) (wh.x, wh.y * value / max) else (wh.x * value / max, wh.y)
    shapeRenderer.rect(pos.x, pos.y, w, h)
  }
}

