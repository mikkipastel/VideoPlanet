package com.mikkipastel.videoplanet

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.mikkipastel.videoplanet.playlist.PlaylistFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.contentContainer, PlaylistFragment.newInstance())
                    .commit()
        }
    }
}
