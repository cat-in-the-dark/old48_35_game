package com.catinthedark.shapeshift

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.{Gdx, Input, InputAdapter}
import com.catinthedark.lib.{Stub, TextureState}

class PairingState(shared0: Shared0, name: String) extends Stub(name) with TextureState {
  var hardSkip: Boolean = false
  
  override def onActivate(): Unit = {
    super.onActivate()
    
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

  override def run(delta: Float): Option[Unit] = {
    super.run(delta)
    if (hardSkip) {
      hardSkip = false
      println("WARNING hard skip of network connection")
      return Some()
    }
    
    if (shared0.networkControl != null) {
      shared0.networkControl.isConnected
    } else {
      None
    }
  }

  override val texture: Texture = if (shared0.networkControl.isServer) {
    Assets.Textures.HunterThemePack.pairing
  } else {
    Assets.Textures.WolfThemePack.pairing
  }
}
