# Copyright (c) 2019 LG Electronics, Inc.

SUMMARY = "RPLidar ros2 node"
AUTHOR  = "Hunter L. Allen <hunter@openrobotics.org>"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a6c8ac7da7368c03f2b7c2c4a246194d"

DEPENDS = "rclcpp ros2-sensor-msgs ros2-std-srvs ament-cmake-ros"

inherit ament

SRCREV = "944d9f6359113334e8b276bc628b1a72380b610d"
SRC_URI = "git://github.com/jb-seo/rplidar_ros2.git;branch=ros2"

FILES_${PN} = "${bindir} ${datadir}"

S = "${WORKDIR}/git"
