package link.k3n.learnandroidservice

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import android.util.Log

class SampleService : Service() {

    private val TAG: String = SampleService::class.java.name
    private val map: HashMap<Int, Long> = HashMap()
    private lateinit var mHandlerThread: HandlerThread
    private lateinit var mHandler: Handler
    private val mBinder: IBinder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")

        mHandlerThread = HandlerThread("CounserService")
        mHandlerThread.start()

        mHandler = Handler(mHandlerThread.looper)
    }

    fun postRunnable(runnable: Runnable) {
        Log.i(TAG, "postRunnable")
        mHandler.post(runnable)
    }

    fun fib(n:Int):Long {
        if (n == 0) return 0
        if (n == 1) return 1
        return fib(n - 1) + fib(n - 2)
    }

    fun improved_fib(n:Int):Long {
        if (n == 0) return 0
        if (n == 1) return 1
        if (map.containsKey(n)) return map.getValue(n)
        val value = improved_fib(n - 1) + improved_fib(n - 2)
        map.put(n, value)
        return value
    }

    /**
     * Call this method when use startService() to start this service.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand")

        //region run as foreground
        val activityIntent: Intent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0)
        val notification: Notification = Notification.Builder(this)
                .setContentTitle("SampleService")
                .setContentText("Working...")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()
        startForeground(startId, notification)
        //endregion

        val position:Int
        if (intent?.extras?.getInt("value") != null) {
            position = intent.extras.getInt("value")
            Log.d(TAG, "position:$position")
        }
        else {
            Log.e(TAG, "Couldn't receive value")
            position = 10
        }

        postRunnable(Runnable {
            val result = fib(position)

            //region Create and Send Notification
            val builder = NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText("SampleService")
                    .setContentText("Result:$result")

            // Start activity when tapped notification
            val resultIntent = Intent(this, MainActivity::class.java)
            val stackBuilder = TaskStackBuilder.create(this)
            stackBuilder.addParentStack(MainActivity::class.java)
            stackBuilder.addNextIntent(resultIntent)
            val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            builder.setContentIntent(resultPendingIntent)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(startId, builder.build())
            //endregion

            MyBroadcastReceiver.sendBroadcast(this, result)
        })

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
        mHandlerThread.quitSafely()
    }

    inner class LocalBinder : Binder() {
        fun getService() : SampleService {
            return this@SampleService
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