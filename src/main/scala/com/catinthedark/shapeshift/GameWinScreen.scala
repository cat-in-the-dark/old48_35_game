package com.catinthedark.shapeshift

import com.badlogic.gdx.{Gdx, Input}
import com.badlogic.gdx.graphics.{GL20, Texture}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.catinthedark.lib.{KeyAwaitState, Stub, TextureState}
import com.catinthedark.lib.Magic._

class GameWinScreen(val shared: Shared0) extends Stub("GameWin")  with KeyAwaitState {
  override val keycode: Int = Input.Keys.ENTER

  val batch = new SpriteBatch

  override def onActivate(data: Any): Unit = {
    super.onActivate()
  }

  override def run(delta: Float): (Option[Unit], Any) = {
    batch.managed { self: SpriteBatch =>
      self.draw(Assets.Textures.won, 0, 0)
    }
    super.run(delta)
  }
}