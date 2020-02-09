package com.androidapp.navweiandroidv2.util.ext

import java.text.SimpleDateFormat

/**
 * Created by S.Nur Uysal on 2019-12-05.
 */
fun String.formatDate(): String {

    var dateFormatI = "yyyy-MM-dd'T'HH:mm:ss"
    var dateFormatO = "dd MMMM yyyy"

    var formatInput = SimpleDateFormat(dateFormatI)
    var formatOutput = SimpleDateFormat(dateFormatO)

    var date = formatInput.parse(this)
    return formatOutput.format(date)
}

