package es.vass.pokedexcanner.app.pokemonList

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import es.vass.pokedexcanner.data.model.Pokemon
import es.vass.pokedexcanner.data.repository.DataProvider

class PokemonListViewModel(val dataProvider: DataProvider):ViewModel() {

    val pokemonList: LiveData<List<Pokemon>>
        get() = dataProvider.getPokedexList()


    fun viewPokemon(id: Long){
        dataProvider.viewPokemon(id)
    }


}