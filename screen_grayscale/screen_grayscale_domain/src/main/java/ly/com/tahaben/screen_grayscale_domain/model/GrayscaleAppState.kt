package ly.com.tahaben.screen_grayscale_domain.model

/**
 * Per-app grayscale behaviour, declared in the order the options are displayed.
 */
enum class GrayscaleAppState {
    /** Grayscale is turned off when this app is in the foreground. */
    COLOR,

    /** The current color-filter state is left untouched when this app is in the foreground. */
    LEAVE_AS_IS,

    /** Grayscale is turned on when this app is in the foreground. */
    GRAYSCALE
}
