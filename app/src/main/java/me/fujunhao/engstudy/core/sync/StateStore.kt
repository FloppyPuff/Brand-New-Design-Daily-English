package me.fujunhao.engstudy.core.sync

import me.fujunhao.engstudy.core.model.AppState

interface StateStore {
  suspend fun snapshot(): AppState
  suspend fun replaceAll(state: AppState)
}
