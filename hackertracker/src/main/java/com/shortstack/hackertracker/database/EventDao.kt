package com.shortstack.hackertracker.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
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

    @Query("SELECT * FROM event WHERE con = :con ORDER BY `begin` ASC")
    fun getSchedule(con: String): LiveData<List<Event>>

    @Query("SELECT * FROM event WHERE con = :con ORDER BY `begin` ASC LIMIT $LIMIT OFFSET :page")
    fun getSchedule(con: String, page: Int): LiveData<List<Event>>

    @Query("SELECT * FROM event ORDER BY `begin` ASC LIMIT $LIMIT")
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
    fun findByText(text: String): LiveData<List<Event>>

    @Query("SELECT * FROM event ORDER BY updatedAt DESC LIMIT $LIMIT")
    fun getRecentlyUpdated(): Single<List<Event>>

    @Query("SELECT * FROM event ORDER BY updatedAt DESC LIMIT $LIMIT")
    fun getUIThreadRecentlyUpdated(): List<Event>

    @Query("SELECT * FROM event WHERE con = :con ORDER BY updatedAt DESC LIMIT $LIMIT")
    fun getRecentlyUpdated(con: String): LiveData<List<Event>>

    @Query("SELECT * FROM event WHERE con = :con AND `end` > :end  ORDER BY `begin` ASC")
    fun getEventTypes(con: String, end: Date): Flowable<List<DatabaseEvent>>
}