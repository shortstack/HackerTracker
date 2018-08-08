package com.shortstack.hackertracker.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shortstack.hackertracker.models.Location

/**
 * Created by Chris on 7/2/2018.
 */
@Dao
interface LocationDao {

    @Query("SELECT * FROM location WHERE conference = :conference")
    fun getAll(conference: String): LiveData<List<Location>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(locations: List<Location>)

    @Query("SELECT * FROM location WHERE name LIKE :text ORDER BY name")
    fun getLocationByText(text: String): LiveData<List<Location>>

}