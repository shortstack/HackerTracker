package com.shortstack.hackertracker.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.shortstack.hackertracker.models.Vendor
import io.reactivex.Flowable


/**
 * Created by Chris on 3/31/2018.
 */
@Dao
interface VendorDao {

    @Query("SELECT * FROM vendor")
    fun getAll(): LiveData<List<Vendor>>

    @Query("SELECT * FROM vendor WHERE conference = :conference")
    fun getAll(conference: String): LiveData<List<Vendor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<Vendor>) : List<Long>

    @Delete
    fun delete(user: Vendor)

}