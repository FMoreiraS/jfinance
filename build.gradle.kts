plugins {
    id("java")
    application
}

group = "com.jfinance"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // JDBC Postgres Driver
    implementation("org.postgresql:postgresql:42.7.10")
}


application {
    mainClass="Main"
}

tasks.test {
    useJUnitPlatform()
}