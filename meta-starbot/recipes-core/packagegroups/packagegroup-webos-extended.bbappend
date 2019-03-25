# Copyright (c) 2019 LG Electronics, Inc.

EXTENDPRAUTO_append = "starbot1"

RDEPENDS_${PN}_append = " \
    rplidar-ros2 \
    kernel-module-cp210x \
    kernel-module-ftdi-sio \
    usbutils \
    screen \
    pi-camera \
"
