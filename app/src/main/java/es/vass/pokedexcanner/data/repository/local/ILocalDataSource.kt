package es.vass.pokedexcanner.data.repository.local

import android.arch.lifecycle.LiveData
import es.vass.pokedexcanner.data.model.Pokemon

interface ILocalDataSource {

    fun getPokemonList(): LiveData<List<Pokemon>>
    fun getPokemonById(idPokemon: Long): LiveData<Pokemon>
    fun insertPokemon(pokemon: Pokemon)
}