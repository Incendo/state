plugins {
    id("org.incendo.cloud-build-logic")
    id("org.incendo.cloud-build-logic.spotless")
}

indra {
    checkstyle().set(libs.versions.checkstyle)

    javaVersions {
        minimumToolchain(17)
        target(17)
        testWith().set(setOf(17))
    }
}

cloudSpotless {
    ktlintVersion = libs.versions.ktlint
}

spotless {
    java {
        importOrderFile(rootProject.file(".spotless/state.importorder"))
    }
}

// Common dependencies.
dependencies {
    compileOnly(libs.checkerQual)
    compileOnly(libs.apiguardian)

    // test dependencies
    testImplementation(libs.jupiter.engine)
    testImplementation(libs.jupiter.params)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.jupiter)
    testImplementation(libs.truth)
}
