package com.catinthedark.shapeshift.units

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.graphics.glutils.{FrameBuffer, ShaderProgram, ShapeRenderer}
import com.badlogic.gdx.graphics.glutils.{ShaderProgram, ShapeRenderer}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.Vector2
import com.catinthedark.shapeshift.Assets
import com.catinthedark.shapeshift.common.Const
import com.catinthedark.shapeshift.common.Const.{Balance, UI}
import com.catinthedark.shapeshift.entity.{Enemy, Entity, Tree}
import com.catinthedark.shapeshift.view._
import com.catinthedark.lib._
import com.catinthedark.shapeshift.common.Const

import scala.collection.mutable

/**
  * Created by over on 02.01.15.
  */
abstract class View(val shared: Shared1) extends SimpleUnit with Deferred {
  val clipShader = new ShaderProgram(Assets.Shaders.clipVert, Assets.Shaders.clipFrag)
  if(!clipShader.isCompiled) {
    println(clipShader.getLog)
    System.exit(1)
  }
  val clipShaderSame = new ShaderProgram(Assets.Shaders.clipVert1, Assets.Shaders.clipFrag1)
  if(!clipShaderSame.isCompiled) {
    println(clipShaderSame.getLog)
    System.exit(1)
  }
  val batch = new SpriteBatch()
  batch.setShader(clipShader)
  val magicBatch = new MagicSpriteBatch(Const.debugEnabled())
  val playerBatch = new MagicSpriteBatch(Const.debugEnabled())

  val hudBatch = new ShapeRenderer()

  magicBatch.setShader(clipShader)
  val shapeRenderer = new ShapeRenderer(5000)

  val enemyView = new EnemyView(shared) with LocalDeferred
  val camera = new OrthographicCamera(Const.Projection.width, Const.Projection.height)

  shared.shared0.networkControl.onMovePipe.ports += enemyView.onMove
  shared.shared0.networkControl.onShootPipe.ports += enemyView.onShoot
  shared.shared0.networkControl.onJumpPipe.ports += enemyView.onJump
  //  shared.shared0.networkControl.onShootPipe.ports += enemyView.onShoot
  //  shared.shared0.networkControl.onAlivePipe.ports += enemyView.onAlive

  val renderList = new mutable.ArrayBuffer[() => Unit]()

  override def onActivate() = {
    camera.position.x = Const.Projection.width / 2
    camera.position.y = Const.Projection.height / 2
    camera.update()
    renderList.clear()
    
    val layers = Assets.Maps.map1.getLayers
    plantTrees(layers.get("tree1"))
    plantTrees(layers.get("tree2"))
    plantTrees(layers.get("tree3"))
    shared.entities += shared.enemy
  }

  def plantTrees(layer: MapLayer) = {
    val mapTrees = layer.getObjects.iterator()
    while (mapTrees.hasNext) {
      val tree = mapTrees.next()
      val x = tree.getProperties.get("x", classOf[Float])
      val y = tree.getProperties.get("y", classOf[Float])
      shared.entities += new Tree(new Vector2(x, y), layerToTexture(layer.getName))
    }
  }

  def onShot(data: (Vector2, Vector2, Vector2, Option[Entity])): Unit = {
    println(s"View onShot: $data")
    
    val playerPos = data._1
    val shotPoint1 = data._2
    val shotPoint2 = data._3
    val entity = data._4

    if (entity.isDefined) {
      entity.get match {
        case _: Tree =>
          println("Tree collide")
          shared.player.audio.ricochetWood.play()
        case _: Enemy =>
          println("Enemy collide")
          shared.enemy.state = KILLED
          shared.player.audio.shoot.play()
        case _ =>
          println("Here")
          shared.player.audio.ricochet.play()
      }
    } else {
      println("Ricoshet")
      shared.player.audio.ricochet.play()
    }
    
    if (Const.debugEnabled()) {
      val task = () => {
        //      clipShaderSame.begin()
        //      clipShaderSame.setUniformf("pos", shared.player.pos.x, shared.player.pos.y)
        //      clipShader.setUniformf("resolution", Gdx.graphics.getWidth, Gdx.graphics.getHeight)
        //      clipShaderSame.setUniformf("cam_pos", camera.position.x, camera.position.y)
        //      clipShaderSame.setUniformf("player_rot", shared.player.angle)
        //      clipShaderSame.setUniformf("max_dist", shared.player.balance.maxRadius)
        //      clipShaderSame.setUniformf("phi", shared.player.balance.viewAngle)
        shapeRenderer.begin(ShapeType.Filled)

        shapeRenderer.setColor(1f, 0f, 0f, 0.5f)
        shapeRenderer.triangle(
          playerPos.x, playerPos.y,
          shotPoint1.x, shotPoint1.y,
          shotPoint2.x, shotPoint2.y)

        shapeRenderer.end()
        //clipShaderSame.end()
      }
      renderList += task
      defer(2f, () => {
        renderList -= task
      })
    }
  }

  var lastJump = Const.Balance.jumpCoolDown

  def onJump(u: Unit): Unit = {
    shared.shared0.networkControl.jump(shared.player.pos, shared.player.angle, shared.player.scale)
    lastJump = 0
  }

  def layerToTexture(layerName: String) = layerName match {
    case "tree1" => Assets.Textures.tree1
    case "tree2" => Assets.Textures.tree2
    case "tree3" => Assets.Textures.tree3
    case _ => throw new RuntimeException(s"ooops. texture for layer $layerName not found")
  }

  def beginClipShader(): Unit = {
    clipShader.begin()
    clipShader.setUniformf("pos", shared.player.pos.x, shared.player.pos.y)
    clipShader.setUniformf("cam_pos", camera.position.x, camera.position.y)
    clipShader.setUniformf("player_rot", shared.player.angle)
    clipShader.setUniformf("max_dist", shared.player.balance.maxRadius)
    clipShader.setUniformf("phi", shared.player.balance.viewAngle)
    clipShader.setUniformf("resolution", Gdx.graphics.getWidth, Gdx.graphics.getHeight)
  }

  def drawFloor() = {
    beginClipShader()

    magicBatch.managed { self =>
      self.draw(Assets.Textures.floor, 0, 0, 0, 0, 3200, 3200)
    }

    clipShader.end()
  }

  def drawTraces() = {
    beginClipShader()

    magicBatch.managed { self =>
      shared.enemyTraces.foreach(trace => {
        self.drawWithDebug(shared.enemy.pack.trace, trace.rect, trace.rect, angle = trace.angle)
      })

      shared.playerTraces.foreach(trace => {
        self.drawWithDebug(shared.player.pack.trace, trace.rect, trace.rect, angle = trace.angle)
      })
    }

    clipShader.end()
  }

  def drawShadow(lightPos: Vector2, targetPos: Vector2, radius: Float): Unit = {
    Gdx.gl.glEnable(GL20.GL_BLEND)
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
//    clipShaderSame.begin()
//    clipShaderSame.setUniformf("pos", shared.player.pos.x, shared.player.pos.y)
//    clipShader.setUniformf("resolution", Gdx.graphics.getWidth, Gdx.graphics.getHeight)
//    clipShaderSame.setUniformf("cam_pos", camera.position.x, camera.position.y)
//    clipShaderSame.setUniformf("player_rot", shared.player.angle)
//    clipShaderSame.setUniformf("max_dist", shared.player.balance.maxRadius)
//    clipShaderSame.setUniformf("phi", shared.player.balance.viewAngle)

    shapeRenderer.begin(ShapeType.Filled)
    shapeRenderer.setColor(0,0,0,1)

    val distance = lightPos.dst(targetPos)

    val alpha = Math.atan2(targetPos.y - lightPos.y, targetPos.x - lightPos.x)
    val beta = Math.asin(radius / distance)
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
    shapeRenderer.setColor(0,0,0, 0.5f)
    shapeRenderer.triangle(x1, y1, x4, y4, x5, y5)
    shapeRenderer.triangle(x2, y2, x3, y3, x6, y6)
    shapeRenderer.end()

    Gdx.gl.glDisable(GL20.GL_BLEND)

//    clipShaderSame.end()
  }

  override def run(delta: Float) = {
    //1. clear screen
    Gdx.gl.glClearColor(UI.darknessRed, UI.darknessGreen, UI.darknessBlue, 0)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

//    //2. clear our depth buffer with 1.0
//    Gdx.gl.glClearDepthf(1f)
//    Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT)
//
//    //3. set the function to LESS
//    Gdx.gl.glDepthFunc(GL20.GL_LESS)
//
//    //4. enable depth writing
//    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
//
//    //5. Enable depth writing, disable RGBA color writing
//    Gdx.gl.glDepthMask(true)
//    Gdx.gl.glColorMask(false, false, false, false)

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
    playerBatch.setProjectionMatrix(camera.combined)
    shapeRenderer.setProjectionMatrix(camera.combined)

    drawFloor()
    drawTraces()

    enemyView.run(delta)

    shared.entities = shared.entities.sortWith((a, b) => {
      shared.player.pos.dst(a.pos) > shared.player.pos.dst(b.pos)
    })

    shared.entities.foreach(entity => {
      val pos = shared.player.pos
      val maxRadius = shared.player.balance.maxRadius
      val distance = pos.dst(entity.pos)
      if (distance < maxRadius) {
        drawShadow(pos, entity.pos, entity.radius)
      }

      beginClipShader()

      magicBatch managed { batch =>
        entity match {
          case enemy: Enemy =>
            enemyView.render(delta, batch)
          case tree: Tree =>
            magicBatch.drawCircleCentered(entity.texture().getTexture, entity.pos.x, entity.pos.y, entity.radius)
          case _ =>
        }
      }

      clipShader.end()
    })

//    shapeRenderer.begin(ShapeType.Filled)
//    shapeRenderer.setColor(UI.darknessColor)
//
    val x1 = Const.Projection.calcX(Gdx.input.getX())
    val y1 = Const.Projection.height - Const.Projection.calcY(Gdx.input.getY())
    var phi = shared.player.balance.viewAngle
    var dx = x1 - playerScreenX
    var dy = y1 - playerScreenY
    var alpha = Math.atan2(dy, dx) * 180 / Math.PI
    shared.player.angle = alpha.toFloat
//    var start = alpha + phi / 2
//    var degrees = 360 - phi
//    shapeRenderer.arc(shared.player.pos.x, shared.player.pos.y, shared.player.balance.maxRadius + 2, start.toFloat, degrees)
//    shapeRenderer.end()

    playerBatch managed { batch =>
      playerBatch.drawWithDebug(shared.player.texture(delta), shared.player.rect, shared.player.physRect, angle = shared.player.angle, scaleX = shared.player.scale, scaleY = shared.player.scale)
    }
    
    renderList.foreach(_())

    lastJump += delta * 7

    if(shared.player.canJump) {
      hudBatch.begin(ShapeType.Filled)
      new HudBar((Const.Balance.jumpCoolDown * 100).toInt).render(hudBatch, (100 / Const.Balance.jumpCoolDown * lastJump).toInt,
        new Vector2(0, 0), new Vector2(Const.Projection.width, 20))
      hudBatch.end()
    }
  }

  override def onExit() = {
  }
}
