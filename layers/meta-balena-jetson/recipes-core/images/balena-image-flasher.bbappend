include balena-image.inc

IMAGE_INSTALL:remove:jetson-orin-nx-xavier-nx-devkit="l4t-launcher-extlinux"
IMAGE_INSTALL:remove:forecr-dsb-ornx-lan-orin-nx-emmc="l4t-launcher-extlinux"
IMAGE_INSTALL:append = "efitools-utils efibootmgr"

