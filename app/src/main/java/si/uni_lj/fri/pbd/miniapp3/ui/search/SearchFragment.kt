package si.uni_lj.fri.pbd.miniapp3.ui.search

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import si.uni_lj.fri.pbd.miniapp3.R
import si.uni_lj.fri.pbd.miniapp3.adapter.RecyclerViewAdapter
import si.uni_lj.fri.pbd.miniapp3.adapter.SpinnerAdapter
import si.uni_lj.fri.pbd.miniapp3.databinding.FragmentSearchBinding
import si.uni_lj.fri.pbd.miniapp3.models.SearchViewModel
import si.uni_lj.fri.pbd.miniapp3.models.dto.IngredientsDTO
import timber.log.Timber

class SearchFragment: Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var sViewModel: SearchViewModel? = null
    private var spinnerAdapter: SpinnerAdapter? = null
    private var recyclerAdapter: RecyclerViewAdapter? = null
    private var progressBar: MaterialProgressBar? = null

    private var selectedIngredient: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = binding.progressBar
        progressBar?.visibility = View.VISIBLE

        sViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        sViewModel?.getIngredients()

        observerSetup()
        recyclerSetup()

        binding.swipeRefreshLayout.setOnRefreshListener {
            progressBar?.visibility = View.VISIBLE
            sViewModel?.getRecipesByIngredient(spinnerAdapter?.getItem(selectedIngredient) as String)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        if (!isNetworkAvailable(context)) {
            progressBar?.visibility = View.INVISIBLE
            binding.noInternetText.visibility = View.VISIBLE
        }
    }

    // Source: https://stackoverflow.com/questions/51141970/check-internet-connectivity-android-in-kotlin
    private fun isNetworkAvailable(context: Context?): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo: NetworkInfo? = cm.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

    private fun observerSetup() {
        // Observer for ingredients - spinner
        sViewModel?.allIngredients?.observe(viewLifecycleOwner) { ingredients ->
            Timber.d("Updated list of ingredients")
            spinnerSetup(ingredients)
        }

        // Observer for drinks - recyclerView
        sViewModel?.searchResults?.observe(viewLifecycleOwner) { recipes ->
            Timber.d("Updated list of drinks")
            recyclerAdapter?.setRecipesFromDto(recipes.drinks)
            recyclerAdapter?.setCaller("SearchFragment")
            progressBar?.visibility = View.INVISIBLE
        }
    }

    private fun recyclerSetup() {
        recyclerAdapter = RecyclerViewAdapter(context)

        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = recyclerAdapter
    }

    // Source: https://www.geeksforgeeks.org/spinner-in-kotlin/
    private fun spinnerSetup(ingredients: IngredientsDTO) {
        spinnerAdapter = SpinnerAdapter(ingredients)

        val spinner: Spinner = binding.spinner
        spinner.adapter = spinnerAdapter

        // Prevent auto selecting first ingredient
        //spinner.setSelection(spinner.selectedItemPosition, false)

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                progressBar?.visibility = View.VISIBLE
                selectedIngredient = position
                sViewModel?.getRecipesByIngredient(spinnerAdapter?.getItem(position) as String)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

}
