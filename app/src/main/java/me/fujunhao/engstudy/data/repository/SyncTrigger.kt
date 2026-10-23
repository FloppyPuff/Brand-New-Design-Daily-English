package me.fujunhao.engstudy.data.repository

import me.fujunhao.engstudy.core.model.AppState
import kotlinx.coroutines.flow.StateFlow

interface SyncTrigger {
  fun markDirty()
  val remoteState: StateFlow<AppState?>
}
