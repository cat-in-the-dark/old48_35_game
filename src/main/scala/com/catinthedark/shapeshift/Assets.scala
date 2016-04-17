package com.catinthedark.shapeshift

import com.badlogic.gdx.graphics.Texture.TextureWrap
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.g2d.{Animation, TextureRegion}
import com.badlogic.gdx.graphics.{Pixmap, Texture}
import com.badlogic.gdx.maps.tiled.{TmxMapLoader, TiledMap}
import com.badlogic.gdx.{Gdx, utils}
import com.catinthedark.shapeshift.common.Const

object Assets {
  object Maps {
    val map1: TiledMap = new TmxMapLoader().load("map.tmx")
  }
  object Shaders {
    val clipVert = Gdx.files.internal("shaders/clip.vert")
    val clipVert1 = Gdx.files.internal("shaders/clip_1.vert")
    val clipFrag = Gdx.files.internal("shaders/clip.frag")
    val clipFrag1 = Gdx.files.internal("shaders/clip_1.frag")
  }
  object Textures {
    val logo = new Texture(Gdx.files.internal("textures/logo.png"))

//    val t0 = new Texture(Gdx.files.internal("textures/start_screen.png"))
//    val t1 = new Texture(Gdx.files.internal("textures/tut1eng.png"))
//    val t2 = new Texture(Gdx.files.internal("textures/tut2eng.png"))
//    val t3 = new Texture(Gdx.files.internal("textures/tut3eng.png"))
//    val t4 = new Texture(Gdx.files.internal("textures/tut4eng.png"))
//    val t4 = new Texture(Gdx.files.internal("textures/tut5eng.png"))
    trait ThemePack {
      val winScreen: Texture
      val pairing: Texture
      val loseScreen: Texture
      val body: Texture 
      val bodyFrames: Array[Array[TextureRegion]]
    }

    val floor = new Texture(Gdx.files.internal("textures/floor.png"))
    floor.setWrap(TextureWrap.Repeat, TextureWrap.Repeat)
    val tree1 = new Texture(Gdx.files.internal("textures/tree1.png"))
    val tree2 = new Texture(Gdx.files.internal("textures/tree2.png"))
    val tree3 = new Texture(Gdx.files.internal("textures/tree3.png"))

    object HunterThemePack extends ThemePack {
      override val winScreen: Texture = new Texture(Gdx.files.internal("textures/logo.png"))
      override val pairing: Texture = new Texture(Gdx.files.internal("textures/pairing.png"))
      override val loseScreen: Texture = new Texture(Gdx.files.internal("textures/logo.png"))
      override val body: Texture = new Texture(Gdx.files.internal("textures/hunter_pack/body.png"))
      override val bodyFrames = TextureRegion.split(body, 215, 297)
    }

    object WolfThemePack extends ThemePack {
      override val winScreen: Texture = new Texture(Gdx.files.internal("textures/logo.png"))
      override val pairing: Texture = new Texture(Gdx.files.internal("textures/pairing.png"))
      override val loseScreen: Texture = new Texture(Gdx.files.internal("textures/logo.png"))
      override val body: Texture = new Texture(Gdx.files.internal("textures/woolf_pack/body.png"))
      override val bodyFrames = TextureRegion.split(body, 48, 48)
    }

  }

  object Fonts {
    val mainGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font/main.ttf"))
    val moneyFontParam = new FreeTypeFontParameter()
    moneyFontParam.size = 44
    val hudFont = mainGenerator.generateFont(moneyFontParam)
    hudFont.setColor(92f / 255, 85f / 255, 103f / 255, 1)

    val ctrlFont = mainGenerator.generateFont(moneyFontParam)
    ctrlFont.setColor(224f / 255, 248f / 255, 18f / 255, 1)
  }

  object Animations {
    private def loopingAnimation(frames: Array[Array[TextureRegion]], frameIndexes: (Int, Int)*): Animation = {
      val array = new utils.Array[TextureRegion]
      frameIndexes.foreach(i => array.add(frames(i._1)(i._2)))
      new Animation(Const.UI.animationSpeed, array, Animation.PlayMode.LOOP)
    }

    private def normalAnimation(speed: Float, frames: Array[Array[TextureRegion]], frameIndexes: (Int, Int)*): Animation = {
      val array = new utils.Array[TextureRegion]
      frameIndexes.foreach(i => array.add(frames(i._1)(i._2)))
      new Animation(speed, array, Animation.PlayMode.NORMAL)
    }
    
    trait PlayerAnimationPack {
      val shooting: Animation
      val running: Animation
      val idle: TextureRegion
    }
    
    object HunterAnimationPack extends PlayerAnimationPack {
      private val textures = Textures.HunterThemePack
      
      override val running: Animation = loopingAnimation(textures.bodyFrames, (0,2), (0, 3), (0, 4), (0, 5), (0, 6), (0, 7), (0, 8))
      override val idle: TextureRegion = textures.bodyFrames(0)(0)
      override val shooting: Animation = normalAnimation(Const.UI.animationSpeed, textures.bodyFrames, (0, 9), (0, 10), (0, 11))
    }

    object WolfAnimationPack extends PlayerAnimationPack {
      private val textures = Textures.WolfThemePack
      
      override val running: Animation = loopingAnimation(textures.bodyFrames, (0,0), (0, 1), (0, 2))
      override val idle: TextureRegion = textures.bodyFrames(0)(1)
      override val shooting: Animation = normalAnimation(Const.UI.animationSpeed, textures.bodyFrames, (0, 3))
    }
  }

  object Audios {
    val bgm = Gdx.audio.newMusic(Gdx.files.internal("sound/bgm.mp3"))
    bgm.setLooping(true)
    val shoot = Gdx.audio.newSound(Gdx.files.internal("sound/shoot.mp3"))
    val ricochet = Gdx.audio.newSound(Gdx.files.internal("sound/ricochet.mp3"))
  }
}
