plugins {
    id("java")
}

dependencies {
    implementation(project(":volt-core"))
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("ch.qos.logback:logback-classic:1.5.0")
}