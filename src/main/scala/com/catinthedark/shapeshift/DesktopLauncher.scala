package com.catinthedark.shapeshift

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage

import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import com.catinthedark.lib.constants._
import com.catinthedark.shapeshift.common.Const

class MyApplication extends Application {

  override def start(stage: Stage): Unit = {
    val root: VBox = FXMLLoader.load(getClass().getClassLoader.getResource("layout.fxml"));
    Const.delegate.foreach { d =>
      val el = d match {
        case onOff: OnOff =>
          new OnOffControl(onOff, FXMLLoader.load(getClass().getClassLoader.getResource("onOff.fxml"))).el
        case range: IRange =>
          new IRangeControl(range, FXMLLoader.load(getClass().getClassLoader.getResource("range.fxml"))).el
        case range: FRange =>
          new FRangeControl(range, FXMLLoader.load(getClass().getClassLoader.getResource("range.fxml"))).el
        case range: Vec2Range =>
          new Vec2RangeControl(range, FXMLLoader.load(getClass().getClassLoader.getResource("vec2Range.fxml"))).el
      }
      root.getChildren.add(el)
    }
    val sc = new Scene(root, 275, 700)

    stage.setTitle("Property Tuner v0.0.0")
    stage.setX(0)
    stage.setY(0)
    stage.setScene(sc)
    stage.show()
    stage.getScene().getWindow().setX(0)
  }
}

object DesktopLauncher {
  def main(args: Array[String]) {
    val conf = new LwjglApplicationConfiguration
    conf.title = "Wild West Farm Shooting"
    conf.height = Const.Projection.height.toInt
    conf.width = Const.Projection.width.toInt
    conf.x = 300
    conf.y = 0

    val address = if (args.length > 0) {
      println(s"Would be connected to ${args(0)} if can")
      args(0)
    } else {
      "https://catinthedark-game-server.herokuapp.com/"
    }
    

//    new Thread(new Runnable {
//      override def run(): Unit = {
//        Application.launch(classOf[MyApplication])
//      }
//    }).start()

    val game = new LwjglApplication(new Main(address), conf)
  }
}
