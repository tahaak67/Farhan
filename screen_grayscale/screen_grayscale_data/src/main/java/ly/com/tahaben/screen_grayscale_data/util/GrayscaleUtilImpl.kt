package ly.com.tahaben.screen_grayscale_data.util


import android.content.Context
import android.content.pm.PackageManager
import com.scottyab.rootbeer.RootBeer
import ly.com.tahaben.screen_grayscale_domain.preferences.Preferences
import ly.com.tahaben.screen_grayscale_domain.util.GrayscaleUtil
import timber.log.Timber
import java.io.DataOutputStream
import java.io.IOException

class GrayscaleUtilImpl(
    private val context: Context,
    private val sharedPref: Preferences,
    private val rootBeer: RootBeer
) : GrayscaleUtil {

    override fun isSecureSettingsPermissionGranted(): Boolean {
        return context.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") == PackageManager.PERMISSION_GRANTED
    }

    override fun isDeviceRooted(): Boolean {
        return rootBeer.isRooted
    }


    override fun getSecureSettingsPermissionWithRoot(): Boolean {
        val cmds =
            arrayOf("pm grant ly.com.tahaben.farhan android.permission.WRITE_SECURE_SETTINGS")
        try {
            Timber.d("excuting command")
            val p = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(p.outputStream)
            for (tmpCmd in cmds) {
                os.writeBytes(
                    """
                $tmpCmd
                
                """.trimIndent()
                )
            }
            Timber.d("done exiting")
            os.writeBytes("exit\n")
            os.flush()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }
}