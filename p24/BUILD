java_library(
    name = "puzzle_lib",
    srcs = glob(["*.java"]),
    deps = [
        "//helper",
        "@maven//:com_google_guava_guava",
        "@maven//:com_microsoft_z3_libz3_java_linux",
        "@maven//:com_microsoft_z3_java_jar",
    ],
)

java_binary(
    name = "p24",
    data = glob(["*.txt"]),
    main_class = "dev.advent.Puzzle",
    runtime_deps = [
        ":puzzle_lib",
    ],
)

java_binary(
    name = "p24_v2",
    data = glob(["*.txt", "*.z3"]),
    main_class = "dev.advent.Puzzlev2",
    runtime_deps = [
        ":puzzle_lib",
    ],
)

java_binary(
    name = "p24_v3",
    data = glob(["*.txt", "*.z3"]),
    main_class = "dev.advent.Puzzlev3",
    runtime_deps = [
        ":puzzle_lib",
    ],
)
