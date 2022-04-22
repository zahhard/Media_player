package com.example.mediaplayer

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
import android.widget.Button

class MainActivity : AppCompatActivity() {
    var mediaPlayer = MediaPlayer()
    var isPause = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var btnPlay = findViewById<Button>(R.id.btn_play)
        var btnPause = findViewById<Button>(R.id.btn_puse)
        initMediaPlayer(this)

        btnPlay.setOnClickListener {
            mediaPlayer.start()
            isPause = false
        }
        btnPause.setOnClickListener {
            mediaPlayer.pause()
            isPause = true
        }
    }

    private fun initMediaPlayer(context: Context) {

        //  val myUrl = "https://dls.music-fa.com/tagdl/downloads/Salar%20Aghili%20-%20Baradar%20Jan%20(320).mp3" // initialize Uri here


        val url = "https://dls.music-fa.com/tagdl/downloads/Salar%20Aghili%20-%20Baradar%20Jan%20(320).mp3" // your URL here
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(url)
            prepare() // might take long! (for buffering, etc)
        }
        setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        //mediaPlayer.setOnErrorListener(this)
    }

    private fun setWakeMode(applicationContext: Context?, partialWakeLock: Int) {
        val wifiManager = getApplicationContext()?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiLock: WifiLock =
            wifiManager.createWifiLock(WIFI_MODE_FULL, "mylock")

        wifiLock.acquire()

        if (isPause)
            wifiLock.release()
    }

    //mediaPlayer = MediaPlayer.create(context, R.raw.sound_file_1)
    //mediaPlayer.start() // no need to call prepare(); create() does that for you

}