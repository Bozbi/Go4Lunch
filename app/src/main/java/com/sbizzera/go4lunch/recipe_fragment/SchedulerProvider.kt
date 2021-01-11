package com.sbizzera.go4lunch.recipe_fragment

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.schedulers.TestScheduler


interface BaseSchedulerProvider {
    fun io(): Scheduler
    fun mainThread(): Scheduler
}

class SchedulerProvider : BaseSchedulerProvider {
    override fun io(): Scheduler = Schedulers.io()
    override fun mainThread(): Scheduler = AndroidSchedulers.mainThread()
}

class TrampolineSchedulerProvider : BaseSchedulerProvider {
    override fun io(): Scheduler = Schedulers.trampoline()
    override fun mainThread(): Scheduler = Schedulers.trampoline()
}

class TestSchedulerProvider(private val testScheduler: TestScheduler):BaseSchedulerProvider{
    override fun io(): Scheduler = testScheduler
    override fun mainThread(): Scheduler = testScheduler
}