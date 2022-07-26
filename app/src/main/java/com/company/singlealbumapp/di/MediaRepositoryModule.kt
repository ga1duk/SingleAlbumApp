package com.company.singlealbumapp.di

import com.company.singlealbumapp.repository.MediaRepository
import com.company.singlealbumapp.repository.MediaRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface MediaRepositoryModule {
    @Binds
    @Singleton
    fun bindsMediaRepositoryImpl(mediaRepositoryImpl: MediaRepositoryImpl): MediaRepository
}