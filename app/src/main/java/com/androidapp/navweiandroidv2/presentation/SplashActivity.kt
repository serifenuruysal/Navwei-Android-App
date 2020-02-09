package com.androidapp.navweiandroidv2.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.parentlocation.HomeActivity


/**
 * Created by S.Nur Uysal on 2019-11-06.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Make sure this is before calling super.onCreate
        setTheme(R.style.SplashTheme)
        super.onCreate(savedInstanceState)

        routeToAppropriatePage()
    }

    private fun routeToAppropriatePage() {

        startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
        finish()

    }

}