package com.catinthedark.shapeshift

import java.net.URI

import com.catinthedark.shapeshift.network.{NetworkControl, NetworkWSControl}

class Shared0(
  val serverAddress: URI
) {
  val networkControl: NetworkControl = new NetworkWSControl(serverAddress)
  private var networkControlThread: Thread = null
  
  def stopNetwork(): Unit = {
    networkControl.dispose()
    if (networkControlThread != null) {
      networkControlThread.interrupt()
      networkControlThread = null
    }
  }
  
  def start(): Unit = {
    networkControlThread = new Thread(networkControl)
    networkControlThread.start()
    println("Network thread started")
  }
}
