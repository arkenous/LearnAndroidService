package link.k3n.learnandroidservice

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import link.k3n.learnandroidservice.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), MainViewModel.OnServiceStateChanged {

    private val TAG: String = MainActivity::class.java.name
    private lateinit var binding:ActivityMainBinding
    private val isStateForeground = ObservableBoolean(false)
    private val mRnd = Random()
    private lateinit var receiver: MyBroadcastReceiver
    private var sampleService:SampleService? = null
    private lateinit var context: Context

    private var result: Long = -1

    fun isServiceForeground() : Boolean {
        Log.i(TAG, "isServiceForeground")
        val manager : ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        return manager.getRunningServices(Integer.MAX_VALUE)
                .filter { SampleService::class.java.name == it.service.className }
                .any { it.foreground }
    }

    fun logMyProcName() {
        Log.i(TAG, "logMyProcName")
        val manager : ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        manager.getRunningServices(Integer.MAX_VALUE)
                .filter { it.pid == android.os.Process.myPid() }
                .forEach { Log.d(TAG, "process name: "+it.process) }
    }

    override fun startServiceForeground() {
        Log.i(TAG, "startServiceForeground")
        sampleService?.startForeground()
    }

    override fun stopServiceForeground() {
        Log.i(TAG, "stopServiceForeground")
        sampleService?.stopForeground()
    }

    override fun runFib() {
        Log.i(TAG, "runFib")
        sampleService?.runFib(mRnd.nextInt(50))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        context = this

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        isStateForeground.set(isServiceForeground())
        binding.isStateForeground = isStateForeground

        val viewModel = MainViewModel(isStateForeground, this)
        binding.viewModel = viewModel

        onNewIntent(intent)

        context.startService(Intent(application, SampleService::class.java))

        logMyProcName()
    }

    override fun onNewIntent(intent: Intent?) {
        Log.i(TAG, "onNewIntent")
        val extras = intent?.extras ?: return
        if (extras.containsKey("result")) result = extras.getLong("result")
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart")
        isStateForeground.set(isServiceForeground())
        receiver = MyBroadcastReceiver.register(this, object : MyBroadcastReceiver.Callback {
            override fun onEventInvoked(result: Long) {
                Log.d(TAG, "onEventInvoked result:$result")
            }
        })

        context.bindService(Intent(application, SampleService::class.java), serviceConnection, 0)
    }

    val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i(TAG, "onServiceDisconnected")
            sampleService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i(TAG, "onServiceConnected")
            if (service == null) {
                Log.e(TAG, "service is null at onServiceConnected")
                return
            }

            val binder:SampleService.LocalBinder = service as SampleService.LocalBinder
            sampleService = binder.getService()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")
        receiver.unregister()
        unbindService(serviceConnection)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }
}
