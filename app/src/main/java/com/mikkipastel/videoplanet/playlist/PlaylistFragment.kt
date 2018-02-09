package com.mikkipastel.videoplanet.playlist

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikkipastel.videoplanet.R
import com.mikkipastel.videoplanet.player.PlayerFragment

class PlaylistFragment : Fragment(), ItemListener {

    lateinit var videoListView: RecyclerView
    lateinit var mAdapter: RecyclerAdapter

    // TODO: change to call api
    var videoname = arrayOf("BigBuckBunny", "small", "jellyfish-25-mbps-hd-hevc", "lion-sample", "page18-movie-4", "Panasonic_HDC_TM_700_P_50i")

    var url = arrayOf("http://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_320x180.mp4",
            "http://techslides.com/demos/sample-videos/small.mp4",
            "http://mirrors.standaloneinstaller.com/video-sample/jellyfish-25-mbps-hd-hevc.mp4",
            "http://mirrors.standaloneinstaller.com/video-sample/lion-sample.mp4",
            "http://mirrors.standaloneinstaller.com/video-sample/page18-movie-4.mp4",
            "http://mirrors.standaloneinstaller.com/video-sample/Panasonic_HDC_TM_700_P_50i.mp4")

    companion object {
        fun newInstance(): PlaylistFragment {
            return PlaylistFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_playlist, container, false)
        initInstances(rootView)
        return rootView
    }

    private fun initInstances(rootView: View) {
        videoListView = rootView.findViewById(R.id.videolist)

        mAdapter = RecyclerAdapter(this, videoname)
        videoListView.isNestedScrollingEnabled = false
        videoListView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        videoListView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View, position: Int) {
        val fragment = PlayerFragment.newInstance(url[position])
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.contentContainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
