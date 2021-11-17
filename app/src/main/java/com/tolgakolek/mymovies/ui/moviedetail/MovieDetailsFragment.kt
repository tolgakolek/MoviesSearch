package com.tolgakolek.mymovies.ui.moviedetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tolgakolek.mymovies.R
import com.tolgakolek.mymovies.data.model.MovieDetail
import com.tolgakolek.mymovies.databinding.FragmentMovieDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {
    private val binding: FragmentMovieDetailsBinding by viewBinding()
    private val viewModel: MovieDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activityBar = (activity as AppCompatActivity).supportActionBar
        arguments?.getString("id")?.let { viewModel.getMovieDetail(it) }
        lifecycleScope.launch {
            viewModel.viewState.collect {
                it.movie?.let {
                    activityBar?.setDisplayHomeAsUpEnabled(true)
                    activityBar?.setTitle("Movie Details")
                    bindMovieDetail(it)
                }
            }
        }
    }

    private fun bindMovieDetail(movieDetail: MovieDetail) {
        binding.apply {
            lottieAnimation.visibility = View.GONE
            tvMovieName.text = movieDetail.title
            tvMovieActors.text = movieDetail.actors
            tvMovieDescription.text = movieDetail.plot
            tvMovieDuration.text = movieDetail.runtime
            tvMovieReleased.text = movieDetail.released
            tvMovieScore.text = movieDetail.imdbRating
            tvMovieType.text = movieDetail.genre
            Glide.with(binding.root)
                .load(movieDetail.poster)
                .thumbnail(0.5f)
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgPoster)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_details, container, false)
    }
}