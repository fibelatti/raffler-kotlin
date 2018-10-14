object Versions {
    const val gradleVersion = "3.2.0"
    const val kotlinVersion = "1.3.0-rc-190"
    const val dexCountPlugin = "0.8.2"
    const val jacocoVersion = "0.8.1"

    const val minSdkVersion = 19
    const val targetSdkVersion = 28
    const val compileSdkVersion = 28

    const val buildToolsVersion = "28.0.2"

    internal const val appCompatVersion = "1.0.0"
    internal const val materialDesignVersion = "1.0.0"
    internal const val constraintLayoutVersion = "2.0.0-alpha1"

    internal const val coroutinesCoreVersion = "1.0.0-RC1"
    internal const val coroutinesAndroidVersion = "1.0.0-RC1"

    internal const val archComponentsVersion = "2.0.0"
    internal const val navigationVersion = "1.0.0-alpha06"

    internal const val daggerVersion = "2.17"

    internal const val gsonVersion = "2.8.5"

    internal const val jUnitVersion = "4.12"
    internal const val testRunnerVersion = "1.0.1"
    internal const val mockitoVersion = "2.23.0"
    internal const val junit5pluginVersion = "1.2.0.0"
    internal const val junit5Version = "5.2.0"
}

object Classpaths {
    val gradlePlugin = "com.android.tools.build:gradle:${Versions.gradleVersion}"
    val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
    val junit5Plugin = "de.mannodermaus.gradle.plugins:android-junit5:${Versions.junit5pluginVersion}"
    val jacocoPlugin = "org.jacoco:org.jacoco.core:${Versions.jacocoVersion}"
    val dexCountPlugin = "com.getkeepsafe.dexcount:dexcount-gradle-plugin:${Versions.dexCountPlugin}"
}

object KotlinDependencies {
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlinVersion}"
    val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesCoreVersion}"
    val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesAndroidVersion}"
}

object SupportLibraryDependencies {
    val supportLibrary = "androidx.appcompat:appcompat:${Versions.appCompatVersion}"
    val materialDesign = "com.google.android.material:material:${Versions.materialDesignVersion}"
    val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayoutVersion}"
}

object ArchitectureComponentDependencies {
    val archComponents = "androidx.lifecycle:lifecycle-extensions:${Versions.archComponentsVersion}"
    val archComponentsCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.archComponentsVersion}"
    val room = "androidx.room:room-runtime:${Versions.archComponentsVersion}"
    val roomCompiler = "androidx.room:room-compiler:${Versions.archComponentsVersion}"
    val navigationFragment = "android.arch.navigation:navigation-fragment-ktx:${Versions.navigationVersion}"
    val navigationUi = "android.arch.navigation:navigation-ui-ktx:${Versions.navigationVersion}"
}

object DIDependencies {
    val dagger = "com.google.dagger:dagger:${Versions.daggerVersion}"
    val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.daggerVersion}"
}

object ThirdPartyDependencies {
    val gson = "com.google.code.gson:gson:${Versions.gsonVersion}"
}

object TestDependencies {
    val junit = "junit:junit:${Versions.jUnitVersion}"
    val junit5 = "org.junit.jupiter:junit-jupiter-api:${Versions.junit5Version}"
    val junit5Engine = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit5Version}"
    val junit5Params = "org.junit.jupiter:junit-jupiter-params:${Versions.junit5Version}"
    val junitVintage = "org.junit.vintage:junit-vintage-engine:${Versions.junit5Version}"
    val testRunner = "com.android.support.test:runner:${Versions.testRunnerVersion}"
    val kotlinTest = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlinVersion}"
    val mockitoCore = "org.mockito:mockito-core:${Versions.mockitoVersion}"
    val mockitoAndroid = "org.mockito:mockito-android:${Versions.mockitoVersion}"
    val supportAnnotations = "androidx.annotation:annotation:${Versions.appCompatVersion}"
    val archComponentsTest = "android.arch.core:core-testing:${Versions.archComponentsVersion}"
}
