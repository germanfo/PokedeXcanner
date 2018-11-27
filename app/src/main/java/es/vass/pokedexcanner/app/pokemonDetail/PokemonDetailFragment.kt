package es.vass.pokedexcanner.pokemonList.pokemonDetail

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import es.vass.pokedexcanner.Injector
import es.vass.pokedexcanner.R
import es.vass.pokedexcanner.app.pokemonDetail.PokemonDetailFragmentViewModel
import es.vass.pokedexcanner.app.pokemonDetail.PokemonDetailFragmentViewModelFactory
import es.vass.pokedexcanner.data.model.Pokemon
import es.vass.pokedexcanner.dummy.DummyContent
import es.vass.pokedexcanner.pokemonList.list.PokemonListActivity
import kotlinx.android.synthetic.main.pokemon_detail.*
import kotlinx.android.synthetic.main.pokemon_detail_data.*
import kotlinx.android.synthetic.main.pokemon_list.*

/**
 * A fragment representing a single Pokemon detail screen.
 * This fragment is either contained in a [PokemonListActivity]
 * in two-pane mode (on tablets) or a [PokemonDetailActivity]
 * on handsets.
 */
class PokemonDetailFragment : Fragment() {

    private var twoPane: Boolean = false

    private var pokemonId : Long? = null

    private var pokemonDetailViewModel : PokemonDetailFragmentViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
               pokemonId = it.getLong(ARG_ITEM_ID)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.pokemon_detail, container, false)


        // Show the dummy content as text in a TextView.
       /* item?.let {
            rootView.pokemon_detail.text = it.details
        }*/

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

            setHasOptionsMenu(true)
        }


        setupViewModel()

        observeViewModel()

        fab.setOnClickListener { view ->
            var mediaPlayer = MediaPlayer.create(context,R.raw.pokeball_throw)
            mediaPlayer.start()

            pokemonDetailViewModel?.apply {
                pokemonId?.let { getPokemonInfo(it)?.value?.nombre }?.let { catchPokemon(it) } }
        }
    }

    fun setupViewModel(){
        if (!twoPane){

            context?.let {
            pokemonDetailViewModel = ViewModelProviders.of(this,
                Injector.providePokemonDetailFragmentViewModelFactory(it)).get(PokemonDetailFragmentViewModel::class.java)
            }
        }
    }

    fun observeViewModel(){
        pokemonDetailViewModel?.let {viewModel ->
            pokemonId?.let { pokemonId ->

                viewModel.getPokemonInfo(pokemonId)?.observe(this, Observer { pokemon ->
                    if (pokemon != null)
                        presentPokemonData(pokemon)
                })
            }
        }
    }

    fun presentPokemonData(pokemon: Pokemon){
        Picasso.get().load(pokemon.imagen).into(iv_pokemon_sprite_detail)
        if (twoPane)
            detail_toolbar.title = pokemon.nombre
        else
            (activity as AppCompatActivity).supportActionBar?.title = pokemon.nombre

        tv_pokemon_item_height_detail.text = pokemon.altura?.toString() ?: "?"
        tv_pokemon_item_name_detail.text = pokemon.nombre
        tv_pokemon_item_number_detail.text = pokemon.id.toString()
        tv_pokemon_item_weight_detail.text = pokemon.peso?.toString() ?: "?"

        Picasso.get().load(pokemon.imagen).into(iv_detalle_pokemon_grande)
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
