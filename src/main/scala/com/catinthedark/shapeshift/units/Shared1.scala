package com.catinthedark.shapeshift.units

import com.catinthedark.shapeshift.common.Const.Balance.{hunterBalance, wolfBalance}
import com.catinthedark.shapeshift.{Assets, Shared0}
import com.catinthedark.shapeshift.common.Const
import com.catinthedark.shapeshift.entity._
import com.catinthedark.shapeshift.network.NetworkServerControl
import com.catinthedark.shapeshift.view.IDLE

import scala.collection.mutable


class Shared1(val shared0: Shared0,
              var entities: mutable.ListBuffer[Entity],
              var jumpingAngle: Float = 0,
              var jumpTime: Float = 0,
              var jumpScale: Float = 0,
              var playerTraces: mutable.Queue[Trace] = new mutable.Queue[Trace](),
              var enemyTraces: mutable.Queue[Trace] = new mutable.Queue[Trace]()) {
  var (player, enemy) = init()

  def reset() = {
    entities.clear()
    val pe = init()
    player = pe._1
    enemy = pe._2
    playerTraces.clear()
    enemyTraces.clear()
  }

  def init() = {
    val (spawnPlayer, spawnEnemy) = Const.Balance.randomSpawn
    if (shared0.networkControl.isInstanceOf[NetworkServerControl])
      (Player(spawnPlayer, IDLE, Assets.Animations.HunterAnimationPack, Assets.Audios.HunterAudioPack, hunterBalance, 0, 1f, canJump = false), Enemy(spawnEnemy, IDLE, Assets.Animations.WolfAnimationPack, Assets.Audios.WolfAudioPack, wolfBalance, 0, 1f))
    else
      (Player(spawnEnemy, IDLE, Assets.Animations.WolfAnimationPack, Assets.Audios.WolfAudioPack, wolfBalance, 0, 1f, canJump = true), Enemy(spawnPlayer, IDLE, Assets.Animations.HunterAnimationPack, Assets.Audios.HunterAudioPack, hunterBalance, 0, 1f))
  }
}
