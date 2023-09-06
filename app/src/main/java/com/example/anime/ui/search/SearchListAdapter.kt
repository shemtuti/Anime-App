package com.example.anime.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.anime.R
import com.example.anime.databinding.ListItemSearchBinding
import com.example.anime.service.SearchAnime

// RecyclerView Adapter for setting up data binding on the anime items in the list
class SearchListAdapter() : RecyclerView.Adapter<HomeViewHolder>() {

    private var responseData: List<SearchAnime> = emptyList()

    fun setData(newList: List<SearchAnime>) {
        responseData = newList

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val withDataBinding: ListItemSearchBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            HomeViewHolder.LAYOUT,
            parent,
            false)
        return HomeViewHolder(withDataBinding)
    }


    override fun getItemCount(): Int {
        return responseData.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.result = responseData[position]
        }
    }
}

// ViewHolder for anime items. All work done by data binding
class HomeViewHolder(val viewDataBinding: ListItemSearchBinding) : RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.list_item_search
    }
}