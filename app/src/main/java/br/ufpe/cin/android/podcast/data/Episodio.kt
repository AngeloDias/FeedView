package br.ufpe.cin.android.podcast.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "episodios")
data class Episodio(
    @PrimaryKey
    val linkEpisodio: String,
    val titulo: String,
    val descricao: String,
    val linkArquivo: String,
    val dataPublicacao: String
) {
    override fun toString(): String {
        return "$titulo ($dataPublicacao) => $descricao"
    }
}