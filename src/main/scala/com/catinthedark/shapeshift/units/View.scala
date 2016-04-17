package com.catinthedark.shapeshift.units

import java.util.concurrent.ConcurrentLinkedQueue

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.graphics.glutils.{FrameBuffer, ShaderProgram, ShapeRenderer}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.Vector2
import com.catinthedark.shapeshift.Assets
import com.catinthedark.shapeshift.common.Const
import com.catinthedark.shapeshift.common.Const.{Balance, UI}
import com.catinthedark.shapeshift.entity.Tree
import com.catinthedark.shapeshift.view._
import com.catinthedark.lib._
import com.catinthedark.shapeshift.common.Const
import Magic.richifySpriteBatch
import org.lwjgl.util.Point

import scala.collection.mutable

/**
  * Created by over on 02.01.15.
  */
abstract class View(val shared: Shared1) extends SimpleUnit with Deferred {
  val batch = new SpriteBatch()
  val magicBatch = new MagicSpriteBatch(Const.debugEnabled())
  val shapeRenderer = new ShapeRenderer()

  val enemyView = new EnemyView(shared) with LocalDeferred
  val camera = new OrthographicCamera(Const.Projection.width, Const.Projection.height)
  camera.position.x = Const.Projection.width / 2
  camera.position.y = Const.Projection.height / 2
  camera.update()

  shared.shared0.networkControl.onMovePipe.ports += enemyView.onMove
  //  shared.shared0.networkControl.onShootPipe.ports += enemyView.onShoot
  //  shared.shared0.networkControl.onAlivePipe.ports += enemyView.onAlive

  val renderList = new mutable.ArrayBuffer[() => Unit]()

  override def onActivate() = {
    val layers = Assets.Maps.map1.getLayers
    plantTrees(layers.get("tree1"))
    plantTrees(layers.get("tree2"))
    plantTrees(layers.get("tree3"))
  }

  def plantTrees(layer: MapLayer) = {
    val mapTrees = layer.getObjects.iterator()
    while (mapTrees.hasNext) {
      val tree = mapTrees.next()
      val x = tree.getProperties.get("x", classOf[Float])
      val y = tree.getProperties.get("y", classOf[Float])
      shared.trees += new Tree(x, y, UI.treePhysRadius, layerToTexture(layer.getName))
    }
  }

  def onShot(data: (Vector2, Vector2, Vector2)): Unit = {
    println(s"View onShot: $data")
    
    val playerPos = data._1
    val shotPoint1 = data._2
    val shotPoint2 = data._3
    
    val task = () => {
      shapeRenderer.begin(ShapeType.Filled)

      shapeRenderer.setColor(1f, 0f, 0f, 0.5f)
      shapeRenderer.triangle(
        playerPos.x, playerPos.y, 
        shotPoint1.x, shotPoint1.y, 
        shotPoint2.x, shotPoint2.y)

      shapeRenderer.end()
    }
    renderList += task
    defer(2f, () => {
      renderList -= task
    })
  }

  def onIdle(u: Unit): Unit = {
    shared.shared0.networkControl.move(shared.player.pos, shared.player.angle, idle = true)
  }

  def layerToTexture(layerName: String) = layerName match {
    case "tree1" => Assets.Textures.tree1
    case "tree2" => Assets.Textures.tree2
    case "tree3" => Assets.Textures.tree3
    case _ => throw new RuntimeException(s"ooops. texture for layer $layerName not found")
  }

  def drawFloor() =
    batch.managed { self =>
      self.draw(Assets.Textures.floor, 0,0,0,0, 3200, 3200)
    }

  def drawShadow(lightPos: Vector2, targetPos: Vector2, radius: Float): Unit = {
    Gdx.gl.glEnable(GL20.GL_BLEND)
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

    shapeRenderer.begin(ShapeType.Filled)
    shapeRenderer.setColor(UI.darknessColor)

    val distance = lightPos.dst(targetPos)

    val alpha = Math.atan2(targetPos.y - lightPos.y, targetPos.x - lightPos.x)
    val beta = Math.asin(UI.treePhysRadius / distance)
    val x1 = (lightPos.x + distance * Math.cos(alpha - beta)).toFloat
    val y1 = (lightPos.y + distance * Math.sin(alpha - beta)).toFloat
    val x2 = (lightPos.x + distance * Math.cos(alpha + beta)).toFloat
    val y2 = (lightPos.y + distance * Math.sin(alpha + beta)).toFloat
    val x3 = (x2 + UI.rayLength * Math.cos(alpha + beta)).toFloat
    val y3 = (y2 + UI.rayLength * Math.sin(alpha + beta)).toFloat
    val x4 = (x1 + UI.rayLength * Math.cos(alpha - beta)).toFloat
    val y4 = (y1 + UI.rayLength * Math.sin(alpha - beta)).toFloat
    val x5 = (x1 + UI.rayLength * Math.cos(alpha - beta - UI.halfShadowAngle)).toFloat
    val y5 = (y1 + UI.rayLength * Math.sin(alpha - beta - UI.halfShadowAngle)).toFloat
    val x6 = (x2 + UI.rayLength * Math.cos(alpha + beta + UI.halfShadowAngle)).toFloat
    val y6 = (y2 + UI.rayLength * Math.sin(alpha + beta + UI.halfShadowAngle)).toFloat

    shapeRenderer.triangle(x1, y1, x2, y2, x3, y3)
    shapeRenderer.triangle(x1, y1, x3, y3, x4, y4)
    shapeRenderer.setColor(UI.semiDarknessColor)
    shapeRenderer.triangle(x1, y1, x4, y4, x5, y5)
    shapeRenderer.triangle(x2, y2, x3, y3, x6, y6)
    shapeRenderer.end()
    Gdx.gl.glDisable(GL20.GL_BLEND)
  }

  override def run(delta: Float) = {
    //1. clear screen
    Gdx.gl.glClearColor(UI.darknessRed, UI.darknessGreen, UI.darknessBlue, 0)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    //2. clear our depth buffer with 1.0
    Gdx.gl.glClearDepthf(1f)
    Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT)

    //3. set the function to LESS
    Gdx.gl.glDepthFunc(GL20.GL_LESS)

    //4. enable depth writing
    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)

    //5. Enable depth writing, disable RGBA color writing
    Gdx.gl.glDepthMask(true)
    Gdx.gl.glColorMask(false, false, false, false)

    if (shared.player.pos.x > Const.Projection.width / 2
      && shared.player.pos.x < Const.Projection.mapWidth - Const.Projection.width / 2)
      camera.position.x = shared.player.pos.x
    if (shared.player.pos.y > Const.Projection.height / 2 &&
      shared.player.pos.y < Const.Projection.mapHeight - Const.Projection.height / 2)
      camera.position.y = shared.player.pos.y

    val playerScreenX = shared.player.pos.x - camera.position.x + Const.Projection.width / 2f
    val playerScreenY = shared.player.pos.y - camera.position.y + Const.Projection.height / 2f

    camera.update()
    batch.setProjectionMatrix(camera.combined)
    magicBatch.setProjectionMatrix(camera.combined)
    shapeRenderer.setProjectionMatrix(camera.combined)

    shapeRenderer.begin(ShapeType.Filled)

    shapeRenderer.setColor(1f, 0f, 0f, 0.5f)
    shapeRenderer.circle(shared.player.pos.x, shared.player.pos.y, shared.player.balance.maxRadius)

    shapeRenderer.end()

    //8. Enable RGBA color writing
    //   (SpriteBatch.begin() will disable depth mask)
    Gdx.gl.glColorMask(true, true, true, true)

    //9. Make sure testing is enabled.
    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)

    //10. Now depth discards pixels outside our masked shapes
    Gdx.gl.glDepthFunc(GL20.GL_EQUAL)

    drawFloor()
    enemyView.run(delta)

    shared.trees = shared.trees.sortWith((a, b) => {
      shared.player.pos.dst(a.x, a.y) > shared.player.pos.dst(b.x, b.y)
    })

    shared.trees.foreach(tree => {
      val pos = shared.player.pos
      val maxRadius = shared.player.balance.maxRadius
      val distance = pos.dst(tree.x, tree.y)
      if (distance < maxRadius) {
        drawShadow(pos, new Vector2(tree.x, tree.y), UI.treePhysRadius)
      }

      magicBatch managed { batch =>
        magicBatch.drawCircleCentered(tree.texture, tree.x, tree.y, Const.UI.treePhysRadius)
      }
    })

    shapeRenderer.begin(ShapeType.Filled)
    shapeRenderer.setColor(UI.darknessColor)

    val x1 = Const.Projection.calcX(Gdx.input.getX())
    val y1 = Const.Projection.height - Const.Projection.calcY(Gdx.input.getY())
    var phi = shared.player.balance.viewAngle
    var dx = x1 - playerScreenX
    var dy = y1 - playerScreenY
    var alpha = Math.atan2(dy, dx) * 180 / Math.PI
    shared.player.angle = alpha.toFloat
    var start = alpha + phi / 2
    var degrees = 360 - phi
    shapeRenderer.arc(shared.player.pos.x, shared.player.pos.y, shared.player.balance.maxRadius + 2, start.toFloat, degrees)
    shapeRenderer.end()

    magicBatch managed { batch =>
      magicBatch.drawWithDebug(shared.player.texture(delta), shared.player.rect, shared.player.physRect, angle = shared.player.angle)
      enemyView.render(delta, batch)
    }
    
    renderList.foreach(_())
  }

  override def onExit() = {
  }
}
