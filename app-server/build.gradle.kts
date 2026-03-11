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

    //FFMPEG wrapper
    implementation("ws.schild:jave-core:3.5.0")
    implementation("ws.schild:jave-nativebin-win64:3.5.0")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "org.theopitsi.multimedia.MMServer"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}


javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml")
}