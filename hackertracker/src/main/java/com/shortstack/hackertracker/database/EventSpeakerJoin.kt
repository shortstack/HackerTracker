package com.shortstack.hackertracker.database

import androidx.room.Entity
import androidx.room.ForeignKey
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.models.Speaker

/**
 * Created by Chris on 7/13/2018.
 */
@Entity(tableName = "event_speaker_join",
        primaryKeys = ["event", "speaker"],
        foreignKeys = [
            ForeignKey(entity = Event::class,
                    parentColumns = ["id"],
                    childColumns = ["event"]),
            ForeignKey(entity = Speaker::class,
                    parentColumns = ["id"],
                    childColumns = ["speaker"])
        ]
)
data class EventSpeakerJoin(
        val event: Int,
        val speaker: Int
)