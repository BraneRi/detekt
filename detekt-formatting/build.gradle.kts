plugins {
    module
}

dependencies {
    implementation(project(":detekt-api"))
    implementation("com.pinterest.ktlint:ktlint-ruleset-standard") {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation("com.pinterest.ktlint:ktlint-core") {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation("com.pinterest.ktlint:ktlint-ruleset-experimental") {
        exclude(group = "org.jetbrains.kotlin")
    }

    testImplementation(project(":detekt-test"))
}

tasks.build { finalizedBy(":detekt-generator:generateDocumentation") }

val depsToPackage = setOf(
    "org.ec4j.core",
    "com.pinterest.ktlint"
)

tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE // allow duplicates
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { dependency -> depsToPackage.any { it in dependency.toString() } }
            .map { if (it.isDirectory) it else zipTree(it) }
    })
}

val moveJarForIntegrationTest by tasks.registering {
    dependsOn(tasks.named("jar"))
    doLast {
        copy {
            from(tasks.named("jar"))
            into(rootProject.buildDir)
            rename { "detekt-formatting.jar" }
        }
    }
}
