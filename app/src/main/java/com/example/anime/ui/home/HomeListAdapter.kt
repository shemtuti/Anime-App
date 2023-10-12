package com.example.anime.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.anime.R
import com.example.anime.databinding.ListItemAnimeBinding
import com.example.anime.domain.Anime

// RecyclerView Adapter for setting up data binding on the anime items in the list

class HomeListAdapter(val callback: HomeFragment.AnimeDetails) :
    RecyclerView.Adapter<HomeListAdapter.HomeViewHolder>() {

    // Anime list that the adapter will show
    var animes: List<Anime> = emptyList()
        set(value) {
            field = value

            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val withDataBinding: ListItemAnimeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            HomeViewHolder.LAYOUT,
            parent,
            false
        )
        return HomeViewHolder(withDataBinding)
    }


    override fun getItemCount(): Int {
        return animes.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.anime = animes[position]
            it.detailsCallback = callback
        }

    }

    // ViewHolder for anime items. All work done by data binding
    class HomeViewHolder(val viewDataBinding: ListItemAnimeBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.list_item_anime
        }
    }
}

