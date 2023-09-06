package com.example.anime.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.anime.R
import com.example.anime.databinding.FragmentHomeBinding
import com.example.anime.domain.Anime
import com.example.anime.ui.details.DetailsActivity
import timber.log.Timber

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var homeAdapter: HomeListAdapter? = null

    // Delay creation of the viewModel until an appropriate lifecycle method
    private val viewModel: HomeViewModel by lazy {
        val activity = requireNotNull(this.activity){
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(this,
            HomeViewModel.Factory(activity.application))[HomeViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Get a reference to the binding object and inflate the fragment views
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        // Set the lifecycleOwner so dataBinding can observe LiveData
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        homeAdapter = HomeListAdapter(AnimeDetails {
            if(it != null){
                val intent = Intent(context, DetailsActivity::class.java)
                intent.putExtra("title", it.title)
                intent.putExtra("year", it.year)
                intent.putExtra("episodes", it.episodes)
                intent.putExtra("score", it.score)
                intent.putExtra("duration", it.duration)
                intent.putExtra("trailerUrl", it.trailerUrl)
                intent.putExtra("synopsis", it.synopsis)
                intent.putExtra("rating", it.rating)
                intent.putExtra("source", it.source)
                intent.putExtra("imageUrl", it.imageUrl)
                startActivity(intent)
                activity?.overridePendingTransition(R.anim.right_enter, R.anim.left_out)
            }

        })

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = homeAdapter
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshData()
            binding.swipeRefreshLayout.isRefreshing = false // Stop refreshing animation
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.right_enter)
        binding.recyclerView.startAnimation(fadeInAnimation)

        viewModel.animeList.observe(viewLifecycleOwner) { animes ->
            animes.apply {

                if(animes.isNotEmpty()) {
                    homeAdapter?.animes = animes
                }
                else {
                    Timber.i("Anime list is empty")
                }
            }
        }
    }

    // Click listener for anime item
    class AnimeDetails(val block: (Anime) -> Unit) {
        // called when an anime-item is clicked
        fun onClick(event: Anime) = block(event)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //binding.root = null
    }
}