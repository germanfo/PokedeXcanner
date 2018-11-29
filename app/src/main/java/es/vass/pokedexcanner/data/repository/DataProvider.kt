package es.vass.pokedexcanner.data.repository

import android.arch.lifecycle.LiveData
import es.vass.pokedexcanner.data.model.Pokemon
import es.vass.pokedexcanner.data.repository.local.ILocalDataSource
import es.vass.pokedexcanner.data.repository.remote.IRemoteDataSource
import es.vass.pokedexcanner.data.repository.remote.responses.PokemonExtendedInfoResponse
import es.vass.pokedexcanner.data.repository.remote.responses.PokemonFormResponse
import es.vass.pokedexcanner.domain.AppExecutors

class DataProvider private constructor(remoteDataSource: IRemoteDataSource, localDataSource: ILocalDataSource){

    val remoteDataSource = remoteDataSource
    val localDataSource = localDataSource

    companion object {
        private  var instance: DataProvider? = null

        fun getInstance(remoteDataSource: IRemoteDataSource, localDataSource: ILocalDataSource): DataProvider? {
            instance?: synchronized(this) {
                instance = DataProvider(remoteDataSource, localDataSource)
            }
            return instance
        }
    }


    fun getPokedexList():LiveData<List<Pokemon>>{
        return localDataSource.getPokemonList()
    }

    fun viewPokemon(id: Long){
        //Añadimos el id del pokemon, y vamos a recoger sus datos
        AppExecutors.diskIO.execute{ localDataSource.insertPokemon(Pokemon(id,"?","",null,null))}

        val remotePokemonFormData : LiveData<PokemonFormResponse> = remoteDataSource.downloadPokemonViewedData(id)

        remotePokemonFormData.observeForever { data ->
            data?.let {
                val pokemonViewed = Pokemon(it.id.toLong(), it.name, it.sprites.backDefault, null, null)

                AppExecutors.diskIO.execute { localDataSource.insertPokemon(pokemonViewed) }

            }
        }
    }

    fun catchPokemon(name: String){

        val remotePokemonExtendedData : LiveData<PokemonExtendedInfoResponse> = remoteDataSource.downloadPokemonCatchedData(name)

        remotePokemonExtendedData.observeForever { data ->
            data?.let {
                val pokemonCatched = Pokemon(it.id.toLong(), it.name, it.sprites.frontDefault, it.height/10, it.weight/10)

                AppExecutors.diskIO.execute { localDataSource.insertPokemon(pokemonCatched) }

            }
        }
    }

    fun getPokedexEntry(pokemonId: Long): LiveData<Pokemon> {
        return localDataSource.getPokemonById(pokemonId)
    }


}