package com.catinthedark.shapeshift

import com.catinthedark.shapeshift.network.{NetworkClientControl, NetworkServerControl}

class Shared0(
  val serverAddress: String
) {
  val networkControl = if (serverAddress != null) {
    new NetworkClientControl(serverAddress)
  } else {
    new NetworkServerControl()
  }
  var networkControlThread: Thread = null
}
