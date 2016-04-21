package com.catinthedark.shapeshift

import java.net.URI

import com.catinthedark.shapeshift.network.{NetworkControl, NetworkWSControl}

class Shared0(
  val serverAddress: URI
) {
  val networkControl: NetworkControl = new NetworkWSControl(serverAddress)
  var networkControlThread: Thread = null
}
