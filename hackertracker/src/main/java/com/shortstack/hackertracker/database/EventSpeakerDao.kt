package com.shortstack.hackertracker.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.Speaker

/**
 * Created by Chris on 7/13/2018.
 */
@Dao
interface EventSpeakerDao {

    @Query("SELECT * FROM speaker INNER JOIN event_speaker_join ON speaker.id=event_speaker_join.speaker WHERE :event=event_speaker_join.event")
    fun getSpeakersForEvent(event: Int): List<Speaker>

    @Query("SELECT * FROM event INNER JOIN event_speaker_join ON event_speaker_join.speaker=:speaker LIMIT 3")
    fun getEventsForSpeaker(speaker: Int): List<DatabaseEvent>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(join: EventSpeakerJoin): Long

}