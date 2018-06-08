package com.shortstack.hackertracker.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.shortstack.hackertracker.models.Conference
import com.shortstack.hackertracker.models.DatabaseConference
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by Chris on 3/31/2018.
 */
@Dao
interface ConferenceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(conference: Conference): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(conferences: List<Conference>)

    @Update
    fun update(conference: Conference): Int

    @Update
    fun update(list: List<Conference>): Int

    @Query("SELECT * FROM conference")
    fun getAll(): LiveData<List<Conference>>

    @Query("SELECT * FROM conference")
    fun get(): List<DatabaseConference>

    @Query("DELETE FROM conference")
    fun deleteAll()

    @Query("SELECT * FROM conference WHERE isSelected = 1")
    fun getCurrentCon(): DatabaseConference?

    @Transaction
    fun upsert(conference: Conference) {
        val id = insert(conference)
        if (id == -1L) {
            update(conference)
        }
    }
}