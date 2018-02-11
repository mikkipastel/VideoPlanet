package com.mikkipastel.videoplanet

import android.os.Bundle
import android.support.v4.app.Fragment
import com.mikkipastel.videoplanet.player.PlayerManager

open class BaseFragment : Fragment() {

    lateinit var playerManager: PlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerManager = PlayerManager.with(context)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        playerManager.bind()
    }

    override fun onStop() {
        super.onStop()
    }
}
