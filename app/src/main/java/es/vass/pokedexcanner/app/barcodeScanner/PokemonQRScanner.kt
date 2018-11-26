package es.vass.pokedexcanner.app.barcodeScanner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import es.vass.pokedexcanner.pokemonList.list.PokemonListActivity
import me.dm7.barcodescanner.zxing.ZXingScannerView

class PokemonQRScanner: AppCompatActivity() , ZXingScannerView.ResultHandler{

    lateinit var scannerView: ZXingScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = ZXingScannerView(this)
        scannerView.setFormats(listOf(BarcodeFormat.QR_CODE))
        setContentView(scannerView)
    }

    override fun onResume() {
        super.onResume()
        scannerView.setResultHandler(this)
        scannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }


    override fun handleResult(rawResult: Result?) {
        scannerView.resumeCameraPreview(this)

        val resultIntent = Intent()

        resultIntent.putExtra(PokemonListActivity.POKEMON_ID, rawResult?.text)

        setResult(Activity.RESULT_OK, resultIntent)
        finish()


    }
}