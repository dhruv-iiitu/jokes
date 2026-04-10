package com.dhruv.jokes.di

import com.dhruv.jokes.data.remote.ApiService
import com.dhruv.jokes.utils.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule{
    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit{
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create() )
            .build()
    }

    @Provides
    fun providesHttpClient(interceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    fun providesLoggingInterceptor(): Interceptor{
        return HttpLoggingInterceptor().apply{
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    fun providesJokesApi(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}