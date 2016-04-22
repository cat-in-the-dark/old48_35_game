package com.catinthedark.shapeshift.units


import com.badlogic.gdx.math.{Intersector, MathUtils, Vector2}
import com.badlogic.gdx.{Gdx, Input, InputAdapter}
import com.catinthedark.lib._
import com.catinthedark.shapeshift.Assets
import com.catinthedark.shapeshift.common.Const
import com.catinthedark.shapeshift.common.Const.{Balance, Projection, UI}
import com.catinthedark.shapeshift.entity.{Enemy, Entity, Trace, Tree}
import com.catinthedark.shapeshift.view._

/**
  * Created by over on 22.01.15.
  */
abstract class Control(shared: Shared1) extends SimpleUnit with Deferred {
  val onPlayerStateChanged = new Pipe[State]()
  val onShoot = new Pipe[(Vector2, Vector2, Vector2, Option[Entity])]()
  val onGameReload = new Pipe[Unit]()
  val onJump = new Pipe[Unit]()

  val STAND_KEY = Input.Keys.CONTROL_LEFT

  override def onActivate() = {
    Gdx.input.setInputProcessor(new InputAdapter {
      override def keyDown(keycode: Int): Boolean = {
        keycode match {
          case Input.Keys.ESCAPE => onGameReload()
          case _ =>
        }
        true
      }

      override def keyUp(keycode: Int): Boolean = {
        true
      }

      override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
        if (pointer == Input.Buttons.LEFT) {
          if (shared.player.canShot) {
            shared.player.canShot = false
            defer(shared.player.balance.shotColdown, () => shared.player.canShot = true)
            defer(0.4f, () => {
              if (shared.player.state == SHOOTING) {
                shared.player.state = IDLE  
              }
            })
          
            val x = Const.Projection.calcX(screenX)
            val y = Const.Projection.calcY(screenY)
            val heroPoint = new Vector2(shared.player.pos)
            val point1 = new Vector2(
              MathUtils.cosDeg(shared.player.angle - shared.player.balance.shotDispersionAngle/2f) * shared.player.balance.shotRadius + shared.player.pos.x,
              MathUtils.sinDeg(shared.player.angle - shared.player.balance.shotDispersionAngle/2f) * shared.player.balance.shotRadius + shared.player.pos.y)
            val point2 = new Vector2(
              MathUtils.cosDeg(shared.player.angle + shared.player.balance.shotDispersionAngle/2f) * shared.player.balance.shotRadius + shared.player.pos.x,
              MathUtils.sinDeg(shared.player.angle + shared.player.balance.shotDispersionAngle/2f) * shared.player.balance.shotRadius + shared.player.pos.y)
  
            //println(s"screenX: $screenX screenY: $screenY x: $x y: $y angle: ${shared.player.angle}")
  
            val segments = Array((point1, point2), (point1, heroPoint), (point2, heroPoint))
  
            val entity = shared.entities.reverse.find(entity => {
              segments.exists(segment => {
                Intersector.intersectSegmentCircle(segment._1, segment._2, entity.pos, Math.pow(entity.radius, 2f).toFloat)
              })
            })

            shared.player.state = SHOOTING
            onShoot(heroPoint, point1, point2, entity)
            val entityName = if (entity.isDefined) {
              entity.get.name
            } else {
              "None"
            }
            shared.shared0.networkControl.shoot(heroPoint, entityName)
          } else {
            shared.player.audio.shootOut.play(1)
          }
          true
        } else {
          false
        }
      }
    })
  }
  
  def onJumping(): Unit = {
    if (shared.player.state == JUMPING) {
      shared.player.audio.steps.pause()
    }
    onJump()
  }

  def onMove(): Unit = {
    if (shared.player.state == RUNNING) {
      shared.player.audio.steps.setVolume(Assets.Audios.stepsVolume)
      shared.player.audio.steps.play()
    } else {
      shared.player.audio.steps.pause()
    }
    shared.shared0.networkControl.move(shared.player.pos, shared.player.angle, idle = false)

    if (shared.playerTraces.isEmpty || shared.playerTraces.last.pos.dst(shared.player.pos) > UI.traceDistance) {
      shared.playerTraces.enqueue(new Trace(new Vector2(shared.player.pos), shared.player.angle))
      defer(shared.player.balance.tracesLiveTime, () => {
        synchronized({
          if (shared.playerTraces.nonEmpty) {
            shared.playerTraces.dequeue()
          }
        })
      })
    }
  }

  def onIdle(u: Unit): Unit = {
    shared.player.audio.steps.pause()
    shared.shared0.networkControl.move(shared.player.pos, shared.player.angle, idle = true)
  }

  def movePlayer(speedX: Float, speedY: Float): Unit  = {
    var newSpeedX = speedX
    var newSpeedY = speedY
    val predictedPos = new Vector2(shared.player.pos.x + speedX, shared.player.pos.y + speedY)

    if (shared.player.state != JUMPING) {
      shared.entities.foreach(entity => {
        entity match {
          case tree: Tree => {
            val predictDistance = predictedPos.dst(entity.pos) - UI.playerPhysRadius - entity.radius
            if (predictDistance <= 0) {
              val angle = Math.atan2(entity.pos.y - predictedPos.y, entity.pos.x - predictedPos.x).toFloat
              val newPosX = predictedPos.x + predictDistance * Math.cos(angle).toFloat
              val newPosY = predictedPos.y + predictDistance * Math.sin(angle).toFloat
              newSpeedX = newPosX - shared.player.pos.x
              newSpeedY = newPosY - shared.player.pos.y
            }
          }
          case _ =>
        }
      })
    }

    if (predictedPos.x > Projection.mapWidth || predictedPos.x < 0) {
      newSpeedX = 0
    }

    if (predictedPos.y > Projection.mapHeight || predictedPos.y < 0) {
      newSpeedY = 0
    }

    shared.player.pos.x += newSpeedX
    shared.player.pos.y += newSpeedY
  }

  override def run(delta: Float): Unit = {
    if (shared.player.state == KILLED) return

    if (shared.player.state != JUMPING) {
      if (controlKeysPressed() || (Gdx.input.isKeyPressed(Input.Keys.SPACE) && shared.player.canJump)) {
        if (controlKeysPressed()) {
          shared.player.state = RUNNING

          var speedX = 0f
          var speedY = 0f
          val speed = Const.gamerSpeed()

          if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            speedX -= speed
          }

          if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            speedX += speed
          }

          if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            speedY += speed
          }

          if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            speedY -= speed
          }

          movePlayer(speedX, speedY)
          onMove()
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && shared.player.canJump) {
          shared.player.state = JUMPING
          shared.player.canJump = false
          defer(Balance.jumpTime, () => {
            shared.player.state = IDLE
            shared.player.scale = 1.0f
            shared.jumpTime = 0
          })

          defer(Balance.jumpCoolDown, () => {
            shared.player.canJump = true
          })

          shared.jumpingAngle = shared.player.angle
          onJumping()
        }
      } else {
        if (shared.player.state != SHOOTING) {
          shared.player.state = IDLE
        }
        onIdle()
      }
    } else {
      val speedX = Const.gamerJumpSpeed() * Math.cos(Math.toRadians(shared.jumpingAngle)).toFloat
      val speedY = Const.gamerJumpSpeed() * Math.sin(Math.toRadians(shared.jumpingAngle)).toFloat
      movePlayer(speedX, speedY)
      shared.jumpTime += delta
      if (shared.jumpTime <= Balance.jumpTime / 2) {
        shared.player.scale += delta
      } else {
        shared.player.scale -= delta
      }
      onJumping()
    }
  }
  
  private def controlKeysPressed(): Boolean = {
    Gdx.input.isKeyPressed(Input.Keys.A) || 
      Gdx.input.isKeyPressed(Input.Keys.D) || 
      Gdx.input.isKeyPressed(Input.Keys.W) || 
      Gdx.input.isKeyPressed(Input.Keys.S)
  }

  override def onExit(): Unit = Gdx.input.setInputProcessor(null)
}
