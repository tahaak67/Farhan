package ly.com.tahaben.farhan.db

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.runBlocking
import ly.com.tahaben.domain.preferences.Preferences
import timber.log.Timber

/* Created by Taha https://github.com/tahaak67/ at 15/9/2024 */

// TODO: Remove on v 1.0
class DatabaseCombineHelper(
    private val context: Context,
    private val farhanDatabase: FarhanDatabase,
    private val preferences: Preferences) {

    suspend fun combineDatabases(): Boolean{
        return try {

            Timber.d("mig db = True")
            val dbList = context.databaseList().toList()
            dbList.forEach { dbName ->
                when(dbName){
                    "notifications_db" ->{
                        Timber.d("notification db here!!")
                        val notificationDb = Room
                            .databaseBuilder(context, NotificationDatabase::class.java, "notifications_db")
                            .build()
                        Timber.d("db created")

                            Timber.d("now blocking!")
                            val notifications = notificationDb.dao.getNotificationsAsList()
                            Timber.d("found ${notifications.size} notifications")
                            Timber.d("inserting to new db now")
                            farhanDatabase.runInTransaction{
                                runBlocking {
                                    notifications.forEach {
                                        farhanDatabase.notificationDao.insertNotificationItem(it)
                                    }
                                }
                            }
                            Timber.d("should be done :)")

                        context.deleteDatabase("notifications_db")
                        context.deleteDatabase("notifications_db-wal")
                        context.deleteDatabase("notifications_db-shm")
                    }
                    "usage_db" -> {
                        Timber.d("usage db here!!")
                        val usageDb = Room
                            .databaseBuilder(context, UsageDatabase::class.java, "usage_db")
                            .build()
                        Timber.d("db created")
                        val usageEvents = usageDb.dao.getAllUsageEvents()
                        Timber.d("found ${usageEvents.size} usage events")
                        Timber.d("inserting to new db now")
                        farhanDatabase.runInTransaction{
                            runBlocking {
                                usageEvents.forEach {
                                    farhanDatabase.usageDao.insertUsageItem(it)
                                }
                            }
                        }
                        val dayLastUpdatedList = usageDb.dao.getAllDayLastUpdatedEntities()
                        Timber.d("found ${dayLastUpdatedList.size} days last updated")
                        Timber.d("inserting to db now")
                        farhanDatabase.runInTransaction {
                            runBlocking {
                                dayLastUpdatedList.forEach {
                                    farhanDatabase.usageDao.setLastDbUpdateTimeForDay(it)
                                }
                            }
                        }

                        Timber.d("Usage should be done :)")
                        context.deleteDatabase("usage_db")
                        context.deleteDatabase("usage_db-wal")
                        context.deleteDatabase("usage_db-shm")
                    }
                    else -> Unit
                }
            }
            preferences.saveShouldCombineDb(false)
            true
        }catch (e: Exception){

            false
        }
    }
}