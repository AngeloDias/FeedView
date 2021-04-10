package br.ufpe.cin.android.podcast.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import br.ufpe.cin.android.podcast.data.Episodio

class EpisodioRepository(private val dao: EpisodioDao) {

    @WorkerThread
    suspend fun insert(episodio: Episodio) {
        dao.insert(episodio)
    }

    @WorkerThread
    suspend fun update(episodio: Episodio) {
        dao.update(episodio)
    }

    @WorkerThread
    suspend fun delete(episodio: Episodio) {
        dao.delete(episodio)
    }

    @WorkerThread
    fun allEpisodios(): LiveData<List<Episodio>> {
        return dao.allEpisodios()
    }

    @WorkerThread
    suspend fun searchByTitulo(titulo: String): Episodio? {
        return dao.searchByTitulo(titulo)
    }
}