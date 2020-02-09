package com.androidapp.navweiandroidv2.presentation

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.util.ext.checkAndRequestPermissions
import com.androidapp.navweiandroidv2.util.ext.explain
import com.androidapp.navweiandroidv2.util.ext.showDialogOK

/**
 * Created by S.Nur Uysal on 2019-11-06.
 */
open class BaseActivity : AppCompatActivity() {
    private val TAG = "tag"

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState, persistentState)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        Log.d(TAG, "Permission callback called-------")
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {

                val perms = HashMap<String, Int>()
                perms[Manifest.permission.CALL_PHONE] = PackageManager.PERMISSION_GRANTED

                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    // Check for both permissions
                    if (perms[Manifest.permission.CALL_PHONE] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "call phone services permission granted")
                        // process the normal flow
                        //todo

                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ")
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.CALL_PHONE
                            )
                        ) {
                            showDialogOK("Service Permissions are required for this app",
                                DialogInterface.OnClickListener { _, which ->
                                    when (which) {
                                        DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
//                                        DialogInterface.BUTTON_NEGATIVE ->
                                            // proceed with logic by disabling the related features or quit the app.
//                                            finish()
                                    }
                                })
                        } else {
                            explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?")
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }



    companion object {

        const val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
    }
}