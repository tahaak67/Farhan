package ly.com.tahaben.farhan

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication
import timber.log.Timber

class HiltTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        Timber.plant(Timber.DebugTree())
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}