package com.shortstack.hackertracker.database

import android.arch.persistence.room.*
import com.shortstack.hackertracker.models.Event
import io.reactivex.Flowable

/**
 * Created by Chris on 3/31/2018.
 */
@Dao
interface EventDao {

    @Query("SELECT * FROM event ORDER BY `begin` ASC")
    fun getFullSchedule(): Flowable<List<Event>>

    @Query("SELECT * FROM event WHERE type LIKE :type ")
    fun getFilteredSchedule(type: String): Flowable<List<Event>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(events: List<Event>)

    @Update
    fun update(item: Event)

}