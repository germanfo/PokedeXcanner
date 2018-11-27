package es.vass.pokedexcanner.app.pokemonDetail

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import es.vass.pokedexcanner.data.repository.DataProvider

class PokemonDetailFragmentViewModelFactory (private val dataProvider: DataProvider): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PokemonDetailFragmentViewModel(dataProvider) as T
    }
}