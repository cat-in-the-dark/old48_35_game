package com.catinthedark.shapeshift

import com.catinthedark.lib.{LocalDeferred, SimpleUnit, YieldUnit}
import com.catinthedark.shapeshift.common.Const
import com.catinthedark.shapeshift.entity.{Entity, Tree}
import com.catinthedark.shapeshift.network.NetworkServerControl
import com.catinthedark.shapeshift.units._
import com.catinthedark.shapeshift.view.KILLED

import scala.collection.mutable

/**
  * Created by over on 18.04.15.
  */
class GameState(shared0: Shared0) extends YieldUnit[Boolean] {
  var shared1: Shared1 = null
  var view: View = null
  var control: Control = null
  var children: Seq[SimpleUnit] = Seq()

  var forceReload = false

  def activateControl() {
    control.onShoot.ports += view.onShot
    control.onJump.ports += view.onJump

    control.onGameReload + (_ => {
      forceReload = true
      stopNetworkThread()
    })
  }

  def onGameOver(u: Unit) = {
    stopNetworkThread()
  }

  def stopNetworkThread(): Unit = {
    println("Trying to stop network thread")
    if (shared0.networkControlThread != null) {
      shared0.networkControlThread.interrupt()
    }
  }

  override def onActivate(data: Any): Unit = {
    Assets.Audios.bgm.play()
    shared1 = data.asInstanceOf[Shared1]
    view = new View(shared1) with LocalDeferred
    control = new Control(shared1) with LocalDeferred
    children = Seq(view, control)
    activateControl()
    children.foreach(_.onActivate())
  }

  override def onExit(): Unit = {
    Assets.Audios.bgm.stop()
    shared1.player.audio.steps.stop()
    shared1.enemy.audio.steps.stop()
    children.foreach(_.onExit())
    shared1.reset()
    shared0.networkControlThread.interrupt()
    
    shared1 = null
    children = null
    view = null
    control = null
  }

  override def run(delta: Float): (Option[Boolean], Any) = {
    shared0.networkControl.processIn()
    children.foreach(_.run(delta))
    
    val res = if (forceReload) {
      forceReload = false
      Some(false)
    } else if (shared1.player.state == KILLED) {
      Some(false)
    } else if (shared1.enemy.state == KILLED) {
      Some(true)
    } else {
      None
    }
    
    (res, null)
  }
}
