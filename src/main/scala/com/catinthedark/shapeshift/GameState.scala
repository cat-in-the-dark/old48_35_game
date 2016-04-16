package com.catinthedark.shapeshift

import com.catinthedark.lib.{LocalDeferred, YieldUnit}
import com.catinthedark.shapeshift.common.Const
import com.catinthedark.shapeshift.units._

/**
  * Created by over on 18.04.15.
  */
class GameState(shared0: Shared0) extends YieldUnit[Boolean] {
  val shared1 = new Shared1(shared0)
  val view = new View(shared1) with LocalDeferred
  val control = new Control(shared1) with LocalDeferred

  var forceReload = false
  var iLoose = false
  var iWon = false

  control.onMoveLeft.ports += view.onMoveLeft
  control.onMoveRight.ports += view.onMoveRight
  control.onMoveForward.ports += view.onMoveForward
  control.onMoveBackward.ports += view.onMoveBackward
  control.onIdle.ports += view.onIdle
  
  control.onGameReload + (_ => {
    forceReload = true
    stopNetworkThread()
  })

  def onILoose(u: Unit) = {
    iLoose = true
    stopNetworkThread()
  }

  def onIWon(u: Unit) = {
    iWon = true
    stopNetworkThread()
  }

  def stopNetworkThread(): Unit = {
    println("Trying to stop network thread")
    if (shared0.networkControlThread != null) {
      shared0.networkControlThread.interrupt()
    }
  }

  shared0.networkControl.onILoosePipe.ports += onILoose
  shared0.networkControl.onIWonPipe.ports += onIWon
  
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
    } else if (iLoose) {
      iLoose = false
      Some(false)
    } else if (iWon) {
      iWon = false
      Some(true)
    } else {
      None
    }
  }
}
