package com.catinthedark.shapeshift.units

import com.catinthedark.lib.constants.{FRange, Vec2Range}
import com.catinthedark.lib.{Deferred, MagicSpriteBatch, SimpleUnit}
import com.catinthedark.shapeshift.Assets
import com.catinthedark.shapeshift.common.Const
import com.catinthedark.shapeshift.view._

abstract class EnemyView(val shared: Shared1, range: Vec2Range, speed: FRange) extends SimpleUnit with Deferred {
}
