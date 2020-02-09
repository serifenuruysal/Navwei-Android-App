package com.androidapp.navweiandroidv2.util.helper

import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedInputStream
import java.io.BufferedReader

/**
 * Created by S.Nur Uysal on 2019-12-06.
 */

fun AppCompatActivity.getSvgContent(fileName: String): String? {

    val inputReader = BufferedInputStream(this.resources.assets.open(fileName))
    return inputReader.bufferedReader().use(BufferedReader::readText)

}