package com.catinthedark.shapeshift.units

import com.badlogic.gdx.math.Vector2
import com.catinthedark.lib.{Deferred, MagicSpriteBatch, SimpleUnit}
import com.catinthedark.shapeshift.view.{IDLE, RUNNING}

abstract class EnemyView(val shared: Shared1) extends SimpleUnit with Deferred {
  def onMove(data: (Vector2, Float, Boolean)): Unit = {
    shared.enemy.pos = data._1
    shared.enemy.angle = data._2
    shared.enemy.state = if (data._3) {
      IDLE
    } else {
      RUNNING
    }
  }

  def render(delta: Float, magicBatch: MagicSpriteBatch) = {
    magicBatch.drawWithDebug(shared.enemy.texture(delta), shared.enemy.rect, shared.enemy.physRect)
  }
}
