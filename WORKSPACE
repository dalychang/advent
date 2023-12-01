workspace(name = "dev_advent")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

############################
#          Maven           #
############################
RULES_JVM_EXTERNAL_TAG = "5.2"
RULES_JVM_EXTERNAL_SHA = "3824ac95d9edf8465c7a42b7fcb88a5c6b85d2bac0e98b941ba13f235216f313"

http_archive(
    name = "rules_jvm_external",
    sha256 = RULES_JVM_EXTERNAL_SHA,
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
)

load("@rules_jvm_external//:repositories.bzl", "rules_jvm_external_deps")

rules_jvm_external_deps()

load("@rules_jvm_external//:setup.bzl", "rules_jvm_external_setup")

rules_jvm_external_setup()

load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = [
        "com.fasterxml.jackson.core:jackson-core:2.15.3",
        "com.fasterxml.jackson.core:jackson-databind:2.15.3",
        "com.fasterxml.jackson.jakarta.rs:jackson-jakarta-rs-json-provider:2.15.3",
        "com.github.pcj:google-options:1.0.0",
        "com.google.auto.value:auto-value-annotations:1.10.1",
        "com.google.auto.value:auto-value:1.10.1",
        "com.google.code.findbugs:jsr305:3.0.2",
        "com.google.code.gson:gson:2.10.1",
        "com.google.guava:guava:31.1-jre",
        "com.google.inject:guice:7.0.0",
        "com.opencsv:opencsv:5.7.1",
        "jakarta.annotation:jakarta.annotation-api:2.1.1",
        "org.apache.commons:commons-lang3:jar:3.12.0",
    ],
    generate_compat_repositories = True,
    repositories = [
        "https://repo1.maven.org/maven2",
        "https://repo.maven.apache.org/maven2",
    ],
)

load("@maven//:compat.bzl", "compat_repositories")

compat_repositories()
