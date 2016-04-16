package com.catinthedark.shapeshift.units

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.{MapLayer, MapObject}
import com.catinthedark.shapeshift.Assets
import com.catinthedark.shapeshift.common.Const
import com.catinthedark.shapeshift.view._
import com.catinthedark.lib._
import Magic.richifySpriteBatch
import org.lwjgl.util.Point

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

  def drawTreeLayer(layer: MapLayer) = {
    val trees = layer.getObjects.iterator()
    while (trees.hasNext) {
      val tree = trees.next()
      val x = tree.getProperties.get("x", classOf[Float])
      val y = tree.getProperties.get("y", classOf[Float])



      println(s"X=$x, Y=$y")
    }
  }

  override def run(delta: Float) = {
    val layers = Assets.Maps.map1.getLayers
    drawTreeLayer(layers.get("tree1"))
    drawTreeLayer(layers.get("tree2"))
    drawTreeLayer(layers.get("tree3"))
    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
  }

  override def onExit() = {
  }
}
