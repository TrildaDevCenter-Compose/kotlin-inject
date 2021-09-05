plugins {
    kotlin("jvm")
    id("kotlin-inject.detekt")
    id("kotlin-inject.merge-tests")
}

dependencies {
    implementation(project(":kotlin-inject-compiler:core"))
    implementation(project(":kotlin-inject-compiler:kapt"))
    implementation(project(":kotlin-inject-compiler:ksp"))

    implementation(libs.kotlin.compile.testing)
    implementation(libs.bundles.kotlin.compile.testing)

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation(libs.assertk)
    testImplementation(libs.burst.junit4)
}