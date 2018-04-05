EXTENDPRAUTO_append = "wand1"

EXTRA_PROVIDES_remove = "virtual/libgl"
PACKAGES_remove = "libgl-mx6-dev"

do_install_append () {
    rm -f ${D}${libdir}/libGL.so*
}
