package es.vass.pokedexcanner.app.pokemonList

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import es.vass.pokedexcanner.data.repository.DataProvider

class PokemonListViewModelFactory (private val dataProvider: DataProvider): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PokemonListViewModel(dataProvider) as T
    }
}