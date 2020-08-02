package com.shortstack.hackertracker

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.transition.Fade
import com.shortstack.hackertracker.models.firebase.*
import com.shortstack.hackertracker.models.local.*
import com.shortstack.hackertracker.utilities.MyClock
import com.shortstack.hackertracker.utilities.now
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

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

    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(
        Calendar.DAY_OF_YEAR
    )
}

fun Date.getDateDifference(date: Date, timeUnit: TimeUnit): Long {
    return timeUnit.convert(date.time - this.time, TimeUnit.MILLISECONDS)
}


fun Calendar.now(): Calendar {
    this.time = MyClock().now()
    return this
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction { add(frameId, fragment) }
}


fun AppCompatActivity.replaceFragment(
    fragment: Fragment,
    frameId: Int,
    hasAnimation: Boolean = false,
    backStack: Boolean = true
) {
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
    codeofconduct,
    code,
    maps,
    start_timestamp.toDate(),
    end_timestamp.toDate(),
    timezone
)

fun FirebaseType.toType() = Type(
    id,
    name,
    conference,
    color
)

fun FirebaseLocation.toLocation() = Location(
    name,
    hotel,
    conference
)

fun FirebaseEvent.toEvent(): Event {
    val element = type.toType()
    val links = if (conference == "DEFCON28") {
        val urls = extractUrls(description)
        urls.map { it.toAction() }
    } else {
        emptyList()
    }


    val types = if (conference.equals("DEFCON28")) {
        when {
            element.isWorkshop -> {
                listOf(Type(1, "Workshop", "DEFCON28", "#968AC8"), element)
            }
            element.isVillage -> {
                listOf(Type(45097, "Village", "DEFCON28", "#968AC8"), element)
            }
            else -> {
                listOf(element)
            }
        }
    } else {
        listOf(element)
    }

    return Event(
        id,
        conference,
        title,
        description,
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(begin),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(end),
        link,
        updated,
        speakers.map { it.toSpeaker() },
        types,
        location.toLocation(),
        links
    )
}

private fun String.toAction() = Action(this)

private fun extractUrls(text: String): List<String> {
    val containedUrls: MutableList<String> =
        ArrayList()
    val urlRegex =
        "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)"
    val pattern: Pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE)
    val urlMatcher: Matcher = pattern.matcher(text)
    while (urlMatcher.find()) {
        containedUrls.add(
            text.substring(
                urlMatcher.start(0),
                urlMatcher.end(0)
            )
        )
    }
    return containedUrls
}

fun FirebaseSpeaker.toSpeaker() = Speaker(
    id,
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

fun FirebaseArticle.toArticle() = Article(
    id,
    name,
    text
)

fun FirebaseFAQ.toFAQ() = FAQ(
    id,
    question,
    answer
)