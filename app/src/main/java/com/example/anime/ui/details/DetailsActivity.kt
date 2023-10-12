package com.example.anime.ui.details

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.anime.R
import com.example.anime.databinding.ActivityDetailsBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import timber.log.Timber

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var title: String
    private lateinit var year: String
    private lateinit var episodes: String
    private lateinit var score: String
    private lateinit var duration: String
    private lateinit var trailerUrl: String
    private lateinit var synopsis: String
    private lateinit var rating: String
    private lateinit var source: String
    private lateinit var imageUrl : String

    // Delay creation of the viewModel until an appropriate lifecycle method
//    private val viewModel: DetailsViewModel by lazy {
//        val activity = requireNotNull(this){
//            "You can only access the viewModel after onViewCreated()"
//        }
//        ViewModelProvider(this,
//            DetailsViewModel.Factory(activity.application))[DetailsViewModel::class.java]
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent
        if (extras != null) {
            title = extras.getStringExtra("title").toString()
            year = extras.getIntExtra("year", 0).toString()
            episodes = extras.getStringExtra("episodes").toString()
            score = extras.getFloatExtra("score", 0.0F).toString()
            duration = extras.getStringExtra("duration").toString()
            trailerUrl = extras.getStringExtra("trailerUrl").toString()
            synopsis = extras.getStringExtra("synopsis").toString()
            rating = extras.getStringExtra("rating").toString()
            source = extras.getStringExtra("source").toString()
            imageUrl = extras.getStringExtra("imageUrl").toString()

            binding.title.text = title
            binding.year.text = "Year: $year"
            binding.score.text = "Score: $score"
            binding.rating.text = rating
            binding.synopsis.text = synopsis

            supportActionBar?.title = "$title Details:"

            binding.youtubePlayerView.enableAutomaticInitialization = false
            binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener(){
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    super.onReady(youTubePlayer)

                    youTubePlayer.cueVideo(trailerUrl, 0f)
                    youTubePlayer.pause()
                    youTubePlayer.mute()
                }

                override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                    super.onError(youTubePlayer, error)
                    Timber.tag("#Error").e(error.toString())
                }
            })

            binding.imageView?.let {
                Glide
                    .with(binding.imageView.context)
                    .load(imageUrl)
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.loading_animation)
                            .error(R.drawable.ic_broken_image)
                    ).into(binding.imageView)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        backFinish()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backFinish()
    }

    private fun backFinish() {
        overridePendingTransition(R.anim.left_enter, R.anim.right_out)
        finish()
    }

    // Check for internet connection
    private fun internetCheck() {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        observeConnectivity(connectivityManager)

        if (networkInfo == null || !networkInfo.isConnected) {
            // No Internet conn
            binding.tvNoInternet.visibility = View.VISIBLE
        } else {
            // Internet available
            binding.tvNoInternet.visibility = View.GONE
        }
    }

    private fun observeConnectivity(connectivityManager: ConnectivityManager) {
        val builder = NetworkRequest.Builder()
        connectivityManager.registerNetworkCallback(
            builder.build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    // Internet available
                    runOnUiThread {
                        binding.tvNoInternet.visibility = View.GONE
                    }
                }
                override fun onLost(network: Network) {
                    // No Internet connection
                    runOnUiThread {
                        binding.tvNoInternet.visibility = View.VISIBLE
                    }
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()
        internetCheck()
    }

    override fun onResume() {
        super.onResume()
        internetCheck()
    }

    override fun onPause() {
        super.onPause()
        internetCheck()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.youtubePlayerView.release()
    }

}
