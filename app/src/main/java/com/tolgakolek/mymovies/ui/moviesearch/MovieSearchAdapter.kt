package com.tolgakolek.mymovies.ui.moviesearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tolgakolek.mymovies.R
import com.tolgakolek.mymovies.data.model.SearchResult
import com.tolgakolek.mymovies.databinding.ItemMovieBinding

class MovieSearchAdapter(private val listener: MovieItemListener) :
    RecyclerView.Adapter<MovieSearchAdapter.MovieSearchViewHolder>() {

    interface MovieItemListener {
        fun onClickMoviePoster(imdbId: String)
    }

    private val movies = ArrayList<SearchResult.Movie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieSearchViewHolder {
        val binding: ItemMovieBinding =
            ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieSearchViewHolder(binding)
    }

    fun setItems(newMovies: List<SearchResult.Movie>) {
        movies.clear()
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }

    fun setMoreMovies(newMovies: List<SearchResult.Movie>){
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MovieSearchViewHolder, position: Int) {
        holder.itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context,R.anim.alpha)
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    inner class MovieSearchViewHolder(
        private val itemBinding: ItemMovieBinding
    ) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        private lateinit var movie: SearchResult.Movie

        init {
            itemBinding.root.setOnClickListener(this)
        }

        fun bind(item: SearchResult.Movie) {
            movie = item
            itemBinding.apply {
                tvMovieTitle.text = item.title
                tvMovieYear.text = item.year
            }
            Glide.with(itemBinding.root)
                .load(item.poster)
                .centerCrop()
                .thumbnail(0.5f)
                .placeholder(R.drawable.placeholder_thumbnail)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(itemBinding.imageView)
        }

        override fun onClick(p0: View?) {
            listener.onClickMoviePoster(movie.imdbID)
        }
    }
}