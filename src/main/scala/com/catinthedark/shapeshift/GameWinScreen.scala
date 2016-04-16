package com.catinthedark.shapeshift

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.catinthedark.lib.{KeyAwaitState, Stub, TextureState}

class GameWinScreen(val shared: Shared0) extends Stub("GameWin") with TextureState with KeyAwaitState {
  override val keycode: Int = Input.Keys.ENTER
  override val texture: Texture = if (shared.networkControl.isServer) {
    Assets.Textures.GoodThemePack.winScreen
  } else {
    Assets.Textures.WolfThemePack.winScreen
  }

  override def onActivate(): Unit = {
    super.onActivate()
  }
}