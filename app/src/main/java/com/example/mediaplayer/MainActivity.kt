package com.example.mediaplayer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
import android.widget.Button
import androidx.core.app.ActivityCompat
import java.io.IOException

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class MainActivity : AppCompatActivity() {
    private var mediaPlayer = MediaPlayer()
    private var mediaRecorder = MediaRecorder()
    private var isPause = true
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var fileName: String = ""
    private var isRecord = false
    private var i = 0
    private var j = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPlay = findViewById<Button>(R.id.btn_play)
        val btnPause = findViewById<Button>(R.id.btn_puse)
        val btnRecord = findViewById<Button>(R.id.btn_record)
        val btnPlayMyVoice = findViewById<Button>(R.id.btn_play_voice)

        val temp = "zahraTestRecordingVoice"
        fileName = "${externalCacheDir?.absolutePath}/$temp.3gp"

        givePermission()
        initMediaPlayer()
        initRecordeer()
        mediaPlayerButtonsHandler(btnPlay, btnPause)

        btnRecord.setOnClickListener {
           /// mediaRecorder.start()
             i ++
            isRecord = i % 2 != 0
            onRecord(isRecord)
        }

        btnPlayMyVoice.setOnClickListener {
            j++
            isRecord = j % 2 != 0
            onPlay(isRecord)
        }
    }

    private fun givePermission() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }

    private fun initRecordeer() {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {

            }
        }
    }

    private fun mediaPlayerButtonsHandler(
        btnPlay: Button,
        btnPause: Button
    ) {
        btnPlay.setOnClickListener {
            mediaPlayer.start()
            isPause = false
        }
        btnPause.setOnClickListener {
            mediaPlayer.pause()
            isPause = true
        }
    }

    private fun initMediaPlayer() {

        val url =
            "https://dls.music-fa.com/tagdl/downloads/Salar%20Aghili%20-%20Baradar%20Jan%20(320).mp3" // your URL here
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
    }

    private fun setWakeMode(applicationContext: Context?, partialWakeLock: Int) {
        val wifiManager =
            getApplicationContext()?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiLock: WifiLock =
            wifiManager.createWifiLock(WIFI_MODE_FULL, "mylock")

        wifiLock.acquire()

        if (isPause)
            wifiLock.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }
///////////////////////////////////////////////////////////////////////////

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun startPlaying() {
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                //Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        mediaPlayer.release()
    }

    private fun startRecording() {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
               // Log.e(LOG_TAG, "prepare() failed")
            }

            start()
        }
    }

    private fun stopRecording() {
        mediaRecorder.apply {
            stop()
            release()
        }
    }
}