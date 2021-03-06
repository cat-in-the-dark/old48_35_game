package com.catinthedark.shapeshift

import java.net.URI

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.{Game, Gdx, Input}
import com.catinthedark.lib._

import scala.util.Random

class Main(address: String) extends Game {
  val rm = new RouteMachine()

  def keyAwait(name: String, tex: Texture, key: Int = Input.Keys.ENTER) =
    new Stub(name) with TextureState with KeyAwaitState {
      val texture: Texture = tex
      val keycode: Int = Input.Keys.ENTER
    }

  def delayed(name: String, tex: Texture, _delay: Float) =
    new Stub(name) with TextureState with DelayState {
      val texture: Texture = tex
      val delay: Float = _delay
    }

  val rand = new Random()
  var shared: Shared0 = _

  override def create() = {

    val logo = delayed("Logo", Assets.Textures.logo, 1.0f)
    val t0 = keyAwait("start", Assets.Textures.t0)
//    val t1 = keyAwait("Tutorial1", Assets.Textures.t1)
//    val t2 = keyAwait("Tutorial2", Assets.Textures.t2)
//    val t3 = keyAwait("Tutorial3", Assets.Textures.t3)
//    val t4 = keyAwait("Tutorial4", Assets.Textures.t4)
//    val t5 = keyAwait("Tutorial4", Assets.Textures.t5)
//    val t6 = keyAwait("Tutorial4", Assets.Textures.t6)

    shared = new Shared0(new URI(address))

    val pairing = new PairingState(shared, "Pairing")
    
    val game = new GameState(shared)
    val gameOver = new GameOverState(shared)
    val gameWin = new GameWinScreen(shared)

    rm.addRoute(logo, anyway => t0)
//    rm.addRoute(t0, anyway => t1)
//    rm.addRoute(t1, anyway => t2)
//    rm.addRoute(t2, anyway => t3)
//    rm.addRoute(t3, anyway => t4)
//    rm.addRoute(t4, anyway => t5)
//    rm.addRoute(t5, anyway => t6)
//    rm.addRoute(t6, anyway => pairing)
    rm.addRoute(t0, anyway => pairing)
    rm.addRoute(pairing, anyway => game)
    rm.addRoute(game, res => {
      res match {
        case true => gameWin
        case false => gameOver
      }
    })

    rm.addRoute(gameWin, anyway => {
      shared.stopNetwork()
      t0
    })
    rm.addRoute(gameOver, anyway => {
      shared.stopNetwork()
      t0
    })

    rm.start(logo)
  }

  override def render() = {
    rm.run(Gdx.graphics.getDeltaTime)
  }

  override def dispose(): Unit ={
    super.dispose()
    shared.stopNetwork()
  }
}
