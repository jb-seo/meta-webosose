# Copyright (c) 2018 LG Electronics, Inc.

EXTENDPRAUTO_append = "webosros22"

VIRTUAL-RUNTIME_com.webos.service.rosbridge ?= "com.webos.service.rosbridge"
VIRTUAL-RUNTIME_com.webos.service.rosbridge_armv4 = ""
VIRTUAL-RUNTIME_com.webos.service.rosbridge_armv5 = ""

RDEPENDS_${PN}_append = " \
    packagegroup-ros2-world \
"
