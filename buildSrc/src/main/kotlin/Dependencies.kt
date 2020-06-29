object Versions {
    const val minSdkVersion = 21
    const val targetSdkVersion = 29
    const val compileSdkVersion = 29

    internal const val kotlinVersion = "1.3.72"
    internal const val coroutinesVersion = "1.3.7"
    internal const val lifecycleVersion = "2.2.0"
    internal const val roomVersion = "2.2.5"
}

object Classpaths {
    const val gradlePlugin = "com.android.tools.build:gradle:4.0.0"
    const val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
}

object Dependencies {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlinVersion}"
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesVersion}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesVersion}"

    const val appCompat = "androidx.appcompat:appcompat:1.1.0"
    const val activity = "androidx.activity:activity-ktx:1.1.0"
    const val fragments = "androidx.fragment:fragment-ktx:1.2.4"
    const val supportAnnotations = "androidx.annotation:annotation:1.1.0"
    const val materialDesign = "com.google.android.material:material:1.1.0"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.0-beta6"

    const val coreLib = "com.fibelatti.core:core:2.0.0-alpha4"
    const val coreLibArch = "com.fibelatti.core:arch-components:2.0.0-alpha4"

    const val archComponents = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycleVersion}"
    const val archComponentsCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycleVersion}"

    const val room = "androidx.room:room-runtime:${Versions.roomVersion}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.roomVersion}"

    const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:2.3.0"
    const val navigationUi = "androidx.navigation:navigation-ui-ktx:2.3.0"

    private const val daggerVersion = "2.27"

    const val dagger = "com.google.dagger:dagger:$daggerVersion"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:$daggerVersion"

    const val gson = "com.google.code.gson:gson:2.8.5"

    const val playCore = "com.google.android.play:core:1.6.5"

    const val leakCanary = "com.squareup.leakcanary:leakcanary-android:2.2"
}

object TestDependencies {
    private const val junit5Version = "5.6.0"

    const val coreLibTest = "com.fibelatti.core:core-test:2.0.0-alpha3"
    const val coreLibArchTest = "com.fibelatti.core:arch-components-test:2.0.0-alpha3"

    const val junit = "junit:junit:4.13"
    const val junit5 = "org.junit.jupiter:junit-jupiter-api:$junit5Version"
    const val junit5Engine = "org.junit.jupiter:junit-jupiter-engine:$junit5Version"
    const val junit5Params = "org.junit.jupiter:junit-jupiter-params:$junit5Version"
    const val junitVintage = "org.junit.vintage:junit-vintage-engine:$junit5Version"
    const val testRunner = "com.android.support.test:runner:1.1.0"
    const val kotlinTest = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlinVersion}"
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutinesVersion}"
    const val mockitoCore = "org.mockito:mockito-inline:3.2.4"
    const val archComponentsTest = "android.arch.core:core-testing:${Versions.lifecycleVersion}"
    const val roomTest = "android.arch.persistence.room:testing:${Versions.roomVersion}"
}
