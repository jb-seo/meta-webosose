# Copyright (c) 2015-2019 LG Electronics, Inc.

SUMMARY = "WebAppMgr is responsible for running web applications on webOS"
AUTHOR = "Lokesh Kumar Goel <lokeshkumar.goel@lge.com>"
SECTION = "webos/base"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "virtual/webruntime qtbase luna-service2 sqlite3 librolegen nyx-lib openssl luna-prefs libpbnjson freetype serviceinstaller glib-2.0 pmloglib lttng-ust"
PROVIDES = "webappmanager-webos"

# webappmgr's upstart conf expects to be able to LD_PRELOAD ptmalloc3
RDEPENDS_${PN} = "ptmalloc3"
# webappmgr's upstart conf expects to have ionice available. Under OE-core, this is supplied by util-linux.
RDEPENDS_${PN} += "util-linux"
RDEPENDS_${PN} += "qtbase-plugins"

#  webappmgr2's upstart conf expects setcpushares-task to be available
VIRTUAL-RUNTIME_cpushareholder ?= "cpushareholder-stub"
RDEPENDS_${PN} += "${VIRTUAL-RUNTIME_cpushareholder}"

WEBOS_VERSION[vardeps] += "PREFERRED_PROVIDER_virtual/webruntime"
WEBOS_VERSION = "${@oe.utils.conditional('PREFERRED_PROVIDER_virtual/webruntime', 'webruntime', '1.0.0-2.chromium68.8_fe88c8ad8969be56f13a094a63f1231630280f45', '1.0.0-4_07025b9860e6d8f55d40069548eef3f610780303', d)}"
PR = "r22"

inherit webos_enhanced_submissions
inherit webos_system_bus
inherit webos_machine_dep
inherit webos_qmake5
inherit webos_lttng
inherit webos_distro_variant_dep
inherit webos_distro_dep
inherit webos_public_repo

WAM_DATA_DIR = "${webos_execstatedir}/${BPN}"

SRC_URI = "${WEBOSOSE_GIT_REPO_COMPLETE}"
S = "${WORKDIR}/git"

WEBOS_SYSTEM_BUS_SKIP_DO_TASKS = "1"

WEBOS_SYSTEM_BUS_FILES_LOCATION = ""

OE_QMAKE_PATH_HEADERS = "${OE_QMAKE_PATH_QT_HEADERS}"

WEBOS_QMAKE_TARGET = "${MACHINE}"

# Set the location of chromium headers
EXTRA_QMAKEVARS_PRE += "CHROMIUM_SRC_DIR=${STAGING_INCDIR}/${PREFERRED_PROVIDER_virtual/webruntime}"

# Enable LTTng tracing capability when enabled in webos_lttng class
EXTRA_QMAKEVARS_PRE += "${@oe.utils.conditional('WEBOS_LTTNG_ENABLED', '1', 'CONFIG+=lttng', '', d)}"

EXTRA_QMAKEVARS_PRE += "DEFINES+=WAM_DATA_DIR=\"\"${webos_cryptofsdir}/.webappmanager/\"\""
EXTRA_QMAKEVARS_PRE += "PREFIX=/usr"
EXTRA_QMAKEVARS_PRE += "PLATFORM=${@'PLATFORM_' + '${DISTRO}'.upper().replace('-', '_')}"

# Set number of raster threads for Blink to 2
WEBAPPMANAGER3_NUM_RASTER_THREADS = "2"

# chromium doesn't build for armv[45]*
COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE_aarch64 = "(.*)"
COMPATIBLE_MACHINE_armv6 = "(.*)"
COMPATIBLE_MACHINE_armv7a = "(.*)"
COMPATIBLE_MACHINE_armv7ve = "(.*)"
COMPATIBLE_MACHINE_x86 = "(.*)"
COMPATIBLE_MACHINE_x86-64 = "(.*)"

do_configure_append() {
    cp -f ${S}/files/launch/WebAppMgr.conf.upstart.in ${S}/files/launch/WebAppMgr.conf
    sed -i "s@WAM_DATA_DIR@${WAM_DATA_DIR}@g" ${S}/files/launch/WebAppMgr-finalize.conf.upstart
    sed -i "s@WEBOS_CRYPTOFSDIR@${webos_cryptofsdir}@g" ${S}/files/launch/WebAppMgr.conf
    sed -i "s@WEBOS_SYSMGR_LOCALSTATEDIR@${webos_sysmgr_localstatedir}@g" ${S}/files/launch/WebAppMgr.conf
    sed -i "s@WEBOS_PREFIX@${webos_prefix}@g" ${S}/files/launch/WebAppMgr.conf
    grep 'export HOOK_SEGV' ${S}/files/launch/WebAppMgr.conf || sed -i '/\/usr\/bin\/WebAppMgr/i\    export HOOK_SEGV=NO'  ${S}/files/launch/WebAppMgr.conf
    sed -i "s@WEBOS_NUM_RASTER_THREADS@${WEBAPPMANAGER3_NUM_RASTER_THREADS}@g" ${S}/files/launch/WebAppMgr.conf
    sed -i '/--disable-low-res-tiling \\/a\        --disable-new-video-renderer \\' ${S}/files/launch/WebAppMgr.conf
    sed -i '/--enable-gpu-rasterization \\/a\        --disable-gpu-rasterization-for-first-frame \\' ${S}/files/launch/WebAppMgr.conf
    cp -f ${S}/files/launch/WebAppMgr.conf ${S}/files/launch/WebAppMgr.conf.upstart
}

WAM_ERROR_SCRIPTS_PATH = "${S}/html-ose"

do_configure_append_qemux86() {
    # Remove this condition once webos wam is synchronized to get systemd initscripts
    if [ -f "${S}/files/launch/systemd/webapp-mgr.sh.in" ]; then
        # Disable media hardware acceleration
        sed -i '/--enable-aggressive-release-policy \\/a\   --disable-web-media-player-neva \\' ${S}/files/launch/systemd/webapp-mgr.sh.in
    fi
}

do_install_append() {
    install -d ${D}${sysconfdir}/init
    install -d ${D}${sysconfdir}/pmlog.d
    install -d ${D}${sysconfdir}/wam
    install -d ${D}${WAM_DATA_DIR}
    install -v -m 644 ${S}/files/launch/WebAppMgr.conf.upstart ${D}${sysconfdir}/init/WebAppMgr.conf
    install -v -m 644 ${S}/files/launch/security_policy.conf ${D}${sysconfdir}/wam/security_policy.conf

    # add loaderror.html and geterror.js to next to resources directory (webos_localization_resources_dir)
    install -d ${D}${datadir}/localization/${BPN}/
    cp -vf ${WAM_ERROR_SCRIPTS_PATH}/* ${D}${datadir}/localization/${BPN}/
    if [ "${PREFERRED_PROVIDER_virtual/webruntime}" = "webruntime" ]; then
        install -d ${D}${webos_sysbus_pubservicesdir}
        install -d ${D}${webos_sysbus_pubrolesdir}
        install -d ${D}${webos_sysbus_prvservicesdir}
        install -d ${D}${webos_sysbus_prvrolesdir}
        install -v -m 0644 ${S}/files/sysbus/com.palm.webappmgr.service.pub ${D}${webos_sysbus_pubservicesdir}/com.palm.webappmgr.service
        install -v -m 0644 ${S}/files/sysbus/com.palm.webappmgr.service.prv ${D}${webos_sysbus_prvservicesdir}/com.palm.webappmgr.service
        install -v -m 0644 ${S}/files/sysbus/com.palm.webappmgr.json.prv ${D}${webos_sysbus_prvrolesdir}/com.palm.webappmgr.json
        install -v -m 0644 ${S}/files/sysbus/com.palm.webappmgr.json.pub ${D}${webos_sysbus_pubrolesdir}/com.palm.webappmgr.json
    fi
}

FILES_${PN} += " \
    ${webos_upstartconfdir} \
    ${sysconfdir}/pmlog.d \
    ${sysconfdir}/init \
    ${sysconfdir}/wam \
    ${libdir}/webappmanager/plugins/*.so \
    ${datadir}/localization/${BPN} \
    ${WEBOS_SYSTEM_BUS_DIRS} \
"
