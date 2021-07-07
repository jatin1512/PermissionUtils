package com.app.permissionutils

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.app.jppermission.PermissionManager
import com.app.jppermission.model.PermissionResult
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(parentJob + Dispatchers.Default)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        coroutineScope.launch {
            withContext(Dispatchers.Main) {
                handleResult(
                    PermissionManager.requestPermissions(
                        this@MainActivity, 1,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    )
                )
            }
        }
    }


    private fun handleResult(permissionResult: PermissionResult) {
        when (permissionResult) {
            is PermissionResult.PermissionGranted -> {
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
            }
            is PermissionResult.PermissionDenied -> {
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
            }
            is PermissionResult.PermissionRequired -> {
                coroutineScope.launch {
                    withContext(Dispatchers.Main) {
                        handleResult(
                            PermissionManager.requestPermissions(
                                this@MainActivity, 1,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        )
                    }
                }
            }
            is PermissionResult.PermissionDeniedPermanently -> {
                Toast.makeText(this, "Denied permanently", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        parentJob.cancel()
    }

}