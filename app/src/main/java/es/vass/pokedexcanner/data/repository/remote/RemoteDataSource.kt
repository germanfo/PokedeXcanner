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
            .callbackExecutor(AppExecutors.networkIO)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()

        apiServices = retrofit.create(PokemonApi::class.java)
    }


    override fun downloadPokemonViewedData(id: Long): LiveData<PokemonFormResponse> {
       var call : Call<PokemonFormResponse> = apiServices.getPokemonInfo(id)

        var pokemonFormResponseLD: MutableLiveData<PokemonFormResponse> = MutableLiveData()

        call.enqueue(object : Callback<PokemonFormResponse>{

            override fun onResponse(call: Call<PokemonFormResponse>, response: Response<PokemonFormResponse>?){
                response?.let {
                    pokemonFormResponseLD.postValue(response.body())

                } ?: pokemonFormResponseLD.postValue(null)

            }
            override fun onFailure(call: Call<PokemonFormResponse>, t: Throwable) {
                pokemonFormResponseLD.postValue(null)
            }
        })

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