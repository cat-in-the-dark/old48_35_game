package com.catinthedark.shapeshift

import com.catinthedark.lib.{Deferred, LocalDeferred, SimpleUnit, YieldUnit}
import com.catinthedark.shapeshift.units._
import com.catinthedark.shapeshift.view.KILLED

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

    shared0.networkControl.onMovePipe.ports += view.enemyView.onMove
    shared0.networkControl.onShootPipe.ports += view.enemyView.onShoot
    shared0.networkControl.onJumpPipe.ports += view.enemyView.onJump
    shared0.networkControl.onEnemyDisconnected.ports += view.enemyView.onDisconnect

    control.onGameReload + (_ => {
      forceReload = true
      stopNetworkThread()
    })
  }

  def deactivateControl(): Unit = {
    control.onShoot.ports.clear()
    control.onJump.ports.clear()

    shared0.networkControl.onMovePipe.ports.clear()
    shared0.networkControl.onShootPipe.ports.clear()
    shared0.networkControl.onJumpPipe.ports.clear()
    shared0.networkControl.onEnemyDisconnected.ports.clear()
  }

  def stopNetworkThread(): Unit = {
    println("Trying to stop network thread")
    shared0.stopNetwork()
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
    println("onExit GameState")
    Assets.Audios.bgm.pause()
    shared1.player.audio.steps.pause()
    shared1.enemy.audio.steps.pause()
    children.foreach(_.onExit())
    deactivateControl()
    shared1.reset()
    //shared0.stopNetwork()
    
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
