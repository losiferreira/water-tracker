package com.losiferreira.watertracker.presentation.tracker

import androidx.lifecycle.ViewModel
import com.losiferreira.watertracker.domain.model.DailyGoal
import com.losiferreira.watertracker.domain.usecase.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class TrackerViewModel(
    private val getTodayWaterEntryUseCase: GetTodayWaterEntryUseCase,
    private val addWaterUseCase: AddWaterUseCase,
    private val removeWaterUseCase: RemoveWaterUseCase,
    private val dailyRolloverUseCase: DailyRolloverUseCase,
    private val dailyGoal: DailyGoal
) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val uiStateSubject = BehaviorSubject.createDefault(TrackerUiState())
    private val uiEventSubject = PublishSubject.create<TrackerUiEvent>()
    private val saveToHistorySubject = PublishSubject.create<Unit>()

    val uiState: Observable<TrackerUiState> = uiStateSubject.distinctUntilChanged()

    init {
        observeWaterEntry()
        observeUiEvents()
        observeSaveToHistory()
        performDailyRollover()
    }

    private fun observeWaterEntry() {
        val disposable = getTodayWaterEntryUseCase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { waterEntry ->
                val currentState = uiStateSubject.value ?: TrackerUiState()
                val progress = dailyGoal.getProgressPercentage(waterEntry.milliliters)
                val remaining = dailyGoal.getRemainingMilliliters(waterEntry.milliliters)
                
                uiStateSubject.onNext(
                    currentState.copy(
                        currentMilliliters = waterEntry.milliliters,
                        progressPercentage = progress,
                        remainingMilliliters = remaining,
                        isLoading = false
                    )
                )
            }
        disposables.add(disposable)
    }

    private fun observeUiEvents() {
        val disposable = uiEventSubject
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { event ->
                when (event) {
                    is TrackerUiEvent.AddWater -> handleAddWater(event.milliliters)
                    is TrackerUiEvent.RemoveWater -> handleRemoveWater(event.milliliters)
                    is TrackerUiEvent.AddCustomWater -> handleAddWater(event.milliliters)
                    is TrackerUiEvent.UpdateGoal -> handleUpdateGoal(event.goalMilliliters)
                }
            }
        disposables.add(disposable)
    }

    private fun handleAddWater(milliliters: Int) {
        val disposable = addWaterUseCase(milliliters)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { 
                    // Trigger save to history with debounce
                    saveToHistorySubject.onNext(Unit)
                },
                { error ->
                    val currentState = uiStateSubject.value ?: TrackerUiState()
                    uiStateSubject.onNext(
                        currentState.copy(error = error.message)
                    )
                }
            )
        disposables.add(disposable)
    }

    private fun handleRemoveWater(milliliters: Int) {
        val disposable = removeWaterUseCase(milliliters)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { 
                    // Trigger save to history with debounce
                    saveToHistorySubject.onNext(Unit)
                },
                { error ->
                    val currentState = uiStateSubject.value ?: TrackerUiState()
                    uiStateSubject.onNext(
                        currentState.copy(error = error.message)
                    )
                }
            )
        disposables.add(disposable)
    }

    private fun performDailyRollover() {
        val disposable = dailyRolloverUseCase.checkAndPerformRollover()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { },
                { }
            )
        disposables.add(disposable)
    }

    private fun observeSaveToHistory() {
        // Debounce saves to history by 1 second
        val disposable = saveToHistorySubject
            .debounce(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                // The actual save happens automatically through Room observables
                // This debounce just prevents excessive database writes
            }
        disposables.add(disposable)
    }

    private fun handleUpdateGoal(goalMilliliters: Int) {
        val currentState = uiStateSubject.value ?: TrackerUiState()
        val newGoal = DailyGoal(goalMilliliters)
        val progress = newGoal.getProgressPercentage(currentState.currentMilliliters)
        val remaining = newGoal.getRemainingMilliliters(currentState.currentMilliliters)
        
        uiStateSubject.onNext(
            currentState.copy(
                dailyGoal = newGoal,
                progressPercentage = progress,
                remainingMilliliters = remaining
            )
        )
    }

    fun onEvent(event: TrackerUiEvent) {
        uiEventSubject.onNext(event)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}