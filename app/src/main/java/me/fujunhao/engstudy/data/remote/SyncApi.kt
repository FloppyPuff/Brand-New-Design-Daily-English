package me.fujunhao.engstudy.data.remote

import me.fujunhao.engstudy.core.model.AppState

interface SyncApi {
  suspend fun me(): ApiResult<UserResponse>
  suspend fun getState(): ApiResult<StateResponse>
  suspend fun putState(revision: Long, payload: AppState): ApiResult<RevisionResponse>
}
