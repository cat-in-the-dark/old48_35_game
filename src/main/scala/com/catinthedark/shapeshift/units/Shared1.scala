package com.catinthedark.shapeshift.units

import com.catinthedark.shapeshift.{Assets, Shared0}
import com.catinthedark.shapeshift.common.Const
import com.catinthedark.shapeshift.entity.{Enemy, Player}
import com.catinthedark.shapeshift.view.IDLE


class Shared1(val shared0: Shared0) {
  def reset() = {
    
  }
  
  var (player, enemy) = init()
  
  def init() = {
    if (shared0.networkControl.isServer)
      (Player(Const.Balance.playerStartPos, IDLE, 0, Assets.Animations.HunterAnimationPack, 0), Enemy(Const.Balance.playerStartPos, IDLE, 0, Assets.Animations.WolfAnimationPack, 0))
    else
      (Player(Const.Balance.playerStartPos, IDLE, 0, Assets.Animations.WolfAnimationPack, 0), Enemy(Const.Balance.playerStartPos, IDLE, 0, Assets.Animations.HunterAnimationPack, 0))
  }
}
