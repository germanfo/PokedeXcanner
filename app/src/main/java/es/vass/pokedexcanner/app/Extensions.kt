package es.vass.pokedexcanner.app

import android.content.Context
import android.support.v7.app.AlertDialog
import es.vass.pokedexcanner.R

//Extensión de Context.alert para crear un AlertDialog sencillo
fun Context.alert (text: String){
    AlertDialog.Builder(this).setMessage(text).setPositiveButton(R.string.ok, null).create().show()
}