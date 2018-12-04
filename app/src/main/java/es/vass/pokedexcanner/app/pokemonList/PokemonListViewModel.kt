package es.vass.pokedexcanner.app.pokemonList

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import es.vass.pokedexcanner.data.model.Pokemon
import es.vass.pokedexcanner.data.repository.DataProvider

class PokemonListViewModel(val dataProvider: DataProvider):ViewModel() {

    //LiveData directo desde DB con la lista de pokemons
    val pokemonList: LiveData<List<Pokemon>>
        get() = dataProvider.getPokedexList()

    //MutableLiveData del pokemon seleccionado (para tablet)
    val selectedPokemonIdFromVM: MutableLiveData<Long> = MutableLiveData()

    //Solicitud al dataProvider de visualizar un pokemon
    fun viewPokemon(id: Long){
        dataProvider.viewPokemon(id)
    }

    //Selección de un pokemon de la lista
    fun setSelectedPokemonId(id: Long?) {
        selectedPokemonIdFromVM.postValue(id)
    }


}