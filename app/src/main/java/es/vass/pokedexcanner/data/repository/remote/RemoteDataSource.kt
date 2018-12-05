package es.vass.pokedexcanner.data.repository.remote

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import es.vass.pokedexcanner.data.repository.remote.responses.PokemonExtendedInfoResponse
import es.vass.pokedexcanner.data.repository.remote.responses.PokemonFormResponse
import es.vass.pokedexcanner.domain.AppExecutors
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RemoteDataSource : IRemoteDataSource{

    val BASE_URL = "https://pokeapi.co/api/v2/"
    val TIMEOUT: Long = 30

    var apiServices: PokemonApi

    init {
        val httpClient : OkHttpClient.Builder = OkHttpClient.Builder()
        httpClient.connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        httpClient.readTimeout(TIMEOUT, TimeUnit.SECONDS)
        httpClient.writeTimeout(TIMEOUT, TimeUnit.SECONDS)

        val retrofit: Retrofit = Retrofit.Builder()
       //Se utiliza el networkIO como ejecutor de Retrofit
            .callbackExecutor(AppExecutors.networkIO)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()

        apiServices = retrofit.create(PokemonApi::class.java)
    }


    /**
     * Función tipo utilizando retrofit para obtener datos desde una api remota
     */
    override fun downloadPokemonViewedData(id: Long): LiveData<PokemonFormResponse> {
        //definición de la CALL de retrofit
       var call : Call<PokemonFormResponse> = apiServices.getPokemonInfo(id)

        //MutableLiveData del tipo de respuesta de la API
        var pokemonFormResponseLD: MutableLiveData<PokemonFormResponse> = MutableLiveData()

        //Llamada asíncrona a la API
        call.enqueue(object : Callback<PokemonFormResponse>{

            override fun onResponse(call: Call<PokemonFormResponse>, response: Response<PokemonFormResponse>?){
                //Al obtener la respuesta de forma asíncrona se modifica el valor del mutable livedata anteriormente definido
                //para que almacene los datos de la respuesta, lo que provocará una acción en los observadores
                response?.let {
                    pokemonFormResponseLD.postValue(response.body())

                } ?: pokemonFormResponseLD.postValue(null)

            }
            override fun onFailure(call: Call<PokemonFormResponse>, t: Throwable) {
                pokemonFormResponseLD.postValue(null)
            }
        })

        //Devolución del objeto livedata (se realiza inmediatamente dado que la llamda a la api es asíncrona)
       return pokemonFormResponseLD
    }

    override fun downloadPokemonCatchedData(name: String): LiveData<PokemonExtendedInfoResponse> {
        var call : Call<PokemonExtendedInfoResponse> = apiServices.getPokemonExtendedInfo(name)

        var pokemonExtendedInfoResponseLD: MutableLiveData<PokemonExtendedInfoResponse> = MutableLiveData()

        call.enqueue(object : Callback<PokemonExtendedInfoResponse>{

            override fun onResponse(call: Call<PokemonExtendedInfoResponse>, response: Response<PokemonExtendedInfoResponse>?){
                response?.let {
                    pokemonExtendedInfoResponseLD.postValue(response.body())

                } ?: pokemonExtendedInfoResponseLD.postValue(null)

            }
            override fun onFailure(call: Call<PokemonExtendedInfoResponse>, t: Throwable) {
                pokemonExtendedInfoResponseLD.postValue(null)
            }
        })

        return pokemonExtendedInfoResponseLD
    }
}