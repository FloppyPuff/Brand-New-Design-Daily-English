package me.fujunhao.engstudy.di

import me.fujunhao.engstudy.data.local.DataStorePreferenceStore
import me.fujunhao.engstudy.data.local.EncryptedSessionStore
import me.fujunhao.engstudy.data.local.PreferenceStore
import me.fujunhao.engstudy.data.local.SessionStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SessionModule {
  @Binds
  @Singleton
  abstract fun bindSessionStore(impl: EncryptedSessionStore): SessionStore

  @Binds
  @Singleton
  abstract fun bindPreferenceStore(impl: DataStorePreferenceStore): PreferenceStore
}
