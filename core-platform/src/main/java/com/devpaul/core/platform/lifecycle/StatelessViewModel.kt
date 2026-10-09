package com.devpaul.core.platform.lifecycle

import androidx.lifecycle.ViewModel
import com.devpaul.core.platform.lifecycle.base.UiEventHolder
import com.devpaul.core.platform.lifecycle.base.UiIntentHolder
import com.devpaul.core.platform.lifecycle.base.ViewModelLoadable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

abstract class StatelessViewModel<UiIntent, UiEvent> : ViewModel(),
    UiIntentHolder<UiIntent>,
    UiEventHolder<UiEvent>,
    ViewModelLoadable {

    private val uiIntentChannel = Channel<UiIntent>(Channel.BUFFERED)
    private val _uiEvents = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    private val _isLoading = MutableStateFlow(false)

    final override val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()
    final override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        launch {
            uiIntentChannel.receiveAsFlow().collect(::onUiIntent)
        }
    }

    final override fun executeUiIntent(intent: UiIntent) {
        uiIntentChannel.trySend(intent)
    }

    protected fun emitUiEvent(event: UiEvent) {
        _uiEvents.tryEmit(event)
    }

    protected fun setLoading(value: Boolean) {
        _isLoading.value = value
    }

    protected fun launchIO(block: suspend () -> Unit) {
        launch {
            withContext(Dispatchers.IO) { block() }
        }
    }
}
