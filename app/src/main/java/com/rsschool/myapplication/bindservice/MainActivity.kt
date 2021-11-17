package com.rsschool.myapplication.bindservice

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.rsschool.myapplication.bindservice.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collect

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = checkNotNull(_binding)

    var service : MyService? = null
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            toggleUpdates()
        }

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        lifecycleScope.launchWhenStarted {
            viewModel.mBinder.collect { myBinder ->
               if (myBinder == null) {
                    Log.d("TAG", "onChanged: unbound from service")
                } else {
                    Log.d("TAG", "onChanged: bound to service.")
                    service = myBinder.service
                }
            }
        }

        lifecycleScope.launchWhenStarted {
        viewModel.mIsProgressUpdating.collect()
            { t ->
                    val handler = Handler(Looper.getMainLooper())
                    val runnable = object : Runnable {
                        override fun run() {
                            if (t) {
                                if (viewModel.getBinder().value != null) {
                                    if (service?.mProgress == service?.mMaxValue) {
                                        viewModel._mIsProgressUpdating.value = false
                                    }
                                    binding.progressBar.apply {
                                        progress = service?.mProgress!!
                                        max = service?.mMaxValue!!
                                    }
                                    val progress: String =
                                        java.lang.String.valueOf(100 * service?.mProgress!! / service?.mMaxValue!!)
                                            .toString() + "%"
                                    binding.textView.setText(progress)
                                    handler.postDelayed(this, 1000)
                                }
                            } else {
                                handler.removeCallbacks(this)
                            }
                        }
                    }
                        // control what the button shows
                        if (t)
                        {
                            binding.button.setText("Pause")
                            handler.postDelayed(runnable, 100)
                        } else
                        {
                            if (service?.mProgress == service?.mMaxValue) {
                                binding.button.setText("Restart")
                            } else {
                                binding.button.setText("Start")
                            }
                        }
        }
        }
    }

    private fun toggleUpdates() {
        if (service != null) {
            if (service?.mProgress == service?.mMaxValue) {
                service?.resetTask()
                binding.button.text = "Start"
            } else {
                if (service?.mIsPaused == true) {
                    service?.unPausePretendLongRunningTask()
                    viewModel._mIsProgressUpdating.value = true
                } else {
                    service?.pausePretendLongRunningTask()
                    viewModel._mIsProgressUpdating.value = false
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startMyService()
    }

    fun startMyService() {
        val serviceIntent = Intent(this, MyService::class.java)
        startService(serviceIntent)
        bindService()

    }

    fun bindService() {
        val serviceIntent = Intent(this, MyService::class.java)
        bindService(serviceIntent, viewModel.serviceConnection, Context.BIND_AUTO_CREATE)

    }
}


