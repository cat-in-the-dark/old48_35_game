package com.catinthedark.shapeshift.units


import com.badlogic.gdx.{Gdx, Input, InputAdapter}
import com.catinthedark.lib._
import com.catinthedark.shapeshift.common.Const
import com.catinthedark.shapeshift.view._
import org.lwjgl.util.Point

/**
  * Created by over on 22.01.15.
  */
abstract class Control(shared: Shared1) extends SimpleUnit with Deferred {
  val onPlayerStateChanged = new Pipe[State]()
  val onShoot = new Pipe[Point]()
  val onGameReload = new Pipe[Unit]()
  val onMoveLeft = new Pipe[Unit]()
  val onMoveRight = new Pipe[Unit]()
  val onMoveForward = new Pipe[Unit]()
  val onMoveBackward = new Pipe[Unit]()
  val onIdle = new Pipe[Unit]()

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
          val x = Const.Projection.calcX(screenX)
          val y = Const.Projection.calcY(screenY)
          println(s"screenX: $screenX screenY: $screenY pointer: $pointer button: $button x: $x y: $y originWidth: ${Gdx.graphics.getWidth}")
          true
        } else {
          false
        }
      }
    })
  }

  override def run(delta: Float): Unit = {
    if (controlKeysPressed()) {
      shared.player.state = RUNNING
      
      if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.D)) {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
          onMoveLeft()
        } else {
          onMoveRight()
        }
      }

      if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.S)) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
          onMoveForward()
        } else {
          onMoveBackward()
        }
      }
    } else {
      shared.player.state = IDLE
      onIdle()
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
