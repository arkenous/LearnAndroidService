package link.k3n.learnandroidservice

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import link.k3n.learnandroidservice.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), MainViewModel.OnServiceStateChanged {

    private val TAG: String = MainActivity::class.java.name
    private lateinit var binding:ActivityMainBinding
    private val isStateActive = ObservableBoolean(false)
    private val mRnd = Random()
    private lateinit var receiver: MyBroadcastReceiver

    fun checkServiceState() : Boolean {
        Log.i(TAG, "checkServiceState")
        val manager : ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE).any {
            SampleService::class.java.name == it.service.className
        }
    }

    override fun startService() {
        Log.i(TAG, "startService")
        val intent = Intent(application, SampleService::class.java)
        intent.putExtra("value", mRnd.nextInt(50))
        startService(intent)
    }

    override fun stopService() {
        Log.i(TAG, "stopService")
        if (checkServiceState()) stopService(Intent(application, SampleService::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        isStateActive.set(checkServiceState())

        binding.isStateActive = isStateActive

        val viewModel = MainViewModel(isStateActive, this)
        binding.viewModel = viewModel
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart")
        isStateActive.set(checkServiceState())
        receiver = MyBroadcastReceiver.register(this, object : MyBroadcastReceiver.Callback {
            override fun onEventInvoked(result: Long) {
                Log.d(TAG, "onEventInvoked result:$result")
            }
        })
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")
        receiver.unregister()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }
}
