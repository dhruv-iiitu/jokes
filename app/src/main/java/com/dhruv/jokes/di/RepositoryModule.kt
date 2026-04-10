package com.dhruv.jokes.di

import com.dhruv.jokes.repos.JokesRepo
import com.dhruv.jokes.repos.JokesRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindJokesRepository(
        jokesRepositoryImpl: JokesRepoImpl
    ): JokesRepo
}