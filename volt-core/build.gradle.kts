import cl.franciscosolis.sonatypecentralupload.SonatypeCentralUploadTask

plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
    id("cl.franciscosolis.sonatype-central-upload") version "1.0.3"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    api("com.zaxxer:HikariCP:7.0.2")
    implementation("org.slf4j:slf4j-api:2.0.9")
    compileOnly("org.jetbrains:annotations:26.0.2")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "volt-core"
            from(components["java"])

            pom {
                name.set("Volt")
                description.set("A lightweight Java ORM")
                url.set("https://github.com/oskarscot/volt")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("oskarscot")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/oskarscot/volt.git")
                    developerConnection.set("scm:git:ssh://github.com/oskarscot/volt.git")
                    url.set("https://github.com/oskarscot/volt")
                }
            }
        }
    }
}

signing {
    val signingKey = System.getenv("GPG_PRIVATE_KEY")
    val signingPassword = System.getenv("GPG_PASSPHRASE")

    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    }

    sign(publishing.publications["mavenJava"])
}

tasks.withType<Javadoc> {
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
}

tasks.named<SonatypeCentralUploadTask>("sonatypeCentralUpload") {
    dependsOn(tasks.jar, tasks.named("sourcesJar"), tasks.named("javadocJar"), tasks.named("generatePomFileForMavenJavaPublication"))

    username = System.getenv("OSSRH_USERNAME")
    password = System.getenv("OSSRH_PASSWORD")

    archives = files(
        tasks.jar,
        tasks.named("sourcesJar"),
        tasks.named("javadocJar")
    )

    pom = file("build/publications/mavenJava/pom-default.xml")

    signingKey = System.getenv("GPG_PRIVATE_KEY")
    signingKeyPassphrase = System.getenv("GPG_PASSPHRASE")
}