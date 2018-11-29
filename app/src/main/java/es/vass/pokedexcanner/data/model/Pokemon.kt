package es.vass.pokedexcanner.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "listaPokemon")
data class Pokemon (@PrimaryKey var id: Long?,
                    @ColumnInfo(name = "name") var nombre: String,
                    @ColumnInfo(name = "image") var imagen: String?,
                    @ColumnInfo(name = "height") var altura: Float?,
                    @ColumnInfo(name = "weight") var peso: Float?
){
    fun idFilledWithZero(): String {
       return String.format("%03d", id)
    }

    constructor():this(null,"?",null,null,null)
}