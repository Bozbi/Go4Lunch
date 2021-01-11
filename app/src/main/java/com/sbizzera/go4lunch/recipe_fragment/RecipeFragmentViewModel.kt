package com.sbizzera.go4lunch.recipe_fragment

import android.os.Build
import android.text.Html
import androidx.lifecycle.ViewModel

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_recipe.*

import java.util.concurrent.TimeUnit

class RecipeFragmentViewModel(
        private val recipeRepo: RecipeRepo,
        private val schedulerProvider: BaseSchedulerProvider
) : ViewModel(

) {

    val viewState: BehaviorSubject<RecipeFragmentUiState> = BehaviorSubject.create()
    val viewEvent: BehaviorSubject<Event<RecipeFragmentViewEvent>> = BehaviorSubject.create()
    private val compositeDisposable = CompositeDisposable()

    init {
        getRandomRecipe()
    }

    fun getRandomRecipe() {
        if (viewState.value != null) {
            val uiStateOnLoading = viewState.value.copy(isLoadingRecipe = true)
            viewState.onNext(uiStateOnLoading)
        }
        val disposable = recipeRepo.getRecipe()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.mainThread())
                .delay(1, TimeUnit.SECONDS,schedulerProvider.io())
                .doOnError { err ->
                    viewEvent.onNext(Event(RecipeFragmentViewEvent.LoadingError(err)))
                }
                .onErrorComplete()
                .timeout(7000, TimeUnit.MILLISECONDS)
                .map {apiResponse -> mapResponsetoRecipeFragUiState(apiResponse) }
                .subscribe(
                        { recipeApiResponse ->
                            viewState.onNext(recipeApiResponse)
                        },

                        { err ->
                            viewEvent.onNext(Event(RecipeFragmentViewEvent.LoadingError(err)))
                        }
                )
        compositeDisposable.add(disposable)
    }

    private fun mapResponsetoRecipeFragUiState(apiResponse: RecipeApiResponse): RecipeFragmentUiState {
        val recipe = apiResponse.recipes[0]
        val recipeInstructions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(recipe.instructions, Html.FROM_HTML_MODE_LEGACY)
        } else
                    recipe.instructions

        with(recipe) {
            return RecipeFragmentUiState(false, title, recipeInstructions.toString(), imageUrl, "Temps de préparation : $cookTime minutes")
        }
    }


    override fun onCleared() {
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }

    sealed class RecipeFragmentViewEvent {
        class LoadingError(val err: Throwable) : RecipeFragmentViewEvent()
    }

}

data class RecipeFragmentUiState(
        val isLoadingRecipe: Boolean,
        val recipeTitle: String,
        val instructions: String,
        val imageUrl: String,
        val coockingTime: String,
)




