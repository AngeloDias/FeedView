package br.ufpe.cin.android.podcast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.ufpe.cin.android.podcast.data.Episodio
import br.ufpe.cin.android.podcast.database.EpisodioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class EpisodioViewModel(val repo: EpisodioRepository): ViewModel() {
    val episodios = repo.allEpisodios()

    fun insert(episodio: Episodio) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insert(episodio)
        }
    }

    fun update(episodio: Episodio) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.update(episodio)
        }
    }

    fun delete(episodio: Episodio) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.delete(episodio)
        }
    }

    fun searchByTitulo(titulo: String): Episodio? {
        var episodio: Episodio? = null

        viewModelScope.launch(Dispatchers.IO) {
            episodio = repo.searchByTitulo(titulo)
        }

        return episodio
    }

}

class EpisodioViewModelFactory(private val repo: EpisodioRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if(modelClass.isAssignableFrom(EpisodioViewModel::class.java)) {
            return EpisodioViewModel(repo) as T
        }

        throw IllegalArgumentException("ViewModel not found!")
    }

}