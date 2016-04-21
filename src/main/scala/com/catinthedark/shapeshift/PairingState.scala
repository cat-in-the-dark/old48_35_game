package com.catinthedark.shapeshift

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.{Gdx, Input, InputAdapter}
import com.catinthedark.lib.{Stub, TextureState}
import com.catinthedark.shapeshift.entity.Entity
import com.catinthedark.shapeshift.units.Shared1

import scala.collection.mutable

class PairingState(shared0: Shared0, name: String) extends Stub(name) with TextureState {
  var hardSkip: Boolean = false
  
  override def onActivate(data: Any): Unit = {
    super.onActivate(data)
    
    Gdx.input.setInputProcessor(new InputAdapter {
      override def keyDown(keyCode: Int): Boolean = {
        keyCode match {
          case Input.Keys.BACKSPACE => hardSkip = true
          case _ => 
        }
        true
      }
    })
    
    shared0.networkControlThread = new Thread(shared0.networkControl)
    shared0.networkControlThread.start()
    println("Network thread started")
  }

  override def onExit(): Unit = {
    super.onExit()
  }

  override def run(delta: Float): (Option[Unit], Any) = {
    super.run(delta)
    if (hardSkip) {
      hardSkip = false
      println("WARNING hard skip of network connection")
      val shared1 = new Shared1(shared0, new mutable.ListBuffer[Entity](), isMain = true)
      return (Some(), shared1)
    }
    
    if (shared0.networkControl != null) {
      if (shared0.networkControl.isConnected.isDefined) {
        val shared1 = new Shared1(shared0, new mutable.ListBuffer[Entity](), isMain = shared0.networkControl.isMain)
        (shared0.networkControl.isConnected, shared1)
      } else {
        (shared0.networkControl.isConnected, null)
      }
    } else {
      (None, null)
    }
  }

  override val texture: Texture = Assets.Textures.pairing
}
