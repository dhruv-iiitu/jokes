package com.dhruv.jokes.di

import android.content.Context
import androidx.room.Room
import com.dhruv.jokes.data.local.JokesDao
import com.dhruv.jokes.data.local.JokesDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun getDatabase(@ApplicationContext context: Context): JokesDb {
        return Room.databaseBuilder(
            context,
            JokesDb::class.java,
            "my-database"
        ).build()
    }

    @Provides
    fun getDao(jokesDb :JokesDb): JokesDao {
        return jokesDb.dao
    }
}

