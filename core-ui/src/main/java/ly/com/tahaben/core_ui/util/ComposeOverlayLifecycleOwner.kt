package ly.com.tahaben.core_ui.util

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

class ComposeOverlayLifecycleOwner(
) : SavedStateRegistryOwner {
    private var mLifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private var mSavedStateRegistryController: SavedStateRegistryController =
        SavedStateRegistryController.create(this)

    /**
     * @return True if the Lifecycle has been initialized.
     */
    val isInitialized: Boolean
        get() = true

    override val lifecycle: Lifecycle
        get() = mLifecycleRegistry

    fun setCurrentState(state: Lifecycle.State) {
        mLifecycleRegistry.currentState = state
    }

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        mLifecycleRegistry.handleLifecycleEvent(event)
    }

    override val savedStateRegistry: SavedStateRegistry
        get() = mSavedStateRegistryController.savedStateRegistry

    fun performRestore(savedState: Bundle?) {
        mSavedStateRegistryController.performRestore(savedState)
    }

    fun performSave(outBundle: Bundle) {
        mSavedStateRegistryController.performSave(outBundle)
    }
}