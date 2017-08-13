package link.k3n.learnandroidservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.util.Log

class MyBroadcastReceiver(context:Context, private val callback: Callback) : BroadcastReceiver() {

    interface Callback {
        fun onEventInvoked(result: Long)
    }

    private val manager:LocalBroadcastManager = LocalBroadcastManager.getInstance(context.applicationContext)

    companion object {
        private val TAG = MyBroadcastReceiver::class.java.name
        private val ACTION_INVOKED = "${MyBroadcastReceiver::class.java.canonicalName}.ACTION_INVOKED"
        private val RESULT = "RESULT"

        fun register(context: Context, callback: Callback): MyBroadcastReceiver {
            Log.i(TAG, "register")
            return MyBroadcastReceiver(context, callback)
        }

        fun sendBroadcast(context: Context, result: Long){
            Log.i(TAG, "sendBroadcast")
            val intent = Intent(ACTION_INVOKED)
            intent.putExtra("RESULT", result)

            val manager = LocalBroadcastManager.getInstance(context.applicationContext)
            manager.sendBroadcast(intent)
        }
    }

    init {
        val filter = IntentFilter()
        filter.addAction(ACTION_INVOKED)

        manager.registerReceiver(this, filter)
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive")
        val action: String = intent.action

        if (ACTION_INVOKED == action) {
            val result: Long = intent.getLongExtra(RESULT, -1)
            callback.onEventInvoked(result)
        }
    }

    fun unregister() {
        Log.i(TAG, "unregister")
        manager.unregisterReceiver(this)
    }
}