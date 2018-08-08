package com.shortstack.hackertracker.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.shortstack.hackertracker.models.Speaker
import io.reactivex.Flowable

/**
 * Created by Chris on 3/31/2018.
 */
@Dao
interface SpeakerDao {

    @Query("SELECT * FROM speaker WHERE id = :id")
    fun getSpeaker(id: Int): Flowable<Speaker>

    @Query("SELECT * FROM speaker")
    fun getAll(): Flowable<List<Speaker>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<Speaker>): List<Long>

    @Delete
    fun delete(user: Speaker)

    @Query("SELECT * FROM speaker where name LIKE :text")
    fun findSpeakerByText(text: String): LiveData<List<Speaker>>
}