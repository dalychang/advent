java_library(
    name = "puzzle_lib",
    srcs = glob(["*.java"]),
    deps = [
        "//helper",
        "@maven//:com_google_guava_guava",
    ],
)

java_binary(
    name = "p11",
    data = glob(["*.txt"]),
    main_class = "dev.advent.Puzzle",
    runtime_deps = [
        ":puzzle_lib",
    ],
)

java_binary(
    name = "p11_v2",
    data = glob(["*.txt"]),
    main_class = "dev.advent.Puzzlev2",
    runtime_deps = [
        ":puzzle_lib",
    ],
)
