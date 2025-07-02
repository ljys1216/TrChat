repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://nexus.scarsz.me/content/groups/public/")
    maven("https://repo.tabooproject.org/repository/releases/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(project(":project:common"))
    compileOnly(project(":project:module-adventure"))
//    compileOnly(project(":project:module-chat"))
    compileOnly(project(":project:module-compat"))
    compileOnly(project(":project:module-nms"))
    compileOnly("ink.ptms.core:v12105:12105:universal")
    compileOnly("net.md-5:bungeecord-chat:1.21-R0.3")
    compileOnly(fileTree(rootDir.resolve("libs")))

    compileOnly("me.clip:placeholderapi:2.11.6") { isTransitive = false }
    compileOnly("com.discordsrv:discordsrv:1.26.0") { isTransitive = false }

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.mockito:mockito-core:4.5.1")
    testImplementation(fileTree(rootDir.resolve("libs")))
    testImplementation(project(":project:common"))
    testImplementation(project(":project:module-adventure"))
    testImplementation(project(":project:module-compat"))
    testImplementation(project(":project:module-nms"))
    testImplementation("net.kyori:adventure-api:4.21.0")
    testImplementation("net.kyori:adventure-text-minimessage:4.21.0")
}

tasks.test {
    useJUnitPlatform()
}

taboolib { subproject = true }