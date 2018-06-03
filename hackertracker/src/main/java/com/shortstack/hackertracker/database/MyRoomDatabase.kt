package com.shortstack.hackertracker.database

import android.arch.lifecycle.MutableLiveData
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.fromFile
import com.shortstack.hackertracker.models.*
import com.shortstack.hackertracker.models.response.Speakers
import com.shortstack.hackertracker.models.response.Types
import com.shortstack.hackertracker.models.response.Vendors
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import javax.inject.Inject

/**
 * Created by Chris on 3/31/2018.
 */
@Database(entities = [(Conference::class), (Event::class), (Type::class), (Vendor::class), (Speaker::class), (FAQ::class)], version = 1)
@TypeConverters(value = [(Converters::class)])
abstract class MyRoomDatabase : RoomDatabase() {

    abstract fun conferenceDao(): ConferenceDao

    abstract fun eventDao(): EventDao

    abstract fun typeDao(): TypeDao

    abstract fun speakerDao(): SpeakerDao

    abstract fun vendorDao(): VendorDao

    abstract fun faqDao(): FAQDao

    @Inject
    lateinit var gson: Gson

    fun init() {
        App.application.myComponent.inject(this)

        val conferences = gson.fromFile<Conferences>(CONFERENCES_FILE, root = "conferences")

        val incorrectDateStamps = mutableListOf<String>()

        conferences.let {
            it.conferences.first().isSelected = true
            conferenceDao().insertAll(it.conferences)
        }

        conferences.conferences.forEach {
            val database = it.directory

            Logger.e("Loading $database")

            try {
                // Types
                gson.fromFile<Types>(TYPES_FILE, root = database).let {
                    it.types.forEach { it.con = database }
                    typeDao().insertAll(it.types)
                }
            } catch (ex: JsonSyntaxException) {
                incorrectDateStamps.add(ex.message!!)
            }

            try {
                // Schedule
                gson.fromFile<Events>(SCHEDULE_FILE, root = database).let {
                    it.events.forEach { it.con = database }
                    eventDao().insertAll(it.events)
                }
            } catch (ex: JsonSyntaxException) {
                incorrectDateStamps.add(ex.message!!)
            }

            try {
                // Vendors
                gson.fromFile<Vendors>(VENDORS_FILE, root = database).let {
                    it.vendors.forEach { it.con = database }
                    vendorDao().insertAll(it.vendors)
                }
            } catch (ex: JsonSyntaxException) {
                incorrectDateStamps.add(ex.message!!)
            }

            try {
                // Speakers
                gson.fromFile<Speakers>(SPEAKERS_FILE, root = database).let {
                    it.speakers.forEach { it.con = database }
                    speakerDao().insertAll(it.speakers)
                }
            } catch (ex: JsonSyntaxException) {
                incorrectDateStamps.add(ex.message!!)
            }

            try {
                gson.fromFile<FAQs>(FAQ_FILE, root = database).let {
                    faqDao().insertAll(it.faqs)
                }
            } catch (ex: JsonSyntaxException) {
                incorrectDateStamps.add(ex.message!!)
            }
        }

        if (incorrectDateStamps.isNotEmpty()) {
            val array = JSONArray()
            incorrectDateStamps.forEach {
                array.put(it)
            }

            Logger.e(array.toString())
        }

    }

    companion object {

        @Volatile
        private var INSTANCE: MyRoomDatabase? = null

        fun getInstance(context: Context, conferenceLiveData: MutableLiveData<Conference>): MyRoomDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context, conferenceLiveData).also { INSTANCE = it }
                }


        fun buildDatabase(context: Context, conferenceLiveData: MutableLiveData<Conference>): MyRoomDatabase {
            Logger.d("Creating database! " + (System.currentTimeMillis() - App.application.timeToLaunch))
            val database = Room.databaseBuilder(context, MyRoomDatabase::class.java, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Logger.d("Database onCreate! " + (System.currentTimeMillis() - App.application.timeToLaunch))

                            Single.fromCallable {
                                val instance = getInstance(context, conferenceLiveData)
                                instance.init()
                                conferenceLiveData.postValue(instance.conferenceDao().getCurrentCon())
                            }.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe()
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            Logger.d("Database onOpen! " + (System.currentTimeMillis() - App.application.timeToLaunch))

                            Single.fromCallable {
                                val instance = getInstance(context, conferenceLiveData)
                                conferenceLiveData.postValue(instance.conferenceDao().getCurrentCon())
                            }.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe()
                        }
                    }).build()

            INSTANCE = database

            return database

        }

        const val DATABASE_NAME = "database"

        private const val CONFERENCES_FILE = "conferences.json"

        private const val SCHEDULE_FILE = "schedule-full.json"
        private const val TYPES_FILE = "event_type.json"
        private const val SPEAKERS_FILE = "speakers.json"
        private const val VENDORS_FILE = "vendors.json"
        private const val FAQ_FILE = "faq.json"
    }
}