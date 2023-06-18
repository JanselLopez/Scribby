package com.jansellopez.scribby.core

import java.util.Calendar



fun getFormattedDate(cal: Calendar?):String{
    cal?.apply {
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)
        val now = Calendar.getInstance()
        val isToday = (day == now.get(Calendar.DAY_OF_MONTH) &&
                month == now.get(Calendar.MONTH) &&
                year == now.get(Calendar.YEAR))

        if(isToday)
            return "${get(Calendar.HOUR)}:${if(get(Calendar.MINUTE)<10)"0" else ""}${get(Calendar.MINUTE)} ${if(get(
                    Calendar.AM_PM)==1) "p.m." else "a.m."}"
        return "${get(Calendar.DAY_OF_MONTH)}/${get(Calendar.MONTH) + 1}/${get(Calendar.YEAR)}"
    }
    return "invalid date"
}