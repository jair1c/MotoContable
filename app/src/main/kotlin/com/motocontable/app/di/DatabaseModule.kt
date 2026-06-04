package com.motocontable.app.di

import android.content.Context
import androidx.room.Room
import com.motocontable.app.data.dao.ConfiguracionDao
import com.motocontable.app.data.dao.ExtraDao
import com.motocontable.app.data.dao.RegistroDao
import com.motocontable.app.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "motocontable.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideRegistroDao(db: AppDatabase): RegistroDao = db.registroDao()
    @Provides fun provideExtraDao(db: AppDatabase): ExtraDao = db.extraDao()
    @Provides fun provideConfiguracionDao(db: AppDatabase): ConfiguracionDao = db.configuracionDao()
}
