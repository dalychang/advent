java_library(
    name = "puzzle_lib",
    srcs = glob(["*.java"]),
    deps = [
        "@bazel_tools//tools/java/runfiles",
        "@maven//:com_github_pcj_google_options",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_inject_guice",
    ],
)

java_binary(
    name = "p4",
    data = glob(["*.txt"]),
    main_class = "dev.advent.Puzzle",
    runtime_deps = [
        ":puzzle_lib",
        "@bazel_tools//tools/java/runfiles",
    ],
)

java_binary(
    name = "p4_v2",
    data = glob(["*.txt"]),
    main_class = "dev.advent.Puzzlev2",
    runtime_deps = [
        ":puzzle_lib",
        "@bazel_tools//tools/java/runfiles",
    ],
)
