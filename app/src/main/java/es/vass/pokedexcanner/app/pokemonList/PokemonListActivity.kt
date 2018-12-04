package es.vass.pokedexcanner.pokemonList.list

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import es.vass.pokedexcanner.Injector
import es.vass.pokedexcanner.R
import es.vass.pokedexcanner.app.alert
import es.vass.pokedexcanner.app.barcodeScanner.PokemonQRScanner
import es.vass.pokedexcanner.app.pokemonList.PokemonListAdapter
import es.vass.pokedexcanner.app.pokemonList.PokemonListViewModel
import es.vass.pokedexcanner.pokemonList.pokemonDetail.PokemonDetailActivity
import kotlinx.android.synthetic.main.activity_pokemon_list.*
import kotlinx.android.synthetic.main.pokemon_list.*

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [PokemonDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class PokemonListActivity : AppCompatActivity() {

//region constantes
    companion object {

        const val POKEMON_ID = "pokemon_id"
        const val SCANNER_REQUEST_CODE = 99
        const val PERMISSION_REQUEST_CODE = 22
    }
//endregion

    //Indicador de tablet
    private var twoPane: Boolean = false

    //ViewModel
    private lateinit var pokemonListViewModel: PokemonListViewModel

    //Adapter del RecyclerView
    private lateinit var rvAdapter: PokemonListAdapter

//region LifeCycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        //Es tablet si se encuentra el detail_container en el layout
        if (pokemon_detail_container != null) {
            twoPane = true
        }

        setupEvents()
        setupViewModel()
        setupRecyclerView(pokemon_list)
        observeViewModel()
    }

//endregion

//region Setups y observers
    fun setupEvents(){
        fab.setOnClickListener {

            //Solicitud de permisos para cámara
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),PERMISSION_REQUEST_CODE)
            }
            else
                launchQRPokemonScanner()

        }

        fab.setOnLongClickListener {
            for (i in 1..151)
                pokemonListViewModel.viewPokemon(i.toLong())
            true
        }
    }

    fun setupViewModel() {
        pokemonListViewModel = ViewModelProviders.of(this, Injector.providePokemonListViewModelFactory(this))
            .get(PokemonListViewModel::class.java)
    }


    private fun setupRecyclerView(recyclerView: RecyclerView) {
        rvAdapter = PokemonListAdapter(this, twoPane)
        recyclerView.adapter = rvAdapter
    }

    fun observeViewModel() {
        //Se observa la lista de pokemons del VM y se notifica al adapter del recyclerview cuando ésta cambie
        pokemonListViewModel.pokemonList.observe(this, Observer { pokemonList ->
            pokemonList?.let {
                rvAdapter.pokemonList = pokemonList
                rvAdapter.notifyDataSetChanged()
            }
        })
    }


//endregion

//region permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            PERMISSION_REQUEST_CODE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) launchQRPokemonScanner()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCANNER_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            var scannedText: String? = data?.getStringExtra(POKEMON_ID)

            var pokemonId : Long? = try {

                scannedText?.toLong()
            }catch (ex: NumberFormatException){
                null
            }

            //Solicitud al VM para visualizar los datos de un pokemon
            pokemonId?.let {
                pokemonListViewModel.viewPokemon(pokemonId)
            }?:alert("Eso no es un pokemon, ponte gafas")
        }
    }
//endregion



    fun launchQRPokemonScanner(){
        var scannerIntent = Intent(this, PokemonQRScanner::class.java)
        startActivityForResult(scannerIntent, SCANNER_REQUEST_CODE)
    }


    fun setSelectedPokemonId(id: Long?) {
        pokemonListViewModel.setSelectedPokemonId(id)
    }



}
