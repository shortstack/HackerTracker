package com.shortstack.hackertracker.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.models.Type
import io.reactivex.Flowable

/**
 * Created by Chris on 3/31/2018.
 */
@Dao
interface TypeDao {

    @Query("SELECT * FROM type")
    fun getTypes(): Flowable<List<Type>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(type: List<Type>)

    @Query("SELECT * FROM type WHERE type = :event")
    fun getTypeForEvent(event: String): Flowable<Type>
}