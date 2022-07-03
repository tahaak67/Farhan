package ly.com.tahaben.infinite_scroll_blocker_domain.model

data class ScrollViewInfo(
    var maxY: Int,
    var addedAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis(),
    var timesGrown: Int = 0
)
