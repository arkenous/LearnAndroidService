package link.k3n.learnandroidservice

import android.databinding.ObservableBoolean

class MainViewModel(val isStateActive: ObservableBoolean, val onServiceStateChanged: OnServiceStateChanged) {

    interface OnServiceStateChanged {
        fun startService()
        fun stopService()
    }

    fun startService() {
        isStateActive.set(true)
        onServiceStateChanged.startService()
    }

    fun stopService() {
        isStateActive.set(false)
        onServiceStateChanged.stopService()
    }
}