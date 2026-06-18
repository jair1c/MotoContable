package com.motocontable.app.di

import android.content.Context
import androidx.room.Room
import com.motocontable.app.data.dao.*
import com.motocontable.app.data.db.AppDatabase
import com.motocontable.app.data.db.MIGRATION_1_2
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
            .addMigrations(MIGRATION_1_2)          // preserva datos existentes
            .build()

    @Provides fun provideRegistroDao(db: AppDatabase): RegistroDao       = db.registroDao()
    @Provides fun provideExtraDao(db: AppDatabase): ExtraDao             = db.extraDao()
    @Provides fun provideConfiguracionDao(db: AppDatabase): ConfiguracionDao = db.configuracionDao()
    @Provides fun providePagoSemanaDao(db: AppDatabase): PagoSemanaDao   = db.pagoSemanaDao()
    @Provides fun provideGastoSemanaDao(db: AppDatabase): GastoSemanaDao = db.gastoSemanaDao()
}
