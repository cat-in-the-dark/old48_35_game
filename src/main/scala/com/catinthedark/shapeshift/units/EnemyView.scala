package com.catinthedark.shapeshift.units

import com.badlogic.gdx.math.{Affine2, Vector2}
import com.catinthedark.lib.{Deferred, MagicSpriteBatch, SimpleUnit}
import com.catinthedark.shapeshift.Assets
import com.catinthedark.shapeshift.view.{IDLE, KILLED, RUNNING, SHOOTING}

abstract class EnemyView(val shared: Shared1) extends SimpleUnit with Deferred {
  def onMove(data: (Vector2, Float, Boolean)): Unit = {
    if (shared.enemy.state == KILLED) return
    
    shared.enemy.pos = data._1
    shared.enemy.angle = data._2
    shared.enemy.state = if (data._3) {
      IDLE
    } else {
      RUNNING
    }
  }
  
  def onShoot(data: (Vector2, String)): Unit = {
    val shootPos = data._1
    val objName = data._2
    
    println(s"Network shot: $shootPos $objName")
    shared.enemy.state = SHOOTING
    objName match {
      case "Enemy" =>
        println("I killed")
        shared.player.state = KILLED
        shared.enemy.audio.shoot.play()
      case "Tree" =>
        println("Tree ricochet")
        shared.enemy.audio.ricochetWood.play()
      case _ =>
        println("Ricochet")
        shared.enemy.audio.ricochet.play()
    }
  }

  def render(delta: Float, magicBatch: MagicSpriteBatch) = {
    magicBatch.drawWithDebug(shared.enemy.texture(delta), shared.enemy.rect, shared.enemy.physRect, angle = shared.enemy.angle)
  }
}
