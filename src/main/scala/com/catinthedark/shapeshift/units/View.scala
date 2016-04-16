package com.catinthedark.shapeshift.units

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.catinthedark.lib._
import com.catinthedark.shapeshift.common.Const

/**
  * Created by over on 02.01.15.
  */
abstract class View(val shared: Shared1) extends SimpleUnit with Deferred {
  val batch = new SpriteBatch()
  val magicBatch = new MagicSpriteBatch(Const.debugEnabled())

  val enemyView = new EnemyView(shared, Const.UI.enemyYRange, Const.UI.enemyParallaxSpeed) with LocalDeferred

//  shared.shared0.networkControl.onMovePipe.ports += enemyView.onMove
//  shared.shared0.networkControl.onShootPipe.ports += enemyView.onShoot
//  shared.shared0.networkControl.onAlivePipe.ports += enemyView.onAlive

  override def onActivate() = {

  }
  
  def onMoveLeft(u: Unit): Unit = {
    val speed = Const.gamerSpeed()
    shared.player.pos.x -= speed
  }

  def onMoveRight(u: Unit): Unit = {
    val speed = Const.gamerSpeed()
    shared.player.pos.x += speed
  }

  def onMoveForward(u: Unit): Unit = {
    val speed = Const.gamerSpeed()
    shared.player.pos.y += speed
  }

  def onMoveBackward(u: Unit): Unit = {
    val speed = Const.gamerSpeed()
    shared.player.pos.y -= speed
  }

  override def run(delta: Float) = {
    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    magicBatch managed { batch =>
      magicBatch.drawWithDebug(shared.player.texture(delta), shared.player.rect, shared.player.rect)
    }
  }

  override def onExit() = {
  }
}
