package com.rsschool.myapplication.bindservice

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivityViewModel : ViewModel() {

    private val TAG = "MainActivityViewModel"
    var _mIsProgressUpdating = MutableStateFlow<Boolean>(false)
    var mIsProgressUpdating = _mIsProgressUpdating.asStateFlow()

    var _mBinder = MutableStateFlow<MyService.MyBinder?>(null)
    var mBinder = _mBinder.asStateFlow()

    var serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.d(TAG, "connected to service")
            _mBinder.value = p1 as MyService.MyBinder
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            _mBinder.value = null
        }
    }

    fun getBinder() : StateFlow<MyService.MyBinder?> {
        return mBinder
    }
}