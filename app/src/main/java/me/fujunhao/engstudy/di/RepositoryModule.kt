package me.fujunhao.engstudy.di

import me.fujunhao.engstudy.core.sync.StateMapper
import me.fujunhao.engstudy.core.sync.StateStore
import me.fujunhao.engstudy.data.remote.RemoteDataSource
import me.fujunhao.engstudy.data.remote.SyncApi
import me.fujunhao.engstudy.data.repository.AuthRepository
import me.fujunhao.engstudy.data.repository.SessionStateProvider
import me.fujunhao.engstudy.data.repository.SyncManager
import me.fujunhao.engstudy.data.repository.SyncScheduler
import me.fujunhao.engstudy.data.repository.SyncTrigger
import me.fujunhao.engstudy.data.repository.WorkManagerSyncScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
  @Binds
  @Singleton
  abstract fun bindSyncTrigger(impl: SyncManager): SyncTrigger

  @Binds
  @Singleton
  abstract fun bindSyncApi(impl: RemoteDataSource): SyncApi

  @Binds
  @Singleton
  abstract fun bindStateStore(impl: StateMapper): StateStore

  @Binds
  @Singleton
  abstract fun bindSyncScheduler(impl: WorkManagerSyncScheduler): SyncScheduler

  @Binds
  @Singleton
  abstract fun bindSessionState(impl: AuthRepository): SessionStateProvider
}
