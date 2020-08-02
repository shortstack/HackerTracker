package com.shortstack.hackertracker

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Action(val url: String) : Parcelable {

    val res: Int
        get() {
            if (url.contains("discord.com") || url.contains("discordapp.com"))
                return R.drawable.ic_discord_logo_white
            if (url.contains("twitter.com"))
                return R.drawable.ic_twitter

            if (url.contains("forum.defcon.org"))
                return R.drawable.ic_baseline_forum_24

            if (url.contains("twitch.tv"))
                return R.drawable.ic_glitch_white_rgb

            if (url.contains("youtube.com"))
                return R.drawable.youtube_social_icon_red

            if (url.contains("soundcloud.com"))
                return R.drawable.soundcloud

            if (url.contains("facebook.com"))
                return R.drawable.logo_facebook

            return R.drawable.browser
        }

    val label: String
        get() {
            if (url.contains("discord.com") || url.contains("discordapp.com"))
                return "discordapp.com"

            if (url.contains("twitter.com"))
                return "@" + url.substring(url.lastIndexOf("/") + 1)

            if (url.contains("forum.defcon.org"))
                return "forum.defcon.org"

            if (url.contains("twitch.tv"))
                return "twitch.tv/" + url.substring(url.lastIndexOf("/") + 1)

            if (url.contains("youtube.com"))
                return "youtube.com"

            if (url.contains("soundcloud.com"))
                return url.substring(url.lastIndexOf("/") + 1)

            if (url.contains("facebook.com"))
                return url.substring(url.lastIndexOf("/") + 1)

            return url
        }
}