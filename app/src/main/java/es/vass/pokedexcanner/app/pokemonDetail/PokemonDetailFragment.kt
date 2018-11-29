package es.vass.pokedexcanner.pokemonList.pokemonDetail

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import es.vass.pokedexcanner.Injector
import es.vass.pokedexcanner.R
import es.vass.pokedexcanner.app.alert
import es.vass.pokedexcanner.app.pokemonDetail.PokemonDetailFragmentViewModel
import es.vass.pokedexcanner.app.pokemonList.PokemonListViewModel
import es.vass.pokedexcanner.data.model.Pokemon
import es.vass.pokedexcanner.pokemonList.list.PokemonListActivity
import kotlinx.android.synthetic.main.pokemon_detail.*
import kotlinx.android.synthetic.main.pokemon_detail_data.*
import java.lang.Exception

/**
 * A fragment representing a single Pokemon detail screen.
 * This fragment is either contained in a [PokemonListActivity]
 * in two-pane mode (on tablets) or a [PokemonDetailActivity]
 * on handsets.
 */
class PokemonDetailFragment : Fragment() {

    private var twoPane: Boolean = false

    private var pokemonIdFromBundle : Long? = null

    private var pokemonDetailViewModel : PokemonDetailFragmentViewModel? = null

    private var pokemonListViewModel : PokemonListViewModel? = null

    private var stopBouncePokeballAnimation : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
               pokemonIdFromBundle = it.getLong(ARG_ITEM_ID)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.pokemon_detail, container, false)


        return rootView
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (resources.getBoolean(R.bool.isTablet)){
            twoPane = true
        }

        if (!twoPane) {
            (activity as AppCompatActivity).let {
                it.setSupportActionBar(detail_toolbar)
                it.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }

        }


        setupViewModel()

        observeViewModel()

        fab_catch.setOnClickListener { view ->

            if (pokemonDetailViewModel?.pokemon?.value?.altura != null){
                context?.alert("Ya tienes este pokemon")
            }
            else {
                var mediaPlayer = MediaPlayer.create(context, R.raw.pokeball_throw)
                mediaPlayer.start()

                animateBouncingPokeball()


                pokemonDetailViewModel?.apply {
                    pokemon.value?.nombre?.let { catchPokemon(it) }
                }
            }
        }

        fab_catch.hide()

    }

    fun animateBouncingPokeball(){
        ViewCompat.animate(fab_catch).rotation(60f).withLayer().setDuration(200).setInterpolator(OvershootInterpolator()).withEndAction {
            ViewCompat.animate(fab_catch).rotation(-60f).withLayer().setDuration(200).setInterpolator(OvershootInterpolator()).setStartDelay(100).withEndAction {
                ViewCompat.animate(fab_catch).rotation(60f).withLayer().setDuration(200).setInterpolator(OvershootInterpolator()).setStartDelay(100).withEndAction {
                        ViewCompat.animate(fab_catch).rotation(0f).withLayer().setDuration(200)
                            .setInterpolator(OvershootInterpolator()).setStartDelay(100).withEndAction {
                                if (stopBouncePokeballAnimation) {
                                    ViewCompat.animate(fab_catch).scaleXBy(2f).scaleYBy(2f).withLayer().setDuration(200)
                                        .setInterpolator(OvershootInterpolator()).setStartDelay(100).withEndAction {
                                        ViewCompat.animate(fab_catch).scaleXBy(-2f).scaleYBy(-2f).withLayer().setDuration(200)
                                            .setInterpolator(OvershootInterpolator()).setStartDelay(100).start()
                                      }.start()
                                } else{
                                    animateBouncingPokeball()
                                }
                        }.start()
                }.start()
            }.start()
        }.start()
    }

    fun setupViewModel(){
        if (twoPane){
            pokemonListViewModel =
                    activity?.let { ViewModelProviders.of(it,
                        context?.let { it1 -> Injector.providePokemonListViewModelFactory(it1) }).get(PokemonListViewModel::class.java) }
        }

        context?.let {
        pokemonDetailViewModel = ViewModelProviders.of(this,
            Injector.providePokemonDetailFragmentViewModelFactory(it)).get(PokemonDetailFragmentViewModel::class.java)
        }

    }

    fun observeViewModel(){
        pokemonDetailViewModel?.let {detailViewModel ->

            detailViewModel.pokemon.observe(this, Observer { pokemon ->
                if (pokemon != null)
                    presentPokemonData(pokemon)
            })


            detailViewModel.isPokemonCatched.observe(this, Observer { isCatched ->
                if (isCatched != null)
                    stopBouncePokeballAnimation = isCatched
            })
        }

        pokemonListViewModel?.selectedPokemonIdFromVM?.observe(this, Observer { selectedId ->
            selectedId?.let { pokemonSelectedId ->
                pokemonDetailViewModel?.loadPokemonInfo(pokemonSelectedId)
            }
        })
            ?:
        pokemonIdFromBundle?.let { pokemonDetailViewModel?.loadPokemonInfo(it) }
    }

    fun presentPokemonData(pokemon: Pokemon){
        if (!TextUtils.isEmpty(pokemon.imagen)) {


            var goodImage = "https://assets.pokemon.com/assets/cms2/img/pokedex/full/${pokemon.idFilledWithZero()}.png"

            if (pokemon.altura != null)
                Picasso.get().load(goodImage).into(iv_detalle_pokemon_grande)
            else
                iv_detalle_pokemon_grande.setImageBitmap(null)

            Picasso.get().load(pokemon.imagen).into(object: Target{
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                }

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    iv_pokemon_sprite_detail.setImageBitmap(bitmap)

                    bitmap?.let{
                        toolbar_layout.setBackgroundColor(Palette.from(bitmap).generate().vibrantSwatch?.rgb ?: context?.let { context ->
                            ContextCompat.getColor(
                                context, R.color.colorPrimary)
                        }!!)
                    }
                }

            })
        }

        toolbar_layout.title = pokemon.nombre

        tv_pokemon_item_height_detail.text = "${pokemon.altura?.toString() ?: "?"} m"
        tv_pokemon_item_name_detail.text = pokemon.nombre
        tv_pokemon_item_number_detail.text = pokemon.id.toString()
        tv_pokemon_item_weight_detail.text = "${pokemon.peso?.toString() ?: "?"} Kg"

        fab_catch.show()

    }



    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back

                NavUtils.navigateUpTo(activity as Activity, Intent(context, PokemonListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
