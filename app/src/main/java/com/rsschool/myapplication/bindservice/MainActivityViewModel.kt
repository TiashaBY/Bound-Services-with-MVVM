package com.rsschool.myapplication.bindservice

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    private val TAG = "MainActivityViewModel"
    var mIsProgressUpdating = MutableLiveData<Boolean>()
    var mBinder = MutableLiveData<MyService.MyBinder>()

    var serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.d(TAG, "connected to service")
            mBinder.postValue(p1 as MyService.MyBinder)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            mBinder.postValue(null)
        }
    }

    fun getIsProgressUpdating() : LiveData<Boolean> {
        return mIsProgressUpdating
    }

    fun getBinder() : LiveData<MyService.MyBinder> {
        return mBinder
    }


}