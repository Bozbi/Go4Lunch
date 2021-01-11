package com.sbizzera.go4lunch.recipe_fragment


import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.TestScheduler
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doReturn

import java.util.concurrent.TimeUnit
import kotlin.RuntimeException


class RecipeFragmentViewModelTest {

    private lateinit var viewModel: RecipeFragmentViewModel
    private lateinit var recipeRepo: RecipeRepo

    private val testScheduler = TestScheduler()

    @Before
    fun setUp() {

        recipeRepo = Mockito.mock(RecipeRepo::class.java)


        val apiFirstResponse =Observable.just(
                RecipeApiResponse(
                        listOf(Recipe("mockTitle#1", "mockInstructions#1", "mockUrl#1", 1))))
        val apiSecondResponse =Observable.just(
                RecipeApiResponse(
                        listOf(Recipe("mockTitle#2", "mockInstructions#2", "mockUrl#2", 2))))
        val apiErrorResponse = Observable.error<Any>(RuntimeException())

        doReturn(apiFirstResponse,apiSecondResponse,apiErrorResponse).`when`(recipeRepo).getRecipe()





        viewModel = RecipeFragmentViewModel(
                recipeRepo,
//                TrampolineSchedulerProvider()
                TestSchedulerProvider(testScheduler)
        )

    }

    @Test
    fun`should get first recipe on launch`(){
        testScheduler.advanceTimeBy(1000,TimeUnit.MILLISECONDS)
        assertEquals(viewModel.viewState.value.recipeTitle,"mockTitle#1")
    }

    @Test
    fun `should get second recipe on refresh click`(){
        viewModel.getRandomRecipe()
        testScheduler.advanceTimeBy(1000,TimeUnit.MILLISECONDS)
        assertEquals(viewModel.viewState.value.recipeTitle,"mockTitle#2")
    }

    @Test
    fun `recipe should be fetch after min delay`(){
        assertNull(viewModel.viewState.value)
        testScheduler.advanceTimeBy(1000,TimeUnit.MILLISECONDS)
        assertEquals(viewModel.viewState.value.recipeTitle,"mockTitle#1")
    }

    @Test
    fun `view state should map correctly api response`(){
        testScheduler.advanceTimeBy(1000,TimeUnit.MILLISECONDS)
        assertEquals(viewModel.viewState.value.recipeTitle,"mockTitle#1")
        assertEquals(viewModel.viewState.value.imageUrl,"mockUrl#1")
        assertFalse(viewModel.viewState.value.isLoadingRecipe)
        assertEquals(viewModel.viewState.value.coockingTime,"Temps de préparation : 1 minutes")
        assertEquals(viewModel.viewState.value.instructions,"mockInstructions#1")
    }

    @Test
    fun `view event should trigger error on fetch error`(){
        viewModel.getRandomRecipe()
        viewModel.getRandomRecipe()
        testScheduler.advanceTimeBy(1000,TimeUnit.MILLISECONDS)
        assertTrue(viewModel.viewEvent.value.getContentIfNotHandled() is RecipeFragmentViewModel.RecipeFragmentViewEvent.LoadingError)
    }

    @Test
    fun `view state should show loader on fetch start and then disappear`(){
        testScheduler.advanceTimeBy(1000,TimeUnit.MILLISECONDS)
        viewModel.getRandomRecipe()
        assertTrue(viewModel.viewState.value.isLoadingRecipe)
        testScheduler.advanceTimeBy(1000,TimeUnit.MILLISECONDS)
        assertFalse(viewModel.viewState.value.isLoadingRecipe)
    }



}


