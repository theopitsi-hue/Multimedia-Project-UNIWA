plugins {
    java
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

    //vlcj
    implementation("uk.co.caprica:vlcj:4.8.2")
    // Needed for native access (JNA)
    implementation("net.java.dev.jna:jna:5.14.0")
}