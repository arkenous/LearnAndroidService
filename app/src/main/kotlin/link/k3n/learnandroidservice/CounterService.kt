package link.k3n.learnandroidservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import java.util.*

class CounterService : Service() {

    private val TAG: String = CounterService::class.java.name
    private lateinit var mHandlerThread: HandlerThread
    private lateinit var mHandler: Handler
    private val mBinder: IBinder = LocalBinder()

    private val timer = Timer()
    private var count: Int = 0

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")

        mHandlerThread = HandlerThread("CounserService")
        mHandlerThread.start()

        mHandler = Handler(mHandlerThread.looper)
    }

    fun postRunnable(runnable: Runnable) {
        mHandler.post(runnable)
    }

    /**
     * Call this method when use startService() to start this service.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand")

        timer.schedule(object : TimerTask() {
            override fun run() {
                Log.d("CounterService", "count:"+count)
                count++
            }
        }, 0, 1000)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
        timer.cancel()
        mHandlerThread.quitSafely()
    }

    inner class LocalBinder : Binder() {
        fun getService() : CounterService {
            return this@CounterService
        }
    }

    /**
     * Call this method when use bindService() to start this service.
     */
    override fun onBind(intent: Intent?): IBinder {
        Log.i(TAG, "onBind")
        return mBinder
    }
}