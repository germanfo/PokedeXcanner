package es.vass.pokedexcanner.app.pokemonDetail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import es.vass.pokedexcanner.data.model.Pokemon
import es.vass.pokedexcanner.data.repository.DataProvider

class PokemonDetailFragmentViewModel(val dataProvider: DataProvider):ViewModel() {

    //MutableLiveData de Pokemon, modificaremos su valor cuando recibamos datos nuevos de pokemon
    val pokemon: MutableLiveData<Pokemon> = MutableLiveData()

    //LiveData de Pokemon, representación del Pokemon en DB
    private var pokemonEntryInfo: LiveData<Pokemon>? = null

    //MutableLiveData boolean, modifcaremos su valor al capturar un pokemon
    var isPokemonCatched: MutableLiveData<Boolean> = MutableLiveData()


    /**
     * Se ajusta el Livedata al obtenido desde el DataProvider y se comienza a observar su valor (Este comportamiento permite que la
     * acción de carga de los datos del pokémon sea asíncrona)
     * Cuando se detecta un cambio de valor en el LiveData, se modifica el MutableLiveData del pokemon para
     * que los observadores del mismo detecten el cambio de información
     */
    fun loadPokemonInfo(pokemonId: Long){

        pokemonEntryInfo = dataProvider.getPokedexEntry(pokemonId)

        pokemonEntryInfo?.observeForever {
            pokemon.postValue(it)
        }
    }


    /**
     * Se observa el MutableLiveData del pokemon para detectar cuando su altura deja de ser null, momento en el que se puede
     * considerar que el pokemon ha sido capturado y se modifica el valor del MutableLiveData boolean para que los observadores
     * del mismo detecten el cambio
     * Se solicita al dataProvider que capture el pokemon indicado, esta operación terminará realizando modificaciones
     * en la DB para el pokemon en cuestión lo cual desencadenará un cambio en el LiveData Pokemon
     *
     * Network -DPLivedata-> DB -DPLivedata-> VM(LD) -VMobserveForever-> VM(MutableLD) -Frgobserve-> presentPokemonData
     */
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