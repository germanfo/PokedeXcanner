package es.vass.pokedexcanner.app.pokemonList

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.vass.pokedexcanner.R
import es.vass.pokedexcanner.data.model.Pokemon
import es.vass.pokedexcanner.pokemonList.list.PokemonListActivity
import es.vass.pokedexcanner.pokemonList.pokemonDetail.PokemonDetailActivity
import es.vass.pokedexcanner.pokemonList.pokemonDetail.PokemonDetailFragment
import kotlinx.android.synthetic.main.pokemon_list_content.view.*


class PokemonListAdapter (private val parentActivity: PokemonListActivity, private val twoPane: Boolean) : RecyclerView.Adapter<PokemonListAdapter.PokemonViewHolder>() {

    private val onClickListener : View.OnClickListener
    var pokemonList: List<Pokemon>? = null

    init {
        onClickListener = View.OnClickListener { view ->
            val item = view.tag as Pokemon
            if (twoPane) {
                val fragment = PokemonDetailFragment().apply {
                    arguments = Bundle().apply {
                        item.id?.let {
                            putLong(PokemonDetailFragment.ARG_ITEM_ID, it)
                        }
                    }
                }
                parentActivity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.pokemon_detail_container, fragment)
                    .commit()
            } else {
                val intent = Intent(view.context, PokemonDetailActivity::class.java).apply {
                    putExtra(PokemonDetailFragment.ARG_ITEM_ID, item.id)
                }
                view.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): PokemonViewHolder {
        return PokemonViewHolder(LayoutInflater.from(parentActivity.applicationContext).inflate(R.layout.pokemon_list_content, parent, false))
    }

    override fun getItemCount(): Int {
       return pokemonList?.size?:0
    }

    override fun onBindViewHolder(pokemonViewHolder: PokemonViewHolder, position: Int) {
        pokemonViewHolder.tvIdText.text = pokemonList?.get(position)?.id.toString()
        pokemonViewHolder.tvTextContent.text = pokemonList?.get(position)?.nombre

        with(pokemonViewHolder.itemView) {
            tag = pokemonList?.get(position)
            setOnClickListener(onClickListener)
        }
    }


    class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIdText = itemView.id_text
        val tvTextContent = itemView.content
    }
}
