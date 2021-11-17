package com.rsschool.myapplication.bindservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log

class MyService : Service() {
    val TAG = "MyService"
    val mBinder = MyBinder()
    var mHandler : Handler? = null
    var mProgress : Int? = null
    var mMaxValue : Int? = null
    var mIsPaused : Boolean = false

    override fun onBind(p0: Intent?): IBinder? {
        return mBinder
    }

    inner class MyBinder : Binder() {
        val service: MyService
            get() = this@MyService
    }

    override fun onCreate() {
        super.onCreate()
        mHandler = Handler(Looper.getMainLooper())
        mProgress = 0
        mIsPaused = true
        mMaxValue = 5000
    }

    fun startPretentLongRunninTask() {
        val runnable = object : Runnable {
            override fun run() {
                if (mProgress!! >= mMaxValue!! || mIsPaused) {
                    Log.d(TAG, "run: removing callbacks")
                    mHandler!!.removeCallbacks(this) // remove callbacks from runnable
                    pausePretendLongRunningTask()
                } else {
                    Log.d(TAG, "run: progress: $mProgress")
                    mProgress?.let { mProgress = mProgress!! + 100 } // increment the progress
                    mHandler!!.postDelayed(this, 100) // continue incrementing
                }
            }
        }
        mHandler!!.postDelayed(runnable, 1000)
    }

    fun resetTask() {
        mProgress = 0
    }

    fun pausePretendLongRunningTask() {
        mIsPaused = true
    }

    fun unPausePretendLongRunningTask() {
        mIsPaused = false
        startPretentLongRunninTask()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }
}
