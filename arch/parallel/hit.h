#pragma once
#include <stdbool.h>

#include <array>

#ifdef __cplusplus
extern "C"
{
#endif

// Returns true if specified coordinates are inside the object
bool hit_test(float x, float y, float z) {
    float result = (x * x * x * x - 2 * x * x * x) + 2 * 2 * (y * y + z * z);

    return result < 0;
}

// Returns array of 6 values:
// x_min, x_max, y_min, y_max, z_min, z_max
const float* get_axis_range() {
    static std::array<float, 6> axis_range = {
            -0.0f, 2.0f,  // x_min, x_max
            -1.0f, 1.0f,  // y_min, y_max
            -1.0f, 1.0f   // z_min, z_max
    };
    return axis_range.data();
}

#ifdef __cplusplus
}
#endif
