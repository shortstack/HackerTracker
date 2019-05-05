package com.shortstack.hackertracker

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.transition.Fade
import com.shortstack.hackertracker.models.firebase.*
import com.shortstack.hackertracker.models.local.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun Date.isToday(): Boolean {
    val current = Calendar.getInstance().now()

    val cal = Calendar.getInstance()
    cal.time = this

    return cal.get(Calendar.YEAR) == current.get(Calendar.YEAR)
            && cal.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR)
}

fun Date.isTomorrow(): Boolean {
    val cal1 = Calendar.getInstance().now()
    cal1.roll(Calendar.DAY_OF_YEAR, true)

    val cal2 = Calendar.getInstance()
    cal2.time = this

    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun Date.isSoonish(SOON_DAYS_AMOUNT: Int): Boolean {
    val cal1 = Calendar.getInstance().now()

    val time = cal1.time.time

    val cal2 = Calendar.getInstance()
    cal2.time = this

    val time2 = cal2.time.time

    val daysInMilliSeconds = SOON_DAYS_AMOUNT * 1000 * 60 * 60 * 24
    return Math.abs(time - time2) < daysInMilliSeconds
}

fun Date.getDateDifference(date: Date, timeUnit: TimeUnit): Long {
    return timeUnit.convert(date.time - this.time, TimeUnit.MILLISECONDS)
}


fun Date.now(): Date {
//    if (BuildConfig.DEBUG) {
//        return Calendar.getInstance().now().time
//    }

    return this
}

fun Calendar.now(): Calendar {
//    if (BuildConfig.DEBUG) {
//        val today = Date()
//        today.time = Constants.DEBUG_FORCE_TIME_DATE
//
//        val calendar = Calendar.getInstance()
//        calendar.time = today
//
//        this.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
//        this.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR))
//
//    } else {
    this.time = Date()
//    }

    return this
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction { add(frameId, fragment) }
}


fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int, hasAnimation: Boolean = false, backStack: Boolean = true) {
    supportFragmentManager.inTransaction {

        if (hasAnimation) {

            val fadeDuration = 300L


            fragment.apply {
                enterTransition = Fade().apply {
                    duration = fadeDuration
                }


                returnTransition = Fade().apply {
                    duration = fadeDuration
                }
            }
        }

        val transaction = replace(frameId, fragment)
        if (backStack) {
            transaction.addToBackStack(null)
        }
        return@inTransaction transaction
    }
}

fun FirebaseConference.toConference() = Conference(
        id,
        name,
        description,
        code,
        maps
)

fun FirebaseType.toType() = Type(
        id,
        name,
        conference,
        color
)

fun FirebaseLocation.toLocation() = Location(
        name,
        conference
)

fun FirebaseEvent.toEvent() = Event(
        id,
        conference,
        title,
        description,
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(begin),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(end),
        link,
        updated,
        speakers.map { it.toSpeaker() },
        type.toType(),
        location.toLocation()
)

fun FirebaseSpeaker.toSpeaker() = Speaker(
        name,
        description,
        link,
        twitter,
        title
)

fun FirebaseVendor.toVendor() = Vendor(
        id,
        name,
        description,
        link,
        partner
)