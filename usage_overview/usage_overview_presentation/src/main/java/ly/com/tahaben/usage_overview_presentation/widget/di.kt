package ly.com.tahaben.usage_overview_presentation.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.WorkManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.usage_overview_domain.preferences.Preferences

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 18,Jun,2024
 */

@AndroidEntryPoint
class WidgetBroadcast() : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = UsageWidget
}


@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    val preferences: Preferences
    val workManager: WorkManager
}


fun getPreferences(context: Context): Preferences {
    val hiltEntryPoint = EntryPointAccessors.fromApplication(
        context, WidgetEntryPoint::class.java
    )
    return hiltEntryPoint.preferences
}

fun getWorkManager(context: Context): WorkManager {
    val hiltEntryPoint = EntryPointAccessors.fromApplication(
        context, WidgetEntryPoint::class.java
    )
    return hiltEntryPoint.workManager
}