package com.androidapp.navweiandroidv2.util.ext

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.androidapp.entity.models.Locations
import com.androidapp.entity.models.Voucher
import com.androidapp.navweiandroidv2.presentation.BaseActivity

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun AppCompatActivity.addFragment(frameId: Int, fragment: Fragment) {
    supportFragmentManager.executePendingTransactions()
    supportFragmentManager.inTransaction { add(frameId, fragment, fragment.javaClass.simpleName) }
}

fun AppCompatActivity.replaceFragment(frameId: Int, fragment: Fragment) {
    supportFragmentManager.executePendingTransactions()
    supportFragmentManager.inTransaction {
        replace(
            frameId,
            fragment,
            fragment.javaClass.simpleName
        )
    }
}

fun AppCompatActivity.replaceFragment(frameId: Int, fragment: Fragment, addToStack: Boolean) {
    supportFragmentManager.executePendingTransactions()
//    supportFragmentManager.inTransaction {
//        if (addToStack) replace(frameId, fragment, fragment.javaClass.simpleName)
//            .addToBackStack(fragment.javaClass.simpleName)
//        else
//            replace(frameId, fragment, fragment.javaClass.simpleName)
//    }

    supportFragmentManager.inTransaction {
        if (addToStack) add(frameId, fragment, fragment.javaClass.simpleName)
            .addToBackStack(fragment.javaClass.simpleName)
        else add(frameId, fragment)
    }
}

fun AppCompatActivity.replaceFragment(
    frameId: Int,
    fragment: Fragment,
    addToStack: Boolean,
    clearBackStack: Boolean
) {
    supportFragmentManager.executePendingTransactions()
    supportFragmentManager.inTransaction {
        if (clearBackStack && supportFragmentManager.backStackEntryCount > 0) {
            val first = supportFragmentManager.getBackStackEntryAt(0)
            supportFragmentManager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        if (addToStack) replace(frameId, fragment, fragment.javaClass.simpleName)
            .addToBackStack(fragment.javaClass.simpleName)
        else
            replace(frameId, fragment, fragment.javaClass.simpleName)
    }
}

fun AppCompatActivity.addFragment(frameId: Int, fragment: Fragment, addToStack: Boolean) {
    supportFragmentManager.executePendingTransactions()
    supportFragmentManager.inTransaction {
        if (addToStack) add(frameId, fragment, fragment.javaClass.simpleName)
            .addToBackStack(fragment.javaClass.simpleName)
        else add(frameId, fragment)
    }
}

fun AppCompatActivity.getCurrentFragment(): Fragment? {
    val fragmentManager = supportFragmentManager
    var fragmentTag: String? = ""

    if (fragmentManager.backStackEntryCount > 0)
        fragmentTag =
            fragmentManager.getBackStackEntryAt(fragmentManager.backStackEntryCount - 1).name

    return fragmentManager.findFragmentByTag(fragmentTag)
}

fun AppCompatActivity.isPermissionGranted(permission: String): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED

fun AppCompatActivity.checkAndRequestPermissions(): Boolean {
    val phonePermission =
        ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)

    val listPermissionsNeeded = ArrayList<String>()

    if (phonePermission != PackageManager.PERMISSION_GRANTED) {
        listPermissionsNeeded.add(Manifest.permission.CALL_PHONE)
    }

    if (listPermissionsNeeded.isNotEmpty()) {
        ActivityCompat.requestPermissions(
            this,
            listPermissionsNeeded.toTypedArray(),
            BaseActivity.REQUEST_ID_MULTIPLE_PERMISSIONS
        )
        return false
    }
    return true
}

fun AppCompatActivity.showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
    AlertDialog.Builder(this)
        .setMessage(message)
        .setPositiveButton("OK", okListener)
        .setNegativeButton("Cancel", okListener)
        .create()
        .show()
}

fun AppCompatActivity.explain(msg: String) {
    val dialog = AlertDialog.Builder(this)
    dialog.setMessage(msg)
        .setPositiveButton("Yes") { _, _ ->
            goToPhoneSettings()
        }
        .setNegativeButton("Cancel") { _, _ ->
        }
    dialog.show()
}

fun AppCompatActivity.goToPhoneSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

@SuppressLint("MissingPermission")
fun AppCompatActivity.callPhone(phoneNumber: String?) {
    if (phoneNumber == null) {
        Toast.makeText(this, "We don't have related content!", Toast.LENGTH_LONG).show()
        return
    }

    if (checkAndRequestPermissions()) {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            // TODO: Missing exception handling
        }
    }
}

fun AppCompatActivity.openUrlAtBrowser(url: String?) {
    val openURL = Intent(Intent.ACTION_VIEW)
    var webUrl = url

    if (url == null) {
        Toast.makeText(this, "We don't have related content!", Toast.LENGTH_LONG).show()
        return
    }

    if (!url.contains("http://www.") &&
        !url.contains("https://www.")) {
        webUrl = "https://www.$url"
    }

    openURL.data = Uri.parse(webUrl)
    this.startActivity(openURL)
}

fun AppCompatActivity.shareLocation(locations: Locations) {
    val sendIntent = Intent()
    sendIntent.action = Intent.ACTION_SEND
    sendIntent.putExtra(
        Intent.EXTRA_TEXT,
        locations.location_details?.name + " \n" + locations.location_details?.description
    )
    sendIntent.type = "text/plain"

    val shareIntent = Intent.createChooser(sendIntent, locations.location_details?.name)
    startActivity(shareIntent)
}

fun AppCompatActivity.shareVoucher(voucher: Voucher) {
    val sendIntent = Intent()
    sendIntent.action = Intent.ACTION_SEND
    sendIntent.putExtra(
        Intent.EXTRA_TEXT,
        voucher.name + " Expire Date:" + voucher.expired_at?.formatDate()
    )
    sendIntent.type = "text/plain"

    val shareIntent = Intent.createChooser(sendIntent, voucher.name)
    startActivity(shareIntent)
}

fun AppCompatActivity.sendEmail(locations: Locations) {
    if (locations.location_details?.email == null) {
        Toast.makeText(this, "We don't have related content!", Toast.LENGTH_LONG).show()
        return
    }

    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:")
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(locations.location_details?.email))
    intent.putExtra(Intent.EXTRA_SUBJECT, locations.location_details?.name)

    startActivity(Intent.createChooser(intent, "Email via..."))
}
