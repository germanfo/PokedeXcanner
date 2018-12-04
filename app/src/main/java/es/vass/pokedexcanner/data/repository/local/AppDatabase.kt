package es.vass.pokedexcanner.data.repository.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import es.vass.pokedexcanner.data.model.Pokemon
import es.vass.pokedexcanner.data.repository.local.daos.PokemonDao

//Definición de la DB ROOM y sus entities
@Database(entities = arrayOf(Pokemon::class), version = 1)
abstract class AppDatabase : RoomDatabase() {


    //Singleton de la DB
    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context):AppDatabase? {
            if (instance == null){
                synchronized(AppDatabase::class){
                    //datos del objeto sql
                    instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "pokedexcanner.db")
                        .fallbackToDestructiveMigration().build()
                }
            }
            return instance
        }
    }

    //obtención de los DAOs de la DB
    abstract fun getPokemonDao(): PokemonDao
}