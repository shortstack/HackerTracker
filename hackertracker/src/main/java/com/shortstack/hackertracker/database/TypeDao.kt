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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(type: Type): Long

    @Query("SELECT * FROM type")
    fun get(): List<Type>

    @Query("SELECT * FROM type")
    fun getTypes(): Single<List<Type>>

    @Query("SELECT * FROM type WHERE conference = :conference")
    fun getTypes(conference: String): LiveData<List<Type>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(type: List<Type>): List<Long>

    @Query("SELECT * FROM type WHERE name = :event")
    fun getTypeForEvent(event: String): Single<Type>

    @Query("UPDATE type SET name = :name, color = :color, conference = :conference WHERE id = :id")
    fun update(id: Int, name: String, color: String, conference: String): Int

    @Query("UPDATE type SET isSelected = :isSelected WHERE id = :id")
    fun updateSelected(id: Int, isSelected: Boolean): Int

    @Transaction
    fun upsert(types: List<Type>) {
        types.forEach {
            val id = insert(it)
            if (id == -1L) {
                update(it.id, it.name, it.color, it.conference)
            }
        }
    }

    @Transaction
    fun upsert(type: Type) {
        val id = insert(type)
        if (id == -1L) {
            update(type.id, type.name, type.color, type.conference)
        }
    }
}