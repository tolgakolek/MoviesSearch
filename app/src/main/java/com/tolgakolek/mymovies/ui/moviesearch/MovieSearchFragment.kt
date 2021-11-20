package com.tolgakolek.mymovies.ui.moviesearch

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.tolgakolek.mymovies.R
import com.tolgakolek.mymovies.data.model.SearchResult
import com.tolgakolek.mymovies.databinding.FragmentMovieDetailsBinding
import com.tolgakolek.mymovies.databinding.FragmentMovieSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MovieSearchFragment : Fragment(), MovieSearchAdapter.MovieItemListener {

    private lateinit var dataBinding: FragmentMovieSearchBinding
    private val movieSearchAdapter: MovieSearchAdapter by lazy { MovieSearchAdapter(this) }
    private val viewModel: MovieSearchViewModel by viewModels()
    private var isLoading = false
    private var isLoadData = false
    private var totalResults = 0
    private var movies: List<SearchResult.Movie>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(requireActivity(),R.layout.fragment_movie_search)
        val activityBar = (activity as AppCompatActivity).supportActionBar
        activityBar?.setDisplayHomeAsUpEnabled(false)
        activityBar?.setTitle("Movie Search")
        setupRecyclerView()
        setupButton()
        lifecycleScope.launchWhenResumed {
            viewModel.viewState.collect {
                isLoadData = it.isLoadData
                it.movieTitle?.let { dataBinding.edSearch.hint = it }
                it.moviesSearch?.let {
                    totalResults = it.totalResults
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
        if (isLoadData && movies != newMovies) {
            if (isLoading) {
                movieSearchAdapter.setMoreMovies(newMovies)
            } else {
                movieSearchAdapter.setItems(newMovies)
            }
        }
        dataBinding.apply {
            lottieAnimation.visibility = View.GONE
            progressBar.visibility = View.GONE
            tvWarning.visibility = View.GONE
        }
        movies = newMovies
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
        }
        dataBinding.edSearch.setOnEditorActionListener( object : TextView.OnEditorActionListener {
            override fun onEditorAction(textView: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                searchMovie(textView?.text.toString())
                return false
            }
        })
    }

    private fun searchMovie(title : String){
        isLoading = false
        viewModel.getMoviesSearch(title)
        dataBinding.tvWarning.visibility = View.GONE
        dataBinding.lottieAnimation.visibility = View.VISIBLE
    }

    private fun getMoreMovies() {
        isLoading = true
        dataBinding.progressBar.visibility = View.VISIBLE
        viewModel.getMoreMovies()
    }

    private fun recyclerViewScrollListener(): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState > 0) {
                    isLoading = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    if (isLoading && totalItemCount < totalResults && isLoadData) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                            getMoreMovies()
                        }
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