package com.tolgakolek.mymovies.ui.moviedetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tolgakolek.mymovies.R
import com.tolgakolek.mymovies.data.model.MovieDetail
import com.tolgakolek.mymovies.databinding.FragmentMovieDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {
    private val viewModel: MovieDetailViewModel by viewModels()
    private lateinit var dataBinding: FragmentMovieDetailsBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(requireActivity(),R.layout.fragment_movie_details)
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
        dataBinding.movieDetail = movieDetail
        dataBinding.lottieAnimation.visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_details, container, false)
    }
}
@BindingAdapter("loadImage")
fun ImageView.loadImage(url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(this.context)
            .load(url)
            .thumbnail(0.5f)
            .placeholder(R.drawable.placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(this)
    }
}