package com.shortstack.hackertracker.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.shortstack.hackertracker.models.Type
import io.reactivex.Single

/**
 * Created by Chris on 3/31/2018.
 */
@Dao
interface TypeDao {

    @Query("SELECT * FROM type")
    fun get(): List<Type>

    @Query("SELECT * FROM type")
    fun getTypes(): Single<List<Type>>

    @Query("SELECT * FROM type WHERE con = :con")
    fun getTypes(con: String): LiveData<List<Type>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(type: List<Type>)

    @Query("SELECT * FROM type WHERE type = :event")
    fun getTypeForEvent(event: String): Single<Type>

    @Update
    fun update(type: Type): Int
}