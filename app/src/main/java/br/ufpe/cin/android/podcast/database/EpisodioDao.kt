package br.ufpe.cin.android.podcast.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import br.ufpe.cin.android.podcast.data.Episodio

@Dao
interface EpisodioDao {

    @Insert
    fun insert(episodio: Episodio)

    @Delete
    fun delete(episodio: Episodio)

    @Query("SELECT * FROM episodios")
    fun allEpisodios(): LiveData<List<Episodio>>

    @Query("SELECT * FROM episodios WHERE titulo LIKE :title")
    fun searchByTitulo(title: String): Episodio?
}