package com.catinthedark.shapeshift

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.catinthedark.lib.{KeyAwaitState, Stub, TextureState}

class GameOverState(val shared: Shared0) extends Stub("GameOver") with TextureState with KeyAwaitState {
  override val keycode: Int = Input.Keys.ENTER
  override val texture: Texture = if (shared.networkControl.isServer) {
    Assets.Textures.GoodThemePack.loseScreen
  } else {
    Assets.Textures.WolfThemePack.loseScreen
  }

  override def onActivate(): Unit = {
    super.onActivate()
  }
}
