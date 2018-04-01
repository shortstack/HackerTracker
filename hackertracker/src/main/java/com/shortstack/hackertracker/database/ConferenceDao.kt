package com.shortstack.hackertracker.database

import android.arch.persistence.room.*
import com.shortstack.hackertracker.models.Conference
import io.reactivex.Flowable

/**
 * Created by Chris on 3/31/2018.
 */
@Dao
interface ConferenceDao {

    @Query("DELETE FROM conference")
    fun deleteAll()

    @Query("SELECT * FROM conference")
    fun getAll(): Flowable<List<Conference>>

    @Query("SELECT * FROM conference")
    fun get(): List<Conference>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(conferences: List<Conference>)

    @Query("SELECT * FROM conference WHERE isSelected = 1")
    fun getCurrentCon(): Conference

    @Update
    fun update(conference: Conference)


}