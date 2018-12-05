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

    companion object {
        /**
         * Constante del argumento recibido por BUNDLE que indica el ID del pokemon
         */
        const val ARG_ITEM_ID = "item_id"
    }

    //Indicador de tablet
    private var twoPane: Boolean = false

    //ID del pokemon desde Bundle (modo smartphone)
    private var pokemonIdFromBundle : Long? = null

    //ViewModel del Fragment
    private var pokemonDetailViewModel : PokemonDetailFragmentViewModel? = null

    //ViewModel de la Activity (solo en modo tablet)
    private var pokemonListViewModel : PokemonListViewModel? = null

    //boolean indicador para detener la animación
    private var stopBouncePokeballAnimation : Boolean = false


//region LifeCycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Se recoge el ID del pokemon desde el bundle, si está presente (modo SMP)
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

        //Se identifica si es tablet
        if (resources.getBoolean(R.bool.isTablet)){
            twoPane = true
        }

        //Setea la Toolbar si es modo SMP
        if (!twoPane) {
            (activity as AppCompatActivity).let {
                it.setSupportActionBar(detail_toolbar)
                it.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }

        }

        //Setups y observers
        setupEvents()
        setupViewModel()

        observeViewModel()

    }


    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {

                NavUtils.navigateUpTo(activity as Activity, Intent(context, PokemonListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

//endregion

//region UI Events & Presentations
    fun setupEvents(){
        fab_catch.setOnClickListener { view ->

            //Si la altura del pokemon no es null, ya ha sido capturado
            if (pokemonDetailViewModel?.pokemon?.value?.altura != null){
                context?.alert("Ya tienes este pokemon")
            }
            else {

                //Reproducción de sonido de lanzamiento + inico de animación
                var mediaPlayer = MediaPlayer.create(context, R.raw.pokeball_throw)
                mediaPlayer.start()

                animateBouncingPokeball()


                //Solicitudo al VM de operación de captura
                pokemonDetailViewModel?.apply {
                    pokemon.value?.nombre?.let { catchPokemon(it) }
                }
            }
        }

        fab_catch.hide()
    }

    /**
     * Función de concatenación de animaciones de forma recursiva hasta que se active el flag de parada de animación
     */
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

    //Val de TargetImage para la carga del sprite del pokemon mediante Picasso y la utilización de su color más vivo
    //para el fondo de la toolbar
    val targetImage = object : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            iv_pokemon_sprite_detail?.setImageBitmap(bitmap)

            bitmap?.let{
                toolbar_layout?.setBackgroundColor(Palette.from(bitmap).generate().vibrantSwatch?.rgb ?: context?.let { context ->
                    ContextCompat.getColor(
                        context, R.color.colorPrimary)
                }!!)
            }
        }

    }

    /**
     * Presentación de los datos de un pokemon
     */
    fun presentPokemonData(pokemon: Pokemon){
        iv_detalle_pokemon_grande.setImageDrawable(null)
        iv_pokemon_sprite_detail.setImageDrawable(null)

        Picasso.get().cancelRequest(iv_detalle_pokemon_grande)
        Picasso.get().cancelRequest(targetImage)

        if (!TextUtils.isEmpty(pokemon.imagen)) {

            var goodImage = "https://assets.pokemon.com/assets/cms2/img/pokedex/full/${pokemon.idFilledWithZero()}.png"

            if (pokemon.altura != null) {
                Picasso.get().load(goodImage).into(iv_detalle_pokemon_grande)
            }
            else {
                iv_detalle_pokemon_grande.setImageBitmap(null)
            }


            Picasso.get().load(pokemon.imagen).into(targetImage)
        }

        toolbar_layout.title = pokemon.nombre

        tv_pokemon_item_height_detail.text = "${pokemon.altura?.toString() ?: "?"} m"
        tv_pokemon_item_name_detail.text = pokemon.nombre
        tv_pokemon_item_number_detail.text = pokemon.id.toString()
        tv_pokemon_item_weight_detail.text = "${pokemon.peso?.toString() ?: "?"} Kg"

        fab_catch.show()

    }


//endregion

//region VM
    /**
     * Preparación de los ViewModels
     */
    fun setupViewModel(){

        //Si es TAB se recoge el viewmodel de la activity
        if (twoPane){
            pokemonListViewModel =
                    activity?.let { ViewModelProviders.of(it,
                        context?.let { it1 -> Injector.providePokemonListViewModelFactory(it1) }).get(PokemonListViewModel::class.java) }
        }

        //Se recoge el viewModel del fragment
        context?.let {
        pokemonDetailViewModel = ViewModelProviders.of(this,
            Injector.providePokemonDetailFragmentViewModelFactory(it)).get(PokemonDetailFragmentViewModel::class.java)
        }

    }

    /**
     * Se observan los distintos livedata de los VM
     */
    fun observeViewModel(){

        // Se observa el LiveData de "Pokemon" para presentar la información del pokemon seleccionado
        pokemonDetailViewModel?.let {detailViewModel ->

            detailViewModel.pokemon.observe(this, Observer { pokemon ->
                if (pokemon != null)
                    presentPokemonData(pokemon)
            })

            //Se observa el boolean indicador de pokemon capturado para activar el flag de parada de animación
            detailViewModel.isPokemonCatched.observe(this, Observer { isCatched ->
                if (isCatched != null)
                    stopBouncePokeballAnimation = isCatched
            })
        }

        //Si el viewmodel de la activity no es null se observa el LiveData del ID Pokmeon para iniciar la carga
        //de datos del pokemon cuando cambie, en otro caso se inicia directamente la carga de datos del pokemon
        //basándonos en el ID recibido por bundle
        pokemonListViewModel?.selectedPokemonIdFromVM?.observe(this, Observer { selectedId ->
            selectedId?.let { pokemonSelectedId ->
                pokemonDetailViewModel?.loadPokemonInfo(pokemonSelectedId)
            }
        })
            ?:
        pokemonIdFromBundle?.let { pokemonDetailViewModel?.loadPokemonInfo(it) }
    }
//endregion





}
