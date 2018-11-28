package es.vass.pokedexcanner.app.pokemonDetail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import es.vass.pokedexcanner.data.model.Pokemon
import es.vass.pokedexcanner.data.repository.DataProvider

class PokemonDetailFragmentViewModel(val dataProvider: DataProvider):ViewModel() {

    private var pokemon: LiveData<Pokemon>? = null
    var isPokemonCatched: MutableLiveData<Boolean> = MutableLiveData()


    fun getPokemonInfo(pokemonId: Long) : LiveData<Pokemon>?{
        if (pokemon == null)
            pokemon = dataProvider.getPokedexEntry(pokemonId)
        return pokemon
    }

    fun catchPokemon(name: String){
        pokemon?.apply{
            observeForever {
                if (it?.altura != null)
                    isPokemonCatched.postValue(true)
            }
        }
        dataProvider.catchPokemon(name)
    }


}