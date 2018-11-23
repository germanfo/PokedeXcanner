package es.vass.pokedexcanner.pokemonList.pokemonDetail

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.vass.pokedexcanner.R
import es.vass.pokedexcanner.dummy.DummyContent
import kotlinx.android.synthetic.main.activity_pokemon_detail.*
import kotlinx.android.synthetic.main.pokemon_detail.view.*

/**
 * A fragment representing a single Pokemon detail screen.
 * This fragment is either contained in a [PokemonListActivity]
 * in two-pane mode (on tablets) or a [PokemonDetailActivity]
 * on handsets.
 */
class PokemonDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private var item: DummyContent.DummyItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                item = DummyContent.ITEM_MAP[it.getString(ARG_ITEM_ID)]
                activity?.toolbar_layout?.title = item?.content
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.pokemon_detail, container, false)

        // Show the dummy content as text in a TextView.
        item?.let {
            rootView.pokemon_detail.text = it.details
        }

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
