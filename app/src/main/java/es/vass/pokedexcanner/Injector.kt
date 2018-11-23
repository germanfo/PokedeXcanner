package es.vass.pokedexcanner

import android.content.Context
import es.vass.pokedexcanner.app.pokemonList.PokemonListViewModel
import es.vass.pokedexcanner.app.pokemonList.PokemonListViewModelFactory
import es.vass.pokedexcanner.data.repository.DataProvider
import es.vass.pokedexcanner.data.repository.local.LocalDataSource
import es.vass.pokedexcanner.data.repository.remote.RemoteDataSource
import es.vass.pokedexcanner.domain.AppExecutors

object Injector{

    fun provideDataSource(context: Context): DataProvider? {
        return DataProvider.getInstance(RemoteDataSource(),LocalDataSource(context))
    }


    fun providePokemonListViewModelFactory(context: Context):PokemonListViewModelFactory?{
        return provideDataSource(context)?.let { PokemonListViewModelFactory(it) }
    }

}