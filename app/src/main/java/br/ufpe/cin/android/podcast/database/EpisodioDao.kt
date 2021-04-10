package br.ufpe.cin.android.podcast.database

import androidx.lifecycle.LiveData
import androidx.room.*
import br.ufpe.cin.android.podcast.data.Episodio

@Dao
interface EpisodioDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(episodio: Episodio)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(episodio: Episodio)

    @Delete
    suspend fun delete(episodio: Episodio)

    @Query("SELECT * FROM episodios")
    fun allEpisodios(): LiveData<List<Episodio>>

    @Query("SELECT * FROM episodios WHERE titulo LIKE :title")
    suspend fun searchByTitulo(title: String): Episodio?
}