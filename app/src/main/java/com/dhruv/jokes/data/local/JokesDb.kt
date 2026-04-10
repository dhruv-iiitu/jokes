package com.dhruv.jokes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [JokesEntity::class], version = 1, exportSchema = false)
abstract class JokesDb: RoomDatabase(){
    abstract val dao: JokesDao
}