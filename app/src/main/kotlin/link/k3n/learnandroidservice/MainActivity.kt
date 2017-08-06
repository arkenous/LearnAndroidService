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

class MainActivity : AppCompatActivity(), MainViewModel.OnServiceStateChanged {

    private val TAG: String = MainActivity::class.java.name
    private lateinit var binding:ActivityMainBinding
    private val isStateActive = ObservableBoolean(false)

    fun checkServiceState() : Boolean {
        val manager : ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE).any {
            CounterService::class.java.name == it.service.className
        }
    }

    override fun stateChanged() {
        Log.d(TAG, "stateChanged")

        if (isStateActive.get()) startService(Intent(application, CounterService::class.java))
        else stopService(Intent(application, CounterService::class.java))
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
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

}
