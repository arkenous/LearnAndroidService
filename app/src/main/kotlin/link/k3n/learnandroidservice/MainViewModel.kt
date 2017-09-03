package link.k3n.learnandroidservice

import android.databinding.ObservableBoolean

class MainViewModel(val isStateActive: ObservableBoolean, val onServiceStateChanged: OnServiceStateChanged) {

    interface OnServiceStateChanged {
        fun startServiceForeground()
        fun stopServiceForeground()
        fun runFib()
    }

    fun startServiceForeground() {
        isStateActive.set(true)
        onServiceStateChanged.startServiceForeground()
    }

    fun stopServiceForeground() {
        isStateActive.set(false)
        onServiceStateChanged.stopServiceForeground()
    }

    fun runFib() {
        onServiceStateChanged.runFib()
    }
}