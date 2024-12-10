package ly.com.tahaben.launcher_domain.use_case.launcher

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Context
import ly.com.tahaben.core.R

class SetBlackWallpaper(private val context: Context) {
    @SuppressLint("MissingPermission")
    operator fun invoke() {
        val wallpaperManager = WallpaperManager.getInstance(context)
        wallpaperManager.setResource(R.raw.black)
    }
}
