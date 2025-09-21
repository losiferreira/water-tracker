package com.losiferreira.watertracker.presentation.history

import androidx.lifecycle.ViewModel
import com.losiferreira.watertracker.domain.usecase.GetHistoryUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class HistoryViewModel(
    private val getHistoryUseCase: GetHistoryUseCase
) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val uiStateSubject = BehaviorSubject.createDefault(HistoryUiState())
    private val uiEventSubject = PublishSubject.create<HistoryUiEvent>()

    val uiState: Observable<HistoryUiState> = uiStateSubject.distinctUntilChanged()

    init {
        observeUiEvents()
        loadAllEntries()
    }


    private fun observeUiEvents() {
        // UI event handling is now done in loadAllEntries to ensure fresh subscription
    }

    private fun loadAllEntries() {
        val currentState = uiStateSubject.value ?: HistoryUiState()
        uiStateSubject.onNext(currentState.copy(isLoading = true))

        // Clear existing subscriptions
        disposables.clear()
        
        // Use reactive observable instead of single
        val disposable = getHistoryUseCase.observeAllEntries()
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { entries ->
                    val availableMonths = entries
                        .map { YearMonth.from(it.date) }
                        .distinct()
                        .sortedDescending()

                    val newState = uiStateSubject.value ?: HistoryUiState()
                    uiStateSubject.onNext(
                        newState.copy(
                            entries = entries,
                            availableMonths = availableMonths,
                            isLoading = false,
                            error = null,
                            selectedMonth = null
                        )
                    )
                },
                { error ->
                    val errorState = uiStateSubject.value ?: HistoryUiState()
                    uiStateSubject.onNext(
                        errorState.copy(
                            isLoading = false,
                            error = error.message
                        )
                    )
                }
            )
        disposables.add(disposable)
        
        // Setup UI event handling
        setupUiEventHandling()
    }

    private fun filterByMonth(yearMonth: YearMonth) {
        val currentState = uiStateSubject.value ?: HistoryUiState()
        uiStateSubject.onNext(currentState.copy(isLoading = true))

        val yearMonthString = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"))

        val disposable = getHistoryUseCase.observeByMonth(yearMonthString)
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { entries ->
                    uiStateSubject.onNext(
                        currentState.copy(
                            entries = entries,
                            selectedMonth = yearMonth,
                            isLoading = false,
                            error = null
                        )
                    )
                },
                { error ->
                    uiStateSubject.onNext(
                        currentState.copy(
                            isLoading = false,
                            error = error.message
                        )
                    )
                }
            )
        disposables.add(disposable)
    }

    private fun setupUiEventHandling() {
        val uiEventDisposable = uiEventSubject
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { event ->
                when (event) {
                    is HistoryUiEvent.LoadAllEntries -> loadAllEntries()
                    is HistoryUiEvent.FilterByMonth -> filterByMonth(event.yearMonth)
                }
            }
        disposables.add(uiEventDisposable)
    }

    fun onEvent(event: HistoryUiEvent) {
        uiEventSubject.onNext(event)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}