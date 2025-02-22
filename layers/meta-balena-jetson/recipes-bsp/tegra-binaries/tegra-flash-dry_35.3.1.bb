SUMMARY = "Deploy compressed flash artifacts"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BALENA_COREBASE}/COPYING.Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit deploy

PN = "tegra-flash-dry"

BOOTBLOB:jetson-agx-orin-devkit = "boot0_agx_orin_devkit.img.gz"

# TODO: Update boot blobs for Jetson Xavier AGX, Forecr DSB and Orin NX in Xavier NX Devkit on L4T 35.2.1
BOOTBLOB:jetson-orin-nx-xavier-nx-devkit = "boot0_orin_nx_xavier_nx_devkit.img.gz"
BOOTBLOB:jetson-orin-nano-devkit-nvme = "boot0_orin_nano_devkit_nvme.img.gz"

PARTSPEC:jetson-agx-orin-devkit = "partition_specification234.txt"
PARTSPEC:jetson-orin-nx-xavier-nx-devkit = "partition_specification234.txt"
PARTSPEC:jetson-orin-nano-devkit-nvme = "partition_specification234_orin_nano.txt"

BINARY_INSTALL_PATH = "/opt/tegra-binaries/"

SRC_URI = " \
    file://${BOOTBLOB};unpack=0 \
    file://${PARTSPEC} \
"

SRC_URI:append:jetson-agx-orin-devkit = " \
    file://TEGRA_BL_3701_000.Cap.gz;unpack=0 \
    file://TEGRA_BL_3701_300.Cap.gz;unpack=0 \
"

do_install() {
    # Ensure install is not executed until
    # do_unpack copies the archive
    while [ ! -f ${WORKDIR}/${BOOTBLOB} ]
    do
        sleep 1
    done

    install -d ${D}/${BINARY_INSTALL_PATH}
    install ${WORKDIR}/${BOOTBLOB} ${D}/${BINARY_INSTALL_PATH}/boot0.img.gz
    install ${WORKDIR}/${PARTSPEC} ${D}/${BINARY_INSTALL_PATH}/
}

do_install:append:jetson-agx-orin-devkit() {
    install ${WORKDIR}/TEGRA_BL_3701_000.Cap.gz ${D}/${BINARY_INSTALL_PATH}/
    install ${WORKDIR}/TEGRA_BL_3701_300.Cap.gz ${D}/${BINARY_INSTALL_PATH}/
}

do_deploy() {
    rm -rf ${DEPLOY_DIR_IMAGE}/$(basename ${BINARY_INSTALL_PATH}) || true
    mkdir -p ${DEPLOY_DIR_IMAGE}/$(basename ${BINARY_INSTALL_PATH})
    cp -r ${D}/${BINARY_INSTALL_PATH}/* ${DEPLOY_DIR_IMAGE}/$(basename ${BINARY_INSTALL_PATH})
}

FILES:${PN} += " \
    /opt/tegra-binaries/* \
"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

# need to be redeployed on each build
# as this path is not cached
do_install[nostamp] = "1"
do_deploy[nostamp] = "1"
do_unpack[nostamp] = "1"
deltask do_configure
deltask do_compile

addtask do_deploy before do_package after do_install
