package com.catinthedark.shapeshift

import com.catinthedark.shapeshift.network.{NetworkServerControl, NetworkClientControl, NetworkControl}

/**
 * Created by over on 18.04.15.
 */
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
