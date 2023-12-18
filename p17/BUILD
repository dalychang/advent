java_library(
    name = "puzzle_lib",
    srcs = glob(["*.java"]),
    deps = [
        "//helper",
        "@maven//:com_google_guava_guava",
    ],
)

java_binary(
    name = "p17",
    data = glob(["*.txt"]),
    main_class = "dev.advent.Puzzle",
    runtime_deps = [
        ":puzzle_lib",
    ],
)

java_binary(
    name = "p17_v2",
    data = glob(["*.txt"]),
    main_class = "dev.advent.Puzzlev2",
    runtime_deps = [
        ":puzzle_lib",
    ],
)

java_binary(
    name = "p17_v1_2",
    data = glob(["*.txt"]),
    main_class = "dev.advent.Puzzlev3",
    runtime_deps = [
        ":puzzle_lib",
    ],
)

java_binary(
    name = "p17_v2_2",
    data = glob(["*.txt"]),
    main_class = "dev.advent.Puzzlev4",
    runtime_deps = [
        ":puzzle_lib",
    ],
)

java_binary(
    name = "p17_v2_3",
    data = glob(["*.txt"]),
    main_class = "dev.advent.Puzzlev5",
    runtime_deps = [
        ":puzzle_lib",
    ],
)

