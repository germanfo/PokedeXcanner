package es.vass.pokedexcanner.app.splash

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import es.vass.pokedexcanner.Injector
import es.vass.pokedexcanner.R
import es.vass.pokedexcanner.domain.AppExecutors
import es.vass.pokedexcanner.pokemonList.list.PokemonListActivity
import kotlinx.android.synthetic.main.splash.*

class SplashActivity: AppCompatActivity() {

    //Constantes de Duración y delays
    companion object {
        val DURATION: Long = 3000
        val DELAY: Long = 500
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        //Animaciones de la splash activity
        ObjectAnimator.ofFloat(iv_pokedex_top, "translationY", -500f).apply {
            duration = DURATION
            startDelay = DELAY
            start()
        }

        ObjectAnimator.ofFloat(iv_pokedex_bottom, "translationY", 500f).apply {
            duration = DURATION
            startDelay = DELAY
            start()
        }

        ObjectAnimator.ofFloat(background_view, "scaleY", 2f).apply {
            duration = DURATION
            startDelay = DELAY
            start()
        }

        var scaleX = ObjectAnimator.ofFloat(iv_pokedex_ball, "scaleX", 20f)
        var scaleY = ObjectAnimator.ofFloat(iv_pokedex_ball, "scaleY", 20f)

        scaleX.duration = DURATION
        scaleY.duration = DURATION

        var animatorSet = AnimatorSet()
        animatorSet.startDelay = DELAY*2

        animatorSet.play(scaleX).with(scaleY)



        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator){

                startActivity(Intent(this@SplashActivity, PokemonListActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        })

        Handler().postDelayed({
            var mediaPlayer = MediaPlayer.create(this@SplashActivity, R.raw.sound_logon)
            mediaPlayer.start() }, DELAY)

        animatorSet.start()

//region trick
        if (resources.getBoolean(R.bool.isTablet))
        with(Injector.provideDataSource(this)){
            for (i in 1..20)
                this?.viewPokemon(i.toLong())
        }
//endregion

    }
}