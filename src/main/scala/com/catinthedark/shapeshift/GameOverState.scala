package com.catinthedark.shapeshift

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.catinthedark.lib.{KeyAwaitState, Stub, TextureState}
import com.catinthedark.lib.Magic._

class GameOverState(val shared: Shared0)  extends Stub("GameOver")  with KeyAwaitState {
  override val keycode: Int = Input.Keys.ENTER

  val batch = new SpriteBatch

  override def onActivate(): Unit = {
    super.onActivate()
  }

  override def run(delta: Float): Option[Unit] = {
    batch.managed { self: SpriteBatch =>
      self.draw(Assets.Textures.loose, 0, 0)
    }
    super.run(delta)
  }
}
