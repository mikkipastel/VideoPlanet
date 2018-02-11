package com.mikkipastel.videoplanet

import android.os.Bundle
import com.mikkipastel.videoplanet.playlist.PlaylistFragment

class MainActivity : BaseActivity() {

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
