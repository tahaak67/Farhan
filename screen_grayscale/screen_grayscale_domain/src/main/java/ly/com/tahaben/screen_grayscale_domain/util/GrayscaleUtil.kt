package ly.com.tahaben.screen_grayscale_domain.util

interface GrayscaleUtil {

    fun isSecureSettingsPermissionGranted(): Boolean
    fun isDeviceRooted(): Boolean
    fun getSecureSettingsPermissionWithRoot(): Boolean

    fun setGrayscaleState(isEnabled: Boolean)
}