package es.vass.pokedexcanner.data.repository

import android.arch.lifecycle.LiveData
import es.vass.pokedexcanner.data.model.Pokemon
import es.vass.pokedexcanner.data.repository.local.ILocalDataSource
import es.vass.pokedexcanner.data.repository.remote.IRemoteDataSource
import es.vass.pokedexcanner.data.repository.remote.responses.PokemonExtendedInfoResponse
import es.vass.pokedexcanner.data.repository.remote.responses.PokemonFormResponse
import es.vass.pokedexcanner.domain.AppExecutors

class DataProvider private constructor(remoteDataSource: IRemoteDataSource, localDataSource: ILocalDataSource){
    //Definición de los DataSources
    val remoteDataSource = remoteDataSource
    val localDataSource = localDataSource

    //Singleton
    companion object {
        private  var instance: DataProvider? = null

        fun getInstance(remoteDataSource: IRemoteDataSource, localDataSource: ILocalDataSource): DataProvider? {
            instance?: synchronized(this) {
                instance = DataProvider(remoteDataSource, localDataSource)
            }
            return instance
        }
    }

    /**
     * Método para obtener el listado de pokemons, realizando una llamada al localDataSource que podría ser sustituído por una
     * llamada directa al DAO correspondiente
     * DEVUELVE UN LIVEDATA
     */
    fun getPokedexList():LiveData<List<Pokemon>>{
        return localDataSource.getPokemonList()
    }

    /**
     * Método para obtener un pokemon identificado por su id, realizando una llamada al localDataSource que podría ser sustituído por una
     * llamada directa al DAO correspondiente
     * DEVUELVE UN LIVEDATA
     */
    fun getPokedexEntry(pokemonId: Long): LiveData<Pokemon> {
        return localDataSource.getPokemonById(pokemonId)
    }


    /**
     * Método de solicitud de visualización de un pokemon
     */
    fun viewPokemon(id: Long){
        //Inicialmente se añade a la DB el id del pokemon con el resto de datos desconocidos.
        // En esta ejecución, si hay algún observador que estuviera escuchando la lista o el Id en concreto desde BD (métodos superiores)
        // sería informado del cambio
        AppExecutors.diskIO.execute{ localDataSource.insertPokemon(Pokemon(id,"?","",null,null))}

        // LiveData obtenido desde la solicitud al remoteDataSource para la descarga de datos del pokemon
        val remotePokemonFormData : LiveData<PokemonFormResponse> = remoteDataSource.downloadPokemonViewedData(id)

        // Al obtener el livedata (mientras se realiza la conexión de forma asíncrona) se observa para detectar el cambio cuando
        // se produzca la comunicación. En ese momento se almacena la información en la DB, lo que origniará un aviso a los
        // observadores de la lista o el id del pokemon mediante los métodos superiores
        remotePokemonFormData.observeForever { data ->
            data?.let {

                //Mapeo de datos de la respuesta de RED hacia la Entity y modelo de datos de la app
                val pokemonViewed = Pokemon(it.id.toLong(), it.name, it.sprites.backDefault, null, null)

                //almacenamiento del pokemon en la BD
                AppExecutors.diskIO.execute { localDataSource.insertPokemon(pokemonViewed) }

            }
        }
    }


    /**
     * Método para capturar pokemon, con un funcionamiento similar al método de visualizar pokemon.
     * En este caso no se insertan datos vacíos ne la DB puesto que el pokemon ya existe, solo se van a recoger los datos
     * ampliados del mismo
     */
    fun catchPokemon(name: String){

        val remotePokemonExtendedData : LiveData<PokemonExtendedInfoResponse> = remoteDataSource.downloadPokemonCatchedData(name)

        remotePokemonExtendedData.observeForever { data ->
            data?.let {
                val pokemonCatched = Pokemon(it.id.toLong(), it.name, it.sprites.frontDefault, it.height/10, it.weight/10)

                AppExecutors.diskIO.execute { localDataSource.insertPokemon(pokemonCatched) }

            }
        }
    }




}