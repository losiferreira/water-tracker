package com.losiferreira.watertracker.presentation.common

import androidx.compose.runtime.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

@Composable
fun <T : Any> Observable<T>.collectAsState(initial: T): State<T> {
    val state = remember { mutableStateOf(initial) }
    
    DisposableEffect(this) {
        val disposable = this@collectAsState.subscribe { value ->
            state.value = value
        }
        
        onDispose {
            disposable.dispose()
        }
    }
    
    return state
}