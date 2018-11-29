package es.vass.pokedexcanner.app.pokemonDetail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import es.vass.pokedexcanner.data.model.Pokemon
import es.vass.pokedexcanner.data.repository.DataProvider

class PokemonDetailFragmentViewModel(val dataProvider: DataProvider):ViewModel() {

    val pokemon: MutableLiveData<Pokemon> = MutableLiveData()
    private var pokemonEntryInfo: LiveData<Pokemon>? = null
    var isPokemonCatched: MutableLiveData<Boolean> = MutableLiveData()


    fun loadPokemonInfo(pokemonId: Long){

        pokemonEntryInfo = dataProvider.getPokedexEntry(pokemonId)

        pokemonEntryInfo?.observeForever {
            pokemon.postValue(it)
        }
    }

    fun catchPokemon(name: String){
        pokemon.apply{
            observeForever {
                if (it?.altura != null)
                    isPokemonCatched.postValue(true)
            }
        }
        dataProvider.catchPokemon(name)
    }


}