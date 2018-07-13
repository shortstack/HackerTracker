package com.shortstack.hackertracker.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shortstack.hackertracker.models.Speaker

/**
 * Created by Chris on 7/13/2018.
 */
@Dao
interface EventSpeakerDao {


    @Query("SELECT * FROM speaker INNER JOIN event_speaker_join ON speaker.id=event_speaker_join.speaker WHERE :event=event_speaker_join.event")
    fun getSpeakersForEvent(event: Int): List<Speaker>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(join: EventSpeakerJoin)

}