package me.fujunhao.engstudy.di

import android.content.Context
import androidx.room.Room
import me.fujunhao.engstudy.data.local.DailyEnglishDatabase
import me.fujunhao.engstudy.data.local.ProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
  @Provides
  @Singleton
  fun provideDatabase(@ApplicationContext context: Context): DailyEnglishDatabase =
    Room.databaseBuilder(context, DailyEnglishDatabase::class.java, "daily-english-native.db").build()

  @Provides
  fun provideProgressDao(database: DailyEnglishDatabase): ProgressDao = database.progressDao()
}
