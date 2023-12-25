object Compose {
    const val composeVersion = "1.5.4"
    const val composeCompilerVersion = "1.5.7"
    private const val materialVersion = "1.3.1"
    private const val material3Version = "1.1.2"
    const val material = "androidx.compose.material:material:$materialVersion"
    const val material3 = "androidx.compose.material3:material3:$material3Version"
    const val ui = "androidx.compose.ui:ui:$composeVersion"
    const val uiToolingPreview = "androidx.compose.ui:ui-tooling-preview:$composeVersion"
    const val runtime = "androidx.compose.runtime:runtime:$composeVersion"
    const val compiler = "androidx.compose.compiler:compiler:$composeCompilerVersion"
    const val materialExtended =
        "androidx.compose.material:material-icons-extended:$materialVersion"

    private const val navigationVersion = "2.7.2"
    const val navigation = "androidx.navigation:navigation-compose:$navigationVersion"

    private const val hiltNavigationComposeVersion = "1.0.0"
    const val hiltNavigationCompose =
        "androidx.hilt:hilt-navigation-compose:$hiltNavigationComposeVersion"

    private const val activityComposeVersion = "1.7.2"
    const val activityCompose = "androidx.activity:activity-compose:$activityComposeVersion"

    private const val lifecycleVersion = "2.6.2"
    const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion"
    const val lifecycleUtilityCompose = "androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion"

    private const val constraintComposeVersion = "1.0.1"
    const val constraintCompose =
        "androidx.constraintlayout:constraintlayout-compose:$constraintComposeVersion"
}
