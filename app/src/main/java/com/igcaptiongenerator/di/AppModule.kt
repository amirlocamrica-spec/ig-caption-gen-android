package com.igcaptiongenerator.di

import android.content.Context
import androidx.room.Room
import com.igcaptiongenerator.data.local.AppDatabase
import com.igcaptiongenerator.data.local.CaptionDao
import com.igcaptiongenerator.data.remote.CaptionApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideCaptionApi(client: OkHttpClient): CaptionApi = Retrofit.Builder()
        .baseUrl("https://ig-caption-gen-production.up.railway.app/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CaptionApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "ig_caption_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideCaptionDao(db: AppDatabase): CaptionDao = db.captionDao()
}
