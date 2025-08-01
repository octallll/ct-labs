/**
    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
    ░░░░░░░░░░▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄░░░░░░░░░
    ░░░░░░░░▄▀░░░░░░░░░░░░▄░░░░░░░▀▄░░░░░░░
    ░░░░░░░░█░░▄░░░░▄░░░░░░░░░░░░░░█░░░░░░░
    ░░░░░░░░█░░░░░░░░░░░░▄█▄▄░░▄░░░█░▄▄▄░░░
    ░▄▄▄▄▄░░█░░░░░░▀░░░░▀█░░▀▄░░░░░█▀▀░██░░
    ░██▄▀██▄█░░░▄░░░░░░░██░░░░▀▀▀▀▀░░░░██░░
    ░░▀██▄▀██░░░░░░░░▀░██▀░░░░░░░░░░░░░▀██░
    ░░░░▀████░▀░░░░▄░░░██░░░▄█░░░░▄░▄█░░██░
    ░░░░░░░▀█░░░░▄░░░░░██░░░░▄░░░▄░░▄░░░██░
    ░░░░░░░▄█▄░░░░░░░░░░░▀▄░░▀▀▀▀▀▀▀▀░░▄▀░░
    ░░░░░░█▀▀█████████▀▀▀▀████████████▀░░░░
    ░░░░░░████▀░░███▀░░░░░░▀███░░▀██▀░░░░░░
    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
**/

#include <iostream>
#include <omp.h>
#include "hit.h"
#include <random>
#include <iomanip>
#include <chrono>
#include <cstring>
#include <fstream>

using namespace std;

const float *range = get_axis_range();

const float x_min = range[0], x_max = range[1];
const float y_min = range[2], y_max = range[3];
const float z_min = range[4], z_max = range[5];

mt19937 rnd(572);

struct randomGenerator {
    uint32_t mul = 1664525, add = 1013904223, current;

    randomGenerator() {
        current = rnd();
    }

    uint32_t gen() {
        current = current * mul + add;
        return current;
    }
};

float getInRange(float min, float max, float randomFloat) {
    return randomFloat * (max - min) + min;
}

float simulateFirstVariant(long long stepCount) {
    long long countIn = 0;
    int countAll = 0;

    randomGenerator generator;

    for (long long step = 0; step < stepCount; step++) {
        float x = getInRange(x_min, x_max, (float) generator.gen() / UINT_MAX);
        float y = getInRange(y_min, y_max, (float) generator.gen() / UINT_MAX);
        float z = getInRange(z_min, z_max, (float) generator.gen() / UINT_MAX);

        if (hit_test(x, y, z)) {
            countIn++;
        }

        countAll++;
    }

    return (float) countIn / (float) countAll * (x_max - x_min) * (y_max - y_min) * (z_max - z_min);
}

enum Schedule {
    Static,
    Dynamic
};

float simulateSecondVariant(long long stepCount) {
    long long countIn = 0;

#pragma omp parallel
    {
        long long tempIn = 0;

        randomGenerator generator;

#pragma omp for
        for (long long step = 0; step < stepCount; step++) {
            float x = getInRange(x_min, x_max, (float) generator.gen() / UINT_MAX);
            float y = getInRange(y_min, y_max, (float) generator.gen() / UINT_MAX);
            float z = getInRange(z_min, z_max, (float) generator.gen() / UINT_MAX);

            if (hit_test(x, y, z)) {
                tempIn++;
            }
        }

#pragma omp atomic
        countIn += tempIn;
    }

    float answer;

    answer = (float) countIn / (float) stepCount * (x_max - x_min) * (y_max - y_min) * (z_max - z_min);

    return answer;
}

float simulateThirdVariant(long long stepCount, int numThreads, long long chunkSize, Schedule kind) {
    long long countIn = 0;

    if (kind == Static) {
#pragma omp parallel num_threads(numThreads)
        {
            int threadId = omp_get_thread_num();
            long long tempIn = 0;

            randomGenerator generator;

            long long step = threadId * chunkSize;
            long long was = 0;
            long long nxt = (long long) (threadId + 1) * chunkSize;

            while (step < stepCount) {
                float x = getInRange(x_min, x_max, (float) generator.gen() / UINT_MAX);
                float y = getInRange(y_min, y_max, (float) generator.gen() / UINT_MAX);
                float z = getInRange(z_min, z_max, (float) generator.gen() / UINT_MAX);

                if (hit_test(x, y, z)) {
                    tempIn++;
                }

                if (step == nxt - 1) {
                    step += (numThreads - 1) * chunkSize + 1;
                    was++;
                    nxt = step + chunkSize;
                } else {
                    step++;
                }
            }

            countIn += tempIn;
        }
    } else {
        long long nextIteration = 0;

#pragma omp parallel num_threads(numThreads)
        {
            long long tempIn = 0;

            randomGenerator generator;

            while (nextIteration * chunkSize < stepCount) {
                int iteration;
#pragma omp atomic capture
                iteration = nextIteration++;

                for (long long step = iteration * chunkSize; step < min((iteration + 1) * chunkSize, stepCount); step++) {
                    float x = getInRange(x_min, x_max, (float) generator.gen() / UINT_MAX);
                    float y = getInRange(y_min, y_max, (float) generator.gen() / UINT_MAX);
                    float z = getInRange(z_min, z_max, (float) generator.gen() / UINT_MAX);

                    if (hit_test(x, y, z)) {
                        tempIn++;
                    }
                }
            }
#pragma omp atomic
            countIn += tempIn;
        }
    }

    return (float) countIn / (float) stepCount * (x_max - x_min) * (y_max - y_min) * (z_max - z_min);
}

const float pi = acos(-1);

int main(int argc, char *argv[]) {
    const char *inputFile = nullptr;
    const char *outputFile = nullptr;

    int numThreads = -1;
    Schedule kind = Dynamic;
    long long chunkSize = -1;
    int realization = -1;

    for (int i = 1; i < argc; i++) {
        string arg = argv[i];

        if (arg == "--input") {
            inputFile = argv[i + 1];
        }

        if (arg == "--output") {
            outputFile = argv[i + 1];
        }

        if (arg == "--realization") {
            realization = stoi(argv[i + 1]);
        }

        if (arg == "--threads") {
            numThreads = stoi(argv[i + 1]);
        }

        if (arg == "--kind") {
            kind = ((string)argv[i + 1] == "static" ? Static : Dynamic);
        }

        if (arg == "--chunk_size") {
            chunkSize = stoi(argv[i + 1]);
        }
    }

    if (inputFile == nullptr) {
        cerr << "No input file\n";
        return 0;
    }

    freopen(inputFile, "r", stdin);

    long long stepCount;
    cin >> stepCount;

    if (numThreads == -1) {
        numThreads = 16;
    }

    if (chunkSize == -1) {
        chunkSize = stepCount / numThreads;
    }

    omp_set_num_threads(numThreads);

    omp_set_schedule((kind == Static ? omp_sched_static : omp_sched_dynamic), chunkSize);

    float result;

    auto startTime = omp_get_wtime();

    if (realization == 1) {
        result = simulateFirstVariant(stepCount);
    }

    if (realization == 2) {
        result = simulateSecondVariant(stepCount);
    }

    if (realization == 3) {
        result = simulateThirdVariant(stepCount, numThreads, chunkSize, kind);
    }

    auto endTime = omp_get_wtime();

    if (outputFile == nullptr) {
        cerr << "No output file\n";
        return 0;
    }

    ofstream out;

    out.open(outputFile);

    out << result << endl;

    out.close();

    printf("Time (%i thread(s)): %g ms\n", numThreads, (endTime - startTime) * 1000);
}