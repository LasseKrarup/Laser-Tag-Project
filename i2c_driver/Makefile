# Makefile for i2cdriver.c
#
# This Makefile is a modified version
# of the one used in E3HAL
#
# Modified by: Lasse Krarup

# ======== VARIABLES =========
SOURCES = i2cdriver.c
CXXFLAGS = -I. # used for custom .h files with #include <file.h> notation
CXX = g++
BUILD_DIR = build/$(ARCH)
BIN_DIR = bin/$(ARCH)
EXE = $(BIN_DIR)/$(SOURCES:.c=)
# ===== =============== ======

ifeq ($(KERNELRELEASE),) #only build this way if KERNELRELEASE is not set. Otherwise it means we are already in the kernel directory.

# If KERNELDIR is not set by an environment variable
ifeq ($(KERNELDIR),)
	KERNELDIR = ~/sources/rpi-4.14
endif

# If CCPREFIX is not set by an environment variable
ifeq ($(CCPREFIX),)
	CCPREFIX = arm-poky-linux-gnueabi-
endif


.PHONY: all
all: directories $(EXE)

$(EXE): $(OBJECTS) $(DEPS)
	$(MAKE) ARCH=arm CROSS_COMPILE=$(CCPREFIX) -C $(KERNELDIR) M=$(PWD) modules -o $@
	mv .*.cmd *.o *.symvers *.order *.mod.c build/
	mv *.ko bin/

# ===== CREATE DIRECTORIES ======
.PHONY: directories
directories: $(BUILD_DIR) $(BIN_DIR)
$(BUILD_DIR):
	mkdir -p $(BUILD_DIR)
$(BIN_DIR):
	mkdir -p $(BIN_DIR)
# ===== =============== ======

# ======== PHONY TARGETS ===========
.PHONY: clean help distclean
clean: #remove object and dependency files
	rm -rf .*.cmd *.o *.symvers *.order
distclean: #remove all created folders
	rm -f -r -d build bin .tmp* *.mod.c *.ko
help:
	@echo "The following targets are available for this makefile:"
	@echo "------------------------------------"
	@echo "all [default]: fully compiles the program"
	@echo "clean: removes all object and dependency files"
	@echo "distclean: remove all created folders"
	@echo "directories: create required directories"
	@echo "help: display this help menu"
	@echo "------------------------------------"
# ====================================

else
  # called from kernel build system: just declare what our modules are
  # Avoid c90 warning on mixed declaration
	ccflags-y := -DDEBUG -g -std=gnu99 -Wno-declaration-after-statement -Werror
	obj-m := $(SOURCES:.c=.o)
endif