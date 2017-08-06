package link.k3n.learnandroidservice

import android.databinding.ObservableBoolean

class MainViewModel(val isStateActive: ObservableBoolean, val onServiceStateChanged: OnServiceStateChanged) {

    interface OnServiceStateChanged {
        fun stateChanged()
    }

    fun startService() {
        isStateActive.set(true)
        onServiceStateChanged.stateChanged()
    }

    fun stopService() {
        isStateActive.set(false)
        onServiceStateChanged.stateChanged()
    }
}