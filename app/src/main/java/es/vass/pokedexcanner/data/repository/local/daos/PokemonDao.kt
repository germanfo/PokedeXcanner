package es.vass.pokedexcanner.data.repository.local.daos

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import es.vass.pokedexcanner.data.model.Pokemon

@Dao
interface PokemonDao {

    @Query("SELECT * from listaPokemon")
    fun getAll(): LiveData<List<Pokemon>>

    @Insert(onConflict = REPLACE)
    fun insert(pokemon:Pokemon)

    @Insert(onConflict = REPLACE)
    fun insertAll(vararg pokemons: Pokemon)

    @Query("SELECT * from listaPokemon WHERE id = :pokemonId")
    fun getById(pokemonId: Long): LiveData<Pokemon>

}