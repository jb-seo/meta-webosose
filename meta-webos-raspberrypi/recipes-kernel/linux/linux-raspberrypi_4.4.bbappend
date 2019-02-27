# Copyright (c) 2019 LG Electronics, Inc.

EXTENDPRAUTO_append = "3BP"

# Add RPI 3B+ support to the kernel
SRC_URI_append = " \
    file://0001-Support-raspberry-pi-3B.patch \
"
