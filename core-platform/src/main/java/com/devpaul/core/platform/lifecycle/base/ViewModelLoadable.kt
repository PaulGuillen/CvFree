package com.devpaul.core.platform.lifecycle.base

import kotlinx.coroutines.flow.StateFlow

interface ViewModelLoadable {
    val isLoading: StateFlow<Boolean>
}
