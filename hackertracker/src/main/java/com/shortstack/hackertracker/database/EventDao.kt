package com.shortstack.hackertracker.database

import android.arch.persistence.room.*
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.Event
import io.reactivex.Flowable

/**
 * Created by Chris on 3/31/2018.
 */
@Dao
interface EventDao {

    @Query("SELECT * FROM event ORDER BY `begin` ASC")
    fun getFullSchedule(): Flowable<List<Event>>

    @Query("SELECT * FROM event WHERE con = :con ORDER BY `begin` ASC")
    fun getFullSchedule(con: String): Flowable<List<Event>>

    @Query("SELECT * FROM event ORDER BY `begin` ASC")
    fun getUIThreadSchedule(): List<Event>


    @Query("SELECT * FROM event WHERE type LIKE :type ")
    fun getFilteredSchedule(type: String): Flowable<List<Event>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(events: List<Event>)

    @Update
    fun update(event: Event)

    @Update
    fun update(events: List<Event>)

    @Query("SELECT * FROM event WHERE `index` = :id")
    fun getEventById(id: Int): Flowable<Event>

    @Query("SELECT * FROM event WHERE title LIKE :text")
    fun findByText(text: String): Flowable<List<Event>>

    @Query("SELECT * FROM event ORDER BY updatedAt DESC LIMIT 20")
    fun getRecentlyUpdated(): Flowable<List<Event>>

    @Query("SELECT * FROM event WHERE con = :con ORDER BY updatedAt DESC LIMIT 20")
    fun getRecentlyUpdated(con: String): Flowable<List<Event>>


    @Query("SELECT * FROM event WHERE con = :con")
    fun getEventTypes(con: String): Flowable<List<DatabaseEvent>>
}