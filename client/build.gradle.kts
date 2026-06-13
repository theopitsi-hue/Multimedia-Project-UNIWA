
plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    //common code
    implementation(project(":common"))

    //junit
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(libs.guava)

    //lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    //for tests using Lombok
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

    //speed test lib
    implementation("fr.bmartel:jspeedtest:1.32.1")
    testImplementation("fr.bmartel:jspeedtest:1.32.1")

    implementation("uk.co.caprica:vlcj:4.8.3")
    implementation("uk.co.caprica:vlcj-javafx:1.2.0")

    implementation("net.java.dev.jna:jna:5.14.0")
    implementation("net.java.dev.jna:jna-platform:5.14.0")

}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass.set("org.theopitsi.multimedia.client.MMClient")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.media", "javafx.graphics")
}