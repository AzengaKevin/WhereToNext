package com.mysasse.wheretonext

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.mysasse.wheretonext.utils.toast

class RequestPermissionsActivity : AppCompatActivity() {

    companion object {
        const val TAG = "RequestPermissions"
        const val LOCATION_PERMISSION_RC = 78
    }

    private lateinit var grantPermissionButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_permissions)


        grantPermissionButton = findViewById(R.id.grant_permission_btn)

        grantPermissionButton.setOnClickListener {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_RC
            )

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() and (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            when (requestCode) {
                LOCATION_PERMISSION_RC -> {
                    Log.d(TAG, "Location permission has been granted")
                    toast("Permission granted successfully")
                    finish()
                }

                else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        } else {
            Log.d(TAG, "Requested permission denied")
        }
    }
}
