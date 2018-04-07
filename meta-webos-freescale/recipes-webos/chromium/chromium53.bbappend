EXTENDPRAUTO_append = "wand1"

GYP_DEFINES_remove = "use_directmedia2=1 use_umediaserver=1 use_webos_media_focus_extension=1"
DEPENDS_remove = "umediaserver"
DEPENDS_append = " imx-vpuwrap"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI_append = " \
    file://0001-Add-VPU-video-decode-accelerator-to-Chromium-GPU-med.patch \
"
