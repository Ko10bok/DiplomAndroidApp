// build.gradle.kts (корень проекта)

plugins {
    // Подключаем плагины с apply false, чтобы они не применялись здесь, а только в модулях
    id("com.android.application") version "8.1.0" apply false
    kotlin("android") version "1.9.10" apply false
    kotlin("kapt") version "1.9.10" apply false
}

allprojects {

}
