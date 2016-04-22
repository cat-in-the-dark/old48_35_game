package com.catinthedark.shapeshift

import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import com.catinthedark.shapeshift.common.Const

object DesktopLauncher {
  def main(args: Array[String]) {
    val conf = new LwjglApplicationConfiguration
    conf.title = "Za bochok"
    conf.height = Const.Projection.height.toInt
    conf.width = Const.Projection.width.toInt
    conf.x = 300
    conf.y = 0

    val address = if (args.length > 0) {
      args(0)
    } else {
      "https://catinthedark-game-server.herokuapp.com/"
    }

    println(s"Would be connected to $address if can")

    val game = new LwjglApplication(new Main(address), conf)
  }
}
