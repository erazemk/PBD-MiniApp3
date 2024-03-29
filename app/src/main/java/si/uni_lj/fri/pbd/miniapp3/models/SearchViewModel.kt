package si.uni_lj.fri.pbd.miniapp3.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import si.uni_lj.fri.pbd.miniapp3.models.dto.IngredientsDTO
import si.uni_lj.fri.pbd.miniapp3.models.dto.RecipesByIngredientDTO
import si.uni_lj.fri.pbd.miniapp3.repository.RecipeRepository

class SearchViewModel(application: Application): AndroidViewModel(application) {

    val allIngredients: MutableLiveData<IngredientsDTO>
    val searchResults: MutableLiveData<RecipesByIngredientDTO>
    private val repository: RecipeRepository = RecipeRepository(application)

    init {
        allIngredients = repository.searchIngredients
        searchResults = repository.searchRecipes
    }

    fun getRecipesByIngredient(ingredient: String) {
        repository.findRecipesByIngredient(ingredient)
    }

    fun getIngredients() {
        repository.getIngredients()
    }

}
