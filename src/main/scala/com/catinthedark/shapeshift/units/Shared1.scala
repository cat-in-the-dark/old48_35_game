package com.catinthedark.shapeshift.units

import com.catinthedark.shapeshift.common.Const
import com.catinthedark.shapeshift.entity.{Enemy, Player}
import com.catinthedark.shapeshift.view.UP
import com.catinthedark.shapeshift.{Assets, Shared0}


class Shared1(val shared0: Shared0) {
  def init() = {
    if (shared0.networkControl.isServer)
      (Player(Const.UI.playerMinX(), UP, 0, Assets.Animations.goodAnimations), Enemy(Const.UI.playerMinX(), UP, 0, Assets.Animations.uglyEnemyAnimations))
    else
      (Player(Const.UI.playerMinX(), UP, 0, Assets.Animations.uglyAnimations), Enemy(Const.UI.playerMinX(), UP, 0, Assets.Animations.goodEnemyAnimations))
  }

  var (player, enemy) = init()

  def reset() = {
    val d = init()
    player = d._1
    enemy = d._2
  }

}
