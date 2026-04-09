# 最小支持的 CMake 版本
cmake_minimum_required(VERSION 3.16)

# 设置项目名称
project(DevCore)

# 设置 C++ 标准
set(CMAKE_CXX_STANDARD 14)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# 基础源文件
set(BASE_SOURCES
        DevCore.cpp
        logger.cpp
)

# 合并所有源文件
set(ALL_SOURCES ${BASE_SOURCES})

# 创建单一的共享库目标
add_library(${CMAKE_PROJECT_NAME} SHARED ${ALL_SOURCES})

# 包含头文件路径
target_include_directories(${CMAKE_PROJECT_NAME} PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}
)

# 链接库
target_link_libraries(${CMAKE_PROJECT_NAME}
        # android基础库
        android
        log
        jnigraphics
)