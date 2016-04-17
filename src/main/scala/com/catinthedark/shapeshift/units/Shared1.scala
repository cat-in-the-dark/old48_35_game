package com.catinthedark.shapeshift.units

import com.catinthedark.shapeshift.{Assets, Shared0}
import com.catinthedark.shapeshift.common.Const
import com.catinthedark.shapeshift.entity.{Entity, Tree, Enemy, Player}
import com.catinthedark.shapeshift.view.IDLE

import scala.collection.mutable


class Shared1(val shared0: Shared0,
              var entities: mutable.ListBuffer[Entity],
              var jumpingAngle: Float = 0,
              var jumpTime: Float = 0,
              var jumpScale: Float = 0) {
  def reset() = {
    
  }
  
  var (player, enemy) = init()
  
  def init() = {
    val (spawnPlayer, spawnEnemy) = Const.Balance.randomSpawn
    if (shared0.networkControl.isServer)
      (Player(spawnPlayer, IDLE, Assets.Animations.HunterAnimationPack, Assets.Audios.HunterAudioPack, Const.Balance.hunterBalance, 0, 1f, canJump = false), Enemy(spawnEnemy, IDLE, Assets.Animations.WolfAnimationPack, Assets.Audios.WolfAudioPack, 0, 1f))
    else
      (Player(spawnPlayer, IDLE, Assets.Animations.WolfAnimationPack, Assets.Audios.WolfAudioPack, Const.Balance.wolfBalance, 0, 1f, canJump = true), Enemy(spawnEnemy, IDLE, Assets.Animations.HunterAnimationPack, Assets.Audios.HunterAudioPack, 0, 1f))
  }
}
