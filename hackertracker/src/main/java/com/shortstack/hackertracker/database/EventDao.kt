package com.shortstack.hackertracker.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.network.FullResponse
import com.shortstack.hackertracker.network.SyncResponse
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import org.intellij.lang.annotations.Flow
import java.util.*

/**
 * Created by Chris on 3/31/2018.
 */
@Dao
interface EventDao {

    companion object {
        private const val LIMIT = 20
    }

    @Query("SELECT * FROM event where conference = :conference AND `end` > :date AND type IN (:types) ORDER BY `begin` ASC")
    fun getSchedule(conference: String, date: Date, types: List<Int>): LiveData<List<DatabaseEvent>>

    @Query("SELECT * FROM event where conference = :conference AND `end` > :date ORDER BY `begin` ASC")
    fun getSchedule(conference: String, date: Date): LiveData<List<DatabaseEvent>>

//    @Query("SELECT * FROM event WHERE conference = :conference ORDER BY `begin` ASC LIMIT $LIMIT OFFSET :page")
//    fun getSchedule(conference: String, page: Int): LiveData<List<Event>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(events: List<Event>)

    @Update
    fun update(event: Event)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(events: List<Event>): Int

    @Query("SELECT * FROM event WHERE `id` = :id")
    fun getEventById(id: Int): Event?

    @Query("SELECT * FROM event WHERE title LIKE :text")
    fun findByText(text: String): LiveData<List<DatabaseEvent>>

    @Query("SELECT * FROM event WHERE conference = :conference ORDER BY updatedAt DESC LIMIT $LIMIT")
    fun getRecentlyUpdated(conference: String): LiveData<List<DatabaseEvent>>

    @Query("UPDATE event SET isBookmarked = :isBookmarked WHERE `id` = :id")
    fun updateBookmark(id: Int, isBookmarked: Boolean)
}