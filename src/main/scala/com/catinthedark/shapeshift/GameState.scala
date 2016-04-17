package com.catinthedark.shapeshift

import com.catinthedark.lib.{LocalDeferred, YieldUnit}
import com.catinthedark.shapeshift.common.Const
import com.catinthedark.shapeshift.entity.{Entity, Tree}
import com.catinthedark.shapeshift.units._
import com.catinthedark.shapeshift.view.KILLED

import scala.collection.mutable

/**
  * Created by over on 18.04.15.
  */
class GameState(shared0: Shared0) extends YieldUnit[Boolean] {
  val shared1 = new Shared1(shared0, new mutable.ListBuffer[Entity]())
  val view = new View(shared1) with LocalDeferred
  val control = new Control(shared1) with LocalDeferred

  var forceReload = false

  control.onShoot.ports += view.onShot
  control.onIdle.ports += view.onIdle
  control.onJump.ports += view.onJump
  
  control.onGameReload + (_ => {
    forceReload = true
    stopNetworkThread()
  })

  def onGameOver(u: Unit) = {
    stopNetworkThread()
  }

  def stopNetworkThread(): Unit = {
    println("Trying to stop network thread")
    if (shared0.networkControlThread != null) {
      shared0.networkControlThread.interrupt()
    }
  }
  
  val children = Seq(view, control)


  override def onActivate(): Unit = {
    //Assets.Audios.bgm.play()
    children.foreach(_.onActivate())
  }

  override def onExit(): Unit = {
    //Assets.Audios.bgm.stop()
    children.foreach(_.onExit())
    shared1.reset()
  }

  override def run(delta: Float): Option[Boolean] = {
    shared0.networkControl.processIn()
    children.foreach(_.run(delta))
    
    if (forceReload) {
      forceReload = false
      Some(false)
    } else if (shared1.player.state == KILLED) {
      Some(false)
    } else if (shared1.enemy.state == KILLED) {
      Some(true)
    } else {
      None
    }
  }
}
