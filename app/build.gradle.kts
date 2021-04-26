plugins {
    id("com.android.application")
    id("kotlin-android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.apollographql.apollo").version("2.5.5").apply(false)
}

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.3")

    defaultConfig {
        applicationId = "com.example.rocketreserver"
        minSdkVersion(23)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.core:core-ktx:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("com.apollographql.apollo:apollo-runtime:2.5.5")
    implementation("com.apollographql.apollo:apollo-coroutines-support:2.5.5")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("io.coil-kt:coil:0.11.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.0-rc01")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.0-rc01")
    implementation("androidx.paging:paging-runtime-ktx:2.1.2")
    implementation("com.google.android.material:material:1.1.0")
    implementation("androidx.security:security-crypto:1.0.0-rc02")

    testImplementation("junit:junit:4.13")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
}


val generateSourcesTask = tasks.register("generateService1ApolloSources", com.apollographql.apollo.gradle.internal.ApolloGenerateSourcesTask::class.java) {
    generateKotlinModels.set(true)
    outputDir.set(layout.buildDirectory.dir("generated/source/apollo/service1"))
    schemaFile.set(file("src/test/graphql/service1/schema.sdl"))
    val sourceDirectorySet = objects.sourceDirectorySet("service1", "service1")
    sourceDirectorySet.include("**/*.graphql")
    sourceDirectorySet.srcDir("src/test/graphql/service1")
    graphqlFiles.from(sourceDirectorySet)
    projectName.set(name)
    generateMetadata.set(false)
    rootPackageName.set("com.service1")
    projectRootDir.set(rootDir)
    rootFolders.set(sourceDirectorySet.sourceDirectories.map { it.absolutePath })
    metadataOutputFile.set(layout.buildDirectory.file("metadata/apollo/metadata.json"))
    operationOutputGenerator = com.apollographql.apollo.compiler.OperationOutputGenerator.DefaultOperationOuputGenerator(com.apollographql.apollo.compiler.OperationIdGenerator.Sha256())
}

android {
    unitTestVariants.all {
        println("configuring variant: $name")
        addJavaSourceFoldersToModel(generateSourcesTask.get().outputDir.asFile.get())
        registerJavaGeneratingTask(generateSourcesTask.get(), generateSourcesTask.get().outputDir.asFile.get())
    }
}