package com.example.myapplication.ui.audio

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentAudioBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale


class AudioFragment : Fragment() {
    private var _binding: FragmentAudioBinding? = null

    private val binding get() = _binding!!

    private lateinit var mediaRecorder:MediaRecorder
    private lateinit var mediaPlayer:MediaPlayer
    private var audioPath: String = ""


    private var isRecording = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mediaRecorder = MediaRecorder(requireContext())
        mediaPlayer = MediaPlayer()


        binding.startToggleB.isEnabled = false

        if (ActivityCompat.checkSelfPermission(this.requireActivity().baseContext, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), 111)
            binding.startToggleB.isEnabled =  true
        }

        binding.startToggleB.setOnClickListener {
            if(!isRecording) {
                audioPath = requireActivity().getExternalFilesDir(null)?.absolutePath + "/my_audio.mp3"
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

                mediaRecorder.setOutputFile(audioPath)

                try {
                    mediaRecorder.prepare()
                } catch (e: IOException) {
                    Log.e("AudioRecordTest", "prepare() failed")
                }
                mediaRecorder.start()

                binding.startToggleB.setImageResource(R.drawable.stop_button)
            } else {
                try {
                    mediaRecorder.stop()
                    mediaRecorder.reset()

                    binding.startToggleB.setImageResource(R.drawable.voice_control)

                    Toast.makeText(requireContext(), "Audio saved", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Log.e("Audio stop", e.message.toString())
                }

            }

            isRecording = !isRecording
        }

        binding.playB.setOnClickListener {
            Log.d("audio folder",audioPath)
            try {
                mediaPlayer.setDataSource(audioPath)
                mediaPlayer.prepare()
                mediaPlayer.start()
            } catch (e: IOException) {
                Log.e("AudioPlayTest", "prepare() failed")
            }

        }

        return root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            binding.startToggleB.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}