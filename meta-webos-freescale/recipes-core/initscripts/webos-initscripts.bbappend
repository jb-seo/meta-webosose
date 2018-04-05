EXTENDPRAUTO_append = "wand1"

FILESEXTRAPATHS_prepend := "${THISDIR}/webos-initscripts:"

SRC_URI_append = " \
    file://0001-iMX6-Set-32bpp-to-launch-eglfs-successfully.patch \
"
