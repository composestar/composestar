ROOTDIR=./

include ${ROOTDIR}common.mk

all: build 

.PHONY: src \
	test \
	clean

help:
	@echo **************************
	@echo  Compose* Building system
	@echo **************************
	@echo Usage: Make command options
	@echo Commands:
	@echo       all (Builds the system, installs it and tests it)
	@echo       clean (Removes all builded files)
	@echo       help (Displays this text)
	@echo       build (Builds the system and installs it)
	@echo       rebuild (Rebuilds the system and installs it)
	@echo       src (Just builds the system)
	@echo       install (Installs the system)
	@echo       runtime (Builds the runtime)
	@echo       test (RBuilds and runs the unit tests)
	@echo       build_test (Builds the unit tests)
	@echo       run_test (runs the unit tests)

rebuild: clean build

build: src

runtime: src
	
src:
	cd src&&cd CODER&&cd breakpoint&&cd parsers&&${MAKE}&&cd ..&&cd ..&&cd ..&&cd ..

clean:
	cd src&&cd CODER&&cd breakpoint&&cd parsers&&${MAKE}&&cd ..&&cd ..&&cd ..&&cd ..

install:
	@echo NO PLATFORM