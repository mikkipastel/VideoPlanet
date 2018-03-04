package com.mikkipastel.videoplanet.player

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.ui.PlayerView

import com.mikkipastel.videoplanet.base.BaseFragment
import com.mikkipastel.videoplanet.R
import com.mikkipastel.videoplanet.player.PlayerManager.getService

class PlayerFragment : BaseFragment() {

    private var playerView: PlayerView? = null

    private var videoUrl: String? = null

    companion object {

        private val BUNDLE_URL = "bundle_url"

        fun newInstance(url: String): PlayerFragment {
            val args = Bundle()
            args.putString(BUNDLE_URL, url)

            val fragment = PlayerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_player, container, false)
        initInstances(rootView)
        return rootView
    }

    private fun initInstances(rootView: View) {
        playerView = rootView.findViewById(R.id.video_view)

        if (getService() == null) {
            playerManager.bind()
        }

        playerView!!.controllerHideOnTouch = false
        playerView!!.controllerShowTimeoutMs = 0
        playerView!!.showController()

    }

    private fun hasArguments(bundle: Bundle?): Boolean {
        return bundle?.getString(BUNDLE_URL) != null
    }

    override fun onResume() {
        super.onResume()

        managerBinding()
    }

    override fun onDestroy() {
        playerManager.unbind()

        super.onDestroy()
    }

    private fun managerBinding() {
        val bundle = arguments
        if (hasArguments(bundle)) {
            videoUrl = bundle!!.getString(BUNDLE_URL)
        } else {
            videoUrl = getString(R.string.media_url_mp4)
        }

        if (playerManager.isServiceBound) {
            playerManager.playOrPause(videoUrl)
            playerView!!.player = getService().exoPlayer
        }

    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUiFullScreen() {
        playerView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        playerView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        val currentOrientation = resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUiFullScreen()
        } else {
            hideSystemUi()
        }
    }
}