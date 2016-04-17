package com.catinthedark.shapeshift.units

import com.badlogic.gdx.math.{Affine2, Vector2}
import com.catinthedark.lib.{Deferred, MagicSpriteBatch, SimpleUnit}
import com.catinthedark.shapeshift.Assets
import com.catinthedark.shapeshift.common.Const
import com.catinthedark.shapeshift.view.{IDLE, KILLED, RUNNING, SHOOTING}
import com.catinthedark.shapeshift.view.{IDLE, JUMPING, RUNNING}

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
    shared.enemy.scale = 1f
    
    val distance = shared.enemy.pos.dst(shared.player.pos)
    if (shared.enemy.state == RUNNING) {
      val volume = Const.Balance.distanceToVolume(distance)
      shared.enemy.audio.steps.setVolume(volume)
      shared.enemy.audio.steps.play()
    } else {
      shared.enemy.audio.steps.pause()
    }
  }

  def onJump(data: (Vector2, Float, Float)): Unit = {
    shared.enemy.pos = data._1
    shared.enemy.angle = data._2
    shared.enemy.scale = data._3
    shared.enemy.state = JUMPING
    shared.enemy.audio.steps.pause()
  }
  
  def onShoot(data: (Vector2, String)): Unit = {
    val shootPos = data._1
    val objName = data._2

    val distance = shared.enemy.pos.dst(shootPos)
    
    println(s"Network shot: $shootPos $objName $distance")
    shared.enemy.state = SHOOTING
    objName match {
      case "Enemy" =>
        println("I killed")
        shared.player.state = KILLED
        shared.enemy.audio.shoot.play(Const.Balance.distanceToVolume(distance))
      case "Tree" =>
        println("Tree ricochet")
        shared.enemy.audio.ricochetWood.play(Const.Balance.distanceToVolume(distance))
      case _ =>
        println("Ricochet")
        shared.enemy.audio.ricochet.play(Const.Balance.distanceToVolume(distance))
    }
  }

  def render(delta: Float, magicBatch: MagicSpriteBatch) = {
    magicBatch.drawWithDebug(shared.enemy.texture(delta), shared.enemy.rect, shared.enemy.physRect, angle = shared.enemy.angle, scaleX = shared.enemy.scale, scaleY = shared.enemy.scale)
  }
}
