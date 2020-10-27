package es.vass.pokedexcanner.app.pokemonList

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import es.vass.pokedexcanner.R
import es.vass.pokedexcanner.data.model.Pokemon
import es.vass.pokedexcanner.pokemonList.list.PokemonListActivity
import es.vass.pokedexcanner.pokemonList.pokemonDetail.PokemonDetailActivity
import es.vass.pokedexcanner.pokemonList.pokemonDetail.PokemonDetailFragment
import kotlinx.android.synthetic.main.pokemon_list_content.view.*

/**
 * RV Adapter con constructor parentActivity y twoPane como parámetros
 */
class PokemonListAdapter (private val parentActivity: PokemonListActivity, private val twoPane: Boolean) : RecyclerView.Adapter<PokemonListAdapter.PokemonViewHolder>() {

    //Onclick listener
    private val onClickListener : View.OnClickListener
    var pokemonList: List<Pokemon>? = null

    //Inicialización del onclickListener
    init {
        onClickListener = View.OnClickListener { view ->
            val item = view.tag as Pokemon
            if (twoPane) {
                //En tablet se pide a la activity que almacene el id del pokemon seleccionado
                parentActivity.setSelectedPokemonId(item.id)
            } else {
                //En SMP se lanza la Activity de detalle con el id como argumento
                val intent = Intent(parentActivity, PokemonDetailActivity::class.java).apply {
                    putExtra(PokemonDetailFragment.ARG_ITEM_ID, item.id)
                }
                parentActivity.startActivity(intent)
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
        with(pokemonViewHolder.view){
            tag = pokemonList?.get(position)
            setOnClickListener(onClickListener)

            pokemonList?.get(position)?.let {
                tv_pokemon_item_number.text = it.id.toString()
                tv_pokemon_item_name.text = it.nombre
                tv_pokemon_item_height.text = "${it.altura?.toString() ?: "?"} m"
                tv_pokemon_item_weight.text = "${it.peso?.toString() ?: "?"} Kg"

                if (!TextUtils.isEmpty(it.imagen))
                    Picasso.get().load(it.imagen).into(iv_item_pokemon)
                else
                    iv_item_pokemon.setImageResource(R.drawable.ic_help_outline_black_24dp)
            }
        }

    }


    class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }
}
