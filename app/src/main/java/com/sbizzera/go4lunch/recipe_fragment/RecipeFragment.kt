package com.sbizzera.go4lunch.recipe_fragment


import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.sbizzera.go4lunch.R
import com.sbizzera.go4lunch.utils.ViewModelFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recipe.*

class RecipeFragment : Fragment() {

    companion object {
        fun newInstance() = RecipeFragment()
    }

    private lateinit var recipeViewModel: RecipeFragmentViewModel
    private var compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.title = "Eat Home"
        return inflater.inflate(R.layout.fragment_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recipeViewModel = ViewModelProvider(this, ViewModelFactory.getInstance()).get(RecipeFragmentViewModel::class.java)
        recipeRefreshFab.setOnClickListener {
            refreshRecipe()
        }
        initViewState()
        initViewEvent()
    }

    private fun initViewState() {
        val disposable = recipeViewModel.viewState.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { println("error") }
                .subscribe { viewState ->
                    renderNewState(viewState)
                }
        compositeDisposable.add(disposable)
    }

    private fun initViewEvent(){
        val disposable = recipeViewModel.viewEvent.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError{ println("error")}
                .subscribe{viewEvent->
                    when(val event = viewEvent.getContentIfNotHandled()){
                        is RecipeFragmentViewModel.RecipeFragmentViewEvent.LoadingError ->{
                            Toast.makeText(context,event.err.toString(),Toast.LENGTH_LONG).show()
                        }
                    }
                    println("has content been handled: ${viewEvent.hasBeenHandled}")
                }
        compositeDisposable.add(disposable)
    }

    private fun renderNewState(viewState: RecipeFragmentUiState) {
        with(viewState) {
            blur.isVisible = isLoadingRecipe
            recipeLoaderIndicator.isVisible = isLoadingRecipe
            recipeTitleTxt.text = recipeTitle
            recipeCookTimeTxt.text = coockingTime
            recipeInstructionsText.text = instructions
            Glide.with(recipePhotoImg).load(imageUrl).placeholder(R.drawable.restaurant_photo_placeholder).into(recipePhotoImg)
        }
    }

    private fun refreshRecipe() {
        recipeViewModel.getRandomRecipe()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }

}

