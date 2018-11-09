object AppInfo {
    const val appName = "Raffler"
    const val applicationId = "com.fibelatti.raffler"

    private const val versionMajor = 2
    private const val versionMinor = 0
    private const val versionPatch = 2

    val versionCode: Int = versionMajor * 10000 + versionMinor * 100 + versionPatch

    val versionName: String = "${AppInfo.versionMajor}.${AppInfo.versionMinor}.${AppInfo.versionPatch}"
}
