package com.shortstack.hackertracker.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.fromJsonFile
import com.shortstack.hackertracker.models.*
import com.shortstack.hackertracker.models.response.Speakers
import com.shortstack.hackertracker.models.response.Types
import com.shortstack.hackertracker.models.response.Vendors
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Chris on 3/31/2018.
 */
@Database(entities = [(Event::class), (Type::class), (Vendor::class), (Speaker::class)], version = 1)
@TypeConverters(value = [(Converters::class)])
abstract class MyRoomDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    abstract fun typeDao(): TypeDao

    abstract fun speakerDao(): SpeakerDao

    abstract fun vendorDao(): VendorDao

    fun init(database: String) {
        val gson = App.application.gson

        Logger.d("Opening database $database")

//        Logger.d("Loading Types")
        // Types
        gson.fromJsonFile(TYPES_FILE, Types::class.java, root = database).let {
            typeDao().insertAll(it.types)
        }

//        Logger.d("Loading Schedule")
        // Schedule
        gson.fromJsonFile(SCHEDULE_FILE, Events::class.java, root = database).let {
            eventDao().insertAll(it.events)
        }

        Logger.d("Schedule has been loaded with " + (eventDao().getUIThreadSchedule().size) + " elements." )

//        Logger.d("Loading Vendors")
        // Vendors
        gson.fromJsonFile(VENDORS_FILE, Vendors::class.java, root = database).let {
            vendorDao().insertAll(it.vendors)
        }

//        Logger.d("Loading Speakers")
        // Speakers
        gson.fromJsonFile(SPEAKERS_FILE, Speakers::class.java, root = database).let {
            speakerDao().insertAll(it.speakers)
        }
    }


    companion object {

        @Volatile
        private var INSTANCE: MyRoomDatabase? = null

        fun getInstance(context: Context): MyRoomDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }


        fun buildDatabase(context: Context, database: String = Constants.DEFCON_DATABASE_NAME): MyRoomDatabase {

            return Room.databaseBuilder(context, MyRoomDatabase::class.java, database)
                    .allowMainThreadQueries()
                    .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Single.fromCallable {
                        getInstance(context).init(database)
                    }.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe()
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    Single.fromCallable {
                        getInstance(context).init(database)
                    }.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe()
                }
            }).build()

        }

        private const val SCHEDULE_FILE = "schedule-full.json"
        private const val TYPES_FILE = "event_type.json"
        private const val SPEAKERS_FILE = "speakers.json"
        private const val VENDORS_FILE = "vendors.json"
    }
}