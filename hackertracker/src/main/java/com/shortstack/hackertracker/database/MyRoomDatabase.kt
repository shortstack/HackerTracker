package com.shortstack.hackertracker.database

import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Constants.CONFERENCES_FILE
import com.shortstack.hackertracker.Constants.FAQ_FILE
import com.shortstack.hackertracker.Constants.LOCATIONS_FILE
import com.shortstack.hackertracker.Constants.SCHEDULE_FILE
import com.shortstack.hackertracker.Constants.SPEAKERS_FILE
import com.shortstack.hackertracker.Constants.TYPES_FILE
import com.shortstack.hackertracker.Constants.VENDORS_FILE
import com.shortstack.hackertracker.fromFile
import com.shortstack.hackertracker.models.*
import com.shortstack.hackertracker.models.response.Speakers
import com.shortstack.hackertracker.models.response.Types
import com.shortstack.hackertracker.models.response.Vendors
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.FileNotFoundException
import javax.inject.Inject

/**
 * Created by Chris on 3/31/2018.
 */
@Database(entities = [(Conference::class), (Event::class), (Type::class), (Vendor::class),
    (Speaker::class), (FAQ::class), (Location::class), (EventSpeakerJoin::class)], version = 1)
@TypeConverters(value = [(Converters::class)])
abstract class MyRoomDatabase : RoomDatabase() {

    abstract fun conferenceDao(): ConferenceDao

    abstract fun eventDao(): EventDao

    abstract fun typeDao(): TypeDao

    abstract fun speakerDao(): SpeakerDao

    abstract fun vendorDao(): VendorDao

    abstract fun faqDao(): FAQDao

    abstract fun locationDao(): LocationDao

    abstract fun eventSpeakerDao(): EventSpeakerDao

    @Inject
    lateinit var gson: Gson

    fun init() {
        App.application.component.inject(this)

        val conferences = gson.fromFile<Conferences>(CONFERENCES_FILE, root = null)

        conferences.let {
            val first = it.conferences.first()
            first.isSelected = true
            conferenceDao().insertAll(it.conferences)
        }

        conferences.conferences.forEach {
            val database = it.code

            Logger.d("Loading $database")

            try {
                // Types
                gson.fromFile<Types>(TYPES_FILE, root = database).let {
                    typeDao().insertAll(it.types)
                }
            } catch (ex: JsonSyntaxException) {
                Logger.e("Could not open $TYPES_FILE. ${ex.message}")
            } catch (ex: FileNotFoundException) {
                Logger.e("Could not find file $TYPES_FILE.")
            }

            try {
                // Types
                gson.fromFile<Locations>(LOCATIONS_FILE, root = database).let {
                    locationDao().insertAll(it.locations)
                }
            } catch (ex: JsonSyntaxException) {
                Logger.e("Could not open $TYPES_FILE. ${ex.message}")
            } catch (ex: FileNotFoundException) {
                Logger.e("Could not find file $TYPES_FILE.")
            }

            try {
                // Speakers
                gson.fromFile<Speakers>(SPEAKERS_FILE, root = database).let {
                    speakerDao().insertAll(it.speakers)
                }
            } catch (ex: JsonSyntaxException) {
                Logger.e("Could not open $SPEAKERS_FILE. ${ex.message}")
            } catch (ex: FileNotFoundException) {
                Logger.e("Could not find file $SPEAKERS_FILE.")
            }

            try {
                // Schedule
                gson.fromFile<Events>(SCHEDULE_FILE, root = database).let {
                    eventDao().insertAll(it.events)

                    it.events.forEach { event ->
                        event.speakers.forEach {
                            val join = EventSpeakerJoin(event.id, it)
                            eventSpeakerDao().insert(join)
                        }
                    }
                }
            } catch (ex: JsonSyntaxException) {
                Logger.e("Could not open $SCHEDULE_FILE. ${ex.message}")
            } catch (ex: FileNotFoundException) {
                Logger.e("Could not find file $SCHEDULE_FILE.")
            }

            try {
                // Vendors
                gson.fromFile<Vendors>(VENDORS_FILE, root = database).let {
                    vendorDao().insertAll(it.vendors)
                }
            } catch (ex: JsonSyntaxException) {
                Logger.e("Could not open $VENDORS_FILE. ${ex.message}")
            } catch (ex: FileNotFoundException) {
                Logger.e("Could not find file $VENDORS_FILE.")
            }

            try {
                gson.fromFile<FAQs>(FAQ_FILE, root = database).let {
                    faqDao().insertAll(it.faqs)
                }
            } catch (ex: JsonSyntaxException) {
                Logger.e("Could not open $FAQ_FILE. ${ex.message}")
            } catch (ex: FileNotFoundException) {
                Logger.e("Could not find file $FAQ_FILE.")
            }
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: MyRoomDatabase? = null

        fun getInstance(context: Context, conferenceLiveData: MutableLiveData<DatabaseConference>): MyRoomDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context, conferenceLiveData).also { INSTANCE = it }
                }


        fun buildDatabase(context: Context, conferenceLiveData: MutableLiveData<DatabaseConference>): MyRoomDatabase {
            Logger.d("Creating database! " + (System.currentTimeMillis() - App.application.timeToLaunch))
            val database = Room.databaseBuilder(context, MyRoomDatabase::class.java, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Logger.d("Database onCreate! " + (System.currentTimeMillis() - App.application.timeToLaunch))

//                            Single.fromCallable {
//                                val instance = getInstance(context, conferenceLiveData)
//                                instance.init()
//                                conferenceLiveData.postValue(instance.conferenceDao().getCurrentCon())
//                            }.subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe()
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            Logger.d("Database onOpen! " + (System.currentTimeMillis() - App.application.timeToLaunch))

                            Single.fromCallable {
                                val instance = getInstance(context, conferenceLiveData)

                                instance.clearAllTables()

                                instance.init()


                                val currentCon = instance.conferenceDao().getCurrentCon()
                                Logger.d("Setting current conference $currentCon")
                                conferenceLiveData.postValue(currentCon)
                            }.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe()
                        }
                    }).build()

            INSTANCE = database

            return database

        }

        private const val DATABASE_NAME = "database"

    }
}