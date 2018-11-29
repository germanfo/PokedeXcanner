package es.vass.pokedexcanner.app.pokemonList

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import es.vass.pokedexcanner.data.model.Pokemon
import es.vass.pokedexcanner.data.repository.DataProvider

class PokemonListViewModel(val dataProvider: DataProvider):ViewModel() {

    val pokemonList: LiveData<List<Pokemon>>
        get() = dataProvider.getPokedexList()

    val selectedPokemonId: MutableLiveData<Long> = MutableLiveData()


    fun viewPokemon(id: Long){
        dataProvider.viewPokemon(id)
    }

    fun setSelectedPokemonId(id: Long?) {
        selectedPokemonId.postValue(id)
    }


}