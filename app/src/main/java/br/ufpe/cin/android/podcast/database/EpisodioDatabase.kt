package br.ufpe.cin.android.podcast.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.ufpe.cin.android.podcast.data.Episodio

@Database(entities = [Episodio::class], version = 1)
abstract class EpisodioDatabase: RoomDatabase() {
    abstract fun dao(): EpisodioDao

    companion object {

        @Volatile
        private var INSTANCE: EpisodioDatabase? = null

        fun getInstance(context: Context): EpisodioDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        EpisodioDatabase::class.java,
                        "episodio_history_database"
                    ).fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}