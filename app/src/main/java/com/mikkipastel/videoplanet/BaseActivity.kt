package com.mikkipastel.videoplanet

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mikkipastel.videoplanet.player.PlayerManager

open class BaseActivity : AppCompatActivity() {

    lateinit var playerManager: PlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerManager = PlayerManager.with(this)
        playerManager.bind()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }
}
