package com.shortstack.hackertracker.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.models.Type
import java.util.*

/**
 * Created by Chris on 3/31/2018.
 */
@Dao
interface EventDao {

    companion object {
        private const val LIMIT = 20
        private const val TYPE_CONTEST = 7
        private const val TYPE_WORKSHOP = 3
    }

    // Create
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(item: Event): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(events: List<Event>): List<Long>

    // Read
    @Query("SELECT * FROM event where conference = :conference AND `end` > :date AND type IN (:types) ORDER BY `begin` ASC")
    fun getSchedule(conference: String, date: Date, types: List<Int>): LiveData<List<DatabaseEvent>>

    @Query("SELECT * FROM event where conference = :conference AND `end` > :date AND type IN (:types) AND isBookmarked = :isBookmarked ORDER BY `begin` ASC")
    fun getSchedule(conference: String, date: Date, types: List<Int>, isBookmarked: Boolean): LiveData<List<DatabaseEvent>>

    @Query("SELECT * FROM event where conference = :conference AND `end` > :date ORDER BY `begin` ASC")
    fun getSchedule(conference: String, date: Date): LiveData<List<DatabaseEvent>>

    @Query("SELECT * FROM event WHERE `id` = :id")
    fun getEventById(id: Int): DatabaseEvent?

    @Query("SELECT * FROM event INNER JOIN location ON location.id = event.location WHERE event.conference = :conference AND ( title LIKE :text OR location.name LIKE :text )")
    fun getEventByText(conference: String, text: String): List<DatabaseEvent>

    @Query("SELECT * FROM event WHERE type IN (:types) LIMIT 3")
    fun getEventByType(types: List<Int>): List<DatabaseEvent>

    @Query("SELECT * FROM event WHERE conference = :conference ORDER BY updatedAt DESC LIMIT $LIMIT")
    fun getRecentlyUpdated(conference: String): LiveData<List<DatabaseEvent>>

    @Query("SELECT COUNT(*) FROM event where updatedAt > :updatedAt")
    fun getUpdatedCount(updatedAt: Date?): Int

    @Query("SELECT * FROM event where conference = :conference AND isBookmarked = 1 AND updatedAt > :updatedAt")
    fun getUpdatedBookmarks(conference: String, updatedAt: Date): List<DatabaseEvent>

    @Query("SELECT * FROM event where conference = :conference AND isBookmarked = 1")
    fun getUpdatedBookmarks(conference: String): List<DatabaseEvent>

    @Query("SELECT * FROM event where conference = :conference AND type = $TYPE_CONTEST AND `end` > :date ORDER BY `begin` ASC")
    fun getContests(conference: String, date: Date): LiveData<List<DatabaseEvent>>

    @Query("SELECT * FROM event where conference = :conference AND type = $TYPE_WORKSHOP  AND `end` > :date ORDER BY `begin` ASC")
    fun getWorkshops(conference: String, date: Date): LiveData<List<DatabaseEvent>>


    // Update
    @Query("UPDATE event SET type = :type, title = :title, description = :description, `begin` = :begin, `end` = :end, updatedAt = :updatedAt, location = :location, url = :url, conference = :conference WHERE id = :id")
    fun updateEvent(id: Int, type: Int, title: String, description: String, begin: Date, end: Date, updatedAt: Date?, location: Int, url: String?, conference: String)

    @Query("UPDATE event SET isBookmarked = :isBookmarked WHERE `id` = :id")
    fun updateBookmark(id: Int, isBookmarked: Boolean)

    @Transaction
    fun upsert(event: Event) {
        val id = insert(event)
        if (id == -1L) {
            updateEvent(event.id, event.type, event.title, event.description, event.begin, event.end, event.updatedAt, event.location, event.url, event.conference)
        }
    }


    // Delete

}