package com.shortstack.hackertracker.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.Event
import java.util.*

/**
 * Created by Chris on 3/31/2018.
 */
@Dao
interface EventDao {

    companion object {
        private const val LIMIT = 20
    }

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(events: List<Event>): List<Long>

    // Read
    @Query("SELECT * FROM event where conference = :conference AND `end` > :date AND type IN (:types) ORDER BY `begin` ASC")
    fun getSchedule(conference: String, date: Date, types: List<Int>): LiveData<List<DatabaseEvent>>

    @Query("SELECT * FROM event where conference = :conference AND `end` > :date ORDER BY `begin` ASC")
    fun getSchedule(conference: String, date: Date): LiveData<List<DatabaseEvent>>

    @Query("SELECT * FROM event WHERE `id` = :id")
    fun getEventById(id: Int): Event?

    @Query("SELECT * FROM event WHERE title LIKE :text")
    fun getEventByText(text: String): LiveData<List<DatabaseEvent>>

    @Query("SELECT * FROM event WHERE conference = :conference ORDER BY updatedAt DESC LIMIT $LIMIT")
    fun getRecentlyUpdated(conference: String): LiveData<List<DatabaseEvent>>

    @Query("SELECT COUNT(*) FROM event where updatedAt > :updatedAt")
    fun getUpdatedCount(updatedAt: Date?): Int

    @Query("SELECT * FROM event where conference = :conference AND isBookmarked = 1 AND updatedAt > :updatedAt")
    fun getUpdatedBookmarks(conference: String, updatedAt: Date): List<DatabaseEvent>

    @Query("SELECT * FROM event where conference = :conference AND isBookmarked = 1")
    fun getUpdatedBookmarks(conference: String): List<DatabaseEvent>

    // Update
    @Query("UPDATE event SET type = :type, title = :title, description = :description, `begin` = :begin, `end` = :end, updatedAt = :updatedAt, location = :location, url = :url, conference = :conference WHERE id = :id")
    fun updateEvent(id: Int, type: Int, title: String, description: String, begin: Date, end: Date, updatedAt: Date?, location: Int, url: String?, conference: String)

    @Query("UPDATE event SET isBookmarked = :isBookmarked WHERE `id` = :id")
    fun updateBookmark(id: Int, isBookmarked: Boolean)

    // Delete

}