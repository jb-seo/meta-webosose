# Copyright (c) 2019 LG Electronics, Inc.

# You don't need to change this value when you're changing just a RDEPENDS_${PN} variable.
EXTENDPRAUTO_append = "webosvirt1"

RDEPENDS_${PN}_append = " \
    docker \
    e2fsprog-resize2fs \
    kernel-modules \
"
