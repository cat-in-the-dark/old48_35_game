package com.catinthedark.shapeshift.units

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{Color, GL20}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.catinthedark.shapeshift.Assets
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

  def drawTreeLayer(layer: MapLayer) = {
    val trees = layer.getObjects.iterator()
    while (trees.hasNext) {
      val tree = trees.next()
      val x = tree.getProperties.get("x", classOf[Float])
      val y = tree.getProperties.get("y", classOf[Float])
    }
  }

  override def run(delta: Float) = {
    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)


    magicBatch managed { batch =>
      magicBatch.drawWithDebug(shared.player.texture(delta), shared.player.rect, shared.player.rect)
    }

    val layers = Assets.Maps.map1.getLayers
    drawTreeLayer(layers.get("tree1"))
    drawTreeLayer(layers.get("tree2"))
    drawTreeLayer(layers.get("tree3"))

    val shapeRenderer = new ShapeRenderer()
    shapeRenderer.begin(ShapeType.Filled)
    shapeRenderer.setColor(Color.RED)
    var x = 500
    var y = 300
    var x1 = Gdx.input.getX()
    var y1 = Const.Projection.height.toInt - Gdx.input.getY()
    var phi = 60f
    var dx = x1 - x
    var dy = y1 - y
    var alpha = Math.atan2(dy, dx) * 180 / Math.PI
    var start = alpha + phi / 2
    var degrees = 360 - phi
    shapeRenderer.arc(500, 300, 150, start.toFloat, degrees)
    shapeRenderer.line(x, y, x1, y1)
    shapeRenderer.end()
  }

  override def onExit() = {
  }
}
