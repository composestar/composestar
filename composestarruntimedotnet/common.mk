ifeq ($(OS), Darwin)
	SEP=:
	MAKE=make
	RECURSE_MAKE_RULE=if [ "$(SUBDIRS)" != "" ]; then for a in $(SUBDIRS); do cd $$a && $(MAKE) && cd ..; done fi
	RECURSE_CLEAN_RULE=if [ "$(SUBDIRS)" != "" ]; then for a in $(SUBDIRS); do cd $$a && $(MAKE) clean && cd ..; done fi
	DEL=rm -f
	MKDIR=mkdir
	COPY=cp -r
	filecopy=cp
	RMDIR=rm -rf
	NULL=
	INSTALLDIR=/var/bin/
	INSTALLFINDER=$(MAKE) installit
	PATHSEP=/
else 
ifeq ($(OS), linux)
	SEP=:
	MAKE=make
	RECURSE_MAKE_RULE=if [ "$(SUBDIRS)" != "" ]; then for a in $(SUBDIRS); do cd $$a && $(MAKE) && cd ..; done fi
	RECURSE_CLEAN_RULE=if [ "$(SUBDIRS)" != "" ]; then for a in $(SUBDIRS); do cd $$a && $(MAKE) clean && cd ..; done fi
	DEL=rm -f
	MKDIR=mkdir
	COPY=cp -r
	filecopy=cp
	RMDIR=rm -rf
	NULL=
	INSTALLDIR=/var/bin/
	INSTALLFINDER=$(MAKE) installit
	PATHSEP=/
else
	SEP=;
	MAKE=make.exe
	RECURSE_MAKE_RULE=if not "$(SUBDIRS)" == "" for %%a in ( $(SUBDIRS) ) do cd %%a && $(MAKE) && cd..
	RECURSE_CLEAN_RULE=if not "$(SUBDIRS)" == "" for %%a in ( $(SUBDIRS) ) do cd %%a && $(MAKE) clean && cd..
	DEL=del
	MKDIR=md
	COPY=xcopy /s /y
	fileCopy=copy /Y
	RMDIR=rmdir /s /q
	NULL=2>NUL
	INSTALLFINDER=installIt.bat $(MAKE)
	PATHSEP=\\
endif
endif
