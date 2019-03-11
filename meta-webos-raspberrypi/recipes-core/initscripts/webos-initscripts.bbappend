# Copyright (c) 2019 LG Electronics, Inc.

EXTENDPRAUTO_append = "rpi1"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI_append = " \
    file://0001-Workaround-Unblock-wifi-device.patch \
"
