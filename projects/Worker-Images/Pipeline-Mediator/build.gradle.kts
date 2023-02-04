plugins {
    java
    id("org.springframework.boot") version "3.0.0"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.openapi.generator") version "6.2.1"
    id("io.freefair.lombok") version "6.6-rc1"
    id("org.graalvm.buildtools.native") version "0.9.18"
}

group = "eu.venthe.pipeline"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation( "org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains:annotations:23.0.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")

    implementation("org.awaitility:awaitility:4.2.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.1")

    // kafka
    implementation("org.springframework.kafka:spring-kafka")
    testImplementation("org.springframework.kafka:spring-kafka-test")

    // Swagger
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.7")
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("org.openapitools:jackson-databind-nullable:0.2.4")
    implementation("io.swagger.core.v3:swagger-models:2.2.6")

    // ??/
    // https://mvnrepository.com/artifact/org.junit.platform/junit-platform-launcher
    testImplementation("org.junit.platform:junit-platform-launcher:1.9.1")// https://mvnrepository.com/artifact/commons-codec/commons-codec
    implementation("commons-codec:commons-codec:1.15")


}

tasks.withType<Test> {
    useJUnitPlatform()
}

val commonProps = mapOf(
        "licenseName" to "MIT",
        "licenseUrl" to "https://opensource.org/licenses/MIT")

//val gerritApi by tasks.register("generateGerritWebhookApi", org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
//    outputDir.set("$buildDir/generated")
//    apiPackage.set("eu.venthe.pipeline.gerrit_mediator.gerrit.api")
//    invokerPackage.set("eu.venthe.pipeline.gerrit_mediator.jenkins.invoker")
//    modelPackage.set("eu.venthe.pipeline.gerrit_mediator.jenkins.model")
//    templateDir.set("$projectDir/src/main/resources/template/")
//
//    generatorName.set("spring")
//    inputSpec.set("$rootDir/specs/gerrit-event-stream.openapi.yaml")
//    configOptions.set(
//            mapOf(
//                    "library" to "spring-boot",
//                    "useOptional" to "true",
//                    "reactive" to "true",
//                    "useSwaggerUI" to "false",
//                    "interfaceOnly" to "true"
//            ) + commonProps
//    )
//
//    doLast {
//        delete(
//                "${project.buildDir}/build/generated/src/main/java/eu/venthe/pipeline/gerrit_mediator/gerrit/invoker",
//                "${project.buildDir}/build/generated/src/main/java/org/openapitools/configuration",
//        )
//    }
//}

val jenkinsApi by tasks.register("generateJenkinsWebhookApi", org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
    outputDir.set("$buildDir/generated")
    apiPackage.set("eu.venthe.pipeline.gerrit_mediator.gerrit.api")
    invokerPackage.set("eu.venthe.pipeline.gerrit_mediator.jenkins.invoker")
    modelPackage.set("eu.venthe.pipeline.gerrit_mediator.jenkins.model")
    templateDir.set("$projectDir/src/main/resources/template/")

    generatorName.set("java")
    inputSpec.set("$rootDir/specs/jenkins.openapi.yaml")
    configOptions.set(
            mapOf(
                    "library" to "resttemplate",
                    "openApiNullable" to "false"
            ) + commonProps
    )

    doLast {
        delete(
                "${project.buildDir}/build/generated/src/main/java/org/openapitools/configuration",
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    dependsOn(/*gerritApi, */jenkinsApi)
    mustRunAfter(/*gerritApi, */jenkinsApi)
}

openApiValidate {
    inputSpec.set("$rootDir/specs/gerrit-event-stream.openapi.yaml")
}

java.sourceSets["main"].java {
    srcDir("build/generated/src/main/java")
}
