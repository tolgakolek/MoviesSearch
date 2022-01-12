package com.tolgakolek.mymovies.ui.moviesearch

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tolgakolek.mymovies.R
import com.tolgakolek.mymovies.data.model.SearchResult
import com.tolgakolek.mymovies.databinding.FragmentMovieSearchBinding
import com.tolgakolek.mymovies.util.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MovieSearchFragment : Fragment(), MovieSearchAdapter.MovieItemListener {

    private lateinit var dataBinding: FragmentMovieSearchBinding
    private val movieSearchAdapter: MovieSearchAdapter by lazy { MovieSearchAdapter(this) }
    private val viewModel: MovieSearchViewModel by viewModels()
    private var isLoading = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBinding =
            DataBindingUtil.setContentView(requireActivity(), R.layout.fragment_movie_search)
        val activityBar = (activity as AppCompatActivity).supportActionBar
        activityBar?.setDisplayHomeAsUpEnabled(false)
        activityBar?.setTitle("Movie Search")
        setupRecyclerView()
        setupButton()
        getViewModelData()
    }

    private fun getViewModelData() {
        lifecycleScope.launchWhenResumed {
            viewModel.viewState.collect {
                it.movieTitle?.let { dataBinding.edSearch.hint = it }
                it.moviesSearch?.let {
                    it.search?.let {
                        setAdapterItems(it)
                    } ?: kotlin.run {
                        dataBinding.apply {
                            lottieAnimation.visibility = View.GONE
                            tvWarning.apply {
                                text = resources.getString(R.string.not_found_movie)
                                visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setAdapterItems(newMovies: List<SearchResult.Movie>) {
        movieSearchAdapter.setItems(newMovies,viewModel.viewState.value.isLoadData)
        dataBinding.apply {
            lottieAnimation.visibility = View.GONE
            tvWarning.visibility = View.GONE
        }
        isLoading = false
    }

    private fun setupRecyclerView() {
        dataBinding.recyclerView.apply {
            adapter = movieSearchAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addOnScrollListener(recyclerViewScrollListener())
        }
    }

    private fun setupButton() {
        dataBinding.btnSearch.setOnClickListener {
            searchMovie(dataBinding.edSearch.text.toString())
            it.hideKeyboard()
        }
        dataBinding.edSearch.setOnEditorActionListener { textView, p1, p2 ->
            searchMovie(textView?.text.toString())
            textView?.hideKeyboard()
            false
        }
    }

    private fun searchMovie(title: String) {
        viewModel.getMoviesSearch(title)
        dataBinding.lottieAnimation.visibility = View.VISIBLE
    }

    private fun getMoreMovies() {
        viewModel.getMoreMovies()
    }

    private fun recyclerViewScrollListener(): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState > RecyclerView.SCROLL_STATE_IDLE) {
                    isLoading = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    var totalResult = 0
                    viewModel.viewState.value.moviesSearch?.totalResults?.let {
                        totalResult = it
                    }
                    if (isLoading && totalItemCount < totalResult && viewModel.viewState.value.isLoadData
                        && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    ) {
                        getMoreMovies()
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_search, container, false)
    }

    override fun onClickMoviePoster(imdbId: String) {
        findNavController().navigate(
            R.id.action_movieSearchFragment_to_movieDetailsFragment,
            bundleOf("id" to imdbId)
        )
    }
}