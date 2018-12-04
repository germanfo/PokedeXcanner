package es.vass.pokedexcanner.pokemonList.pokemonDetail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import es.vass.pokedexcanner.R
import es.vass.pokedexcanner.pokemonList.list.PokemonListActivity

/**
 * An activity representing a single Pokemon detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [PokemonListActivity].
 */
class PokemonDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_detail)

        //Creación y presentación del fragment de Detalle pasando el POKEMON ID como argumento
        if (savedInstanceState == null) {

            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val fragment = PokemonDetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(
                        PokemonDetailFragment.ARG_ITEM_ID,
                        intent.getLongExtra(PokemonDetailFragment.ARG_ITEM_ID, -1)
                    )
                }
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.pokemon_detail_container, fragment)
                .commit()
        }
    }


}
