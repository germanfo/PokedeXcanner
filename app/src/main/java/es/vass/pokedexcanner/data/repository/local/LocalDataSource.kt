package es.vass.pokedexcanner.data.repository.local

import android.arch.lifecycle.LiveData
import android.content.Context
import es.vass.pokedexcanner.data.model.Pokemon
import es.vass.pokedexcanner.data.repository.local.daos.PokemonDao

/**
 * Clase innecesaria, los DAOs individuales sustituirían y dividirían de forma más organizada las acciones sobre DB
 */
class LocalDataSource : ILocalDataSource{

    lateinit var pokemonDao: PokemonDao

    constructor(context: Context){
        val database = AppDatabase.getInstance(context)
        database?.let {
            pokemonDao = database.getPokemonDao()
        }
    }

    override fun getPokemonList(): LiveData<List<Pokemon>>{
            return pokemonDao.getAll()
    }

    override fun getPokemonById(idPokemon: Long): LiveData<Pokemon>{
        return pokemonDao.getById(idPokemon)

    }

    override fun insertPokemon(pokemon: Pokemon) {
        pokemonDao.insert(pokemon)
    }

}