
plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"

}

repositories {
    mavenCentral()
}

dependencies {
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

    //FFMPEG

}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass.set("org.theopitsi.multimedia.MMClient")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

javafx {
    version = "21"
    modules("javafx.controls", "javafx.fxml")
}