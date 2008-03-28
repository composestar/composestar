# Program to produce
ifeq "$OS" "Windows_NT"
executable = basictest.exe
else
executable = basictest
endif

# List of source files
sources = main.c calc.c 
objects := $(patsubst %.c,%.o,$(sources))

# List of concerns
concerns = mainprint.cps calcfix.cps trace.cps profiling.cps

# Intermediate directory
INTERMED = obj
# Directory for the preprocessed files
PREPROCESSED = $(INTERMED)/preprocessed
# Directory for the woven files
WOVEN = $(INTERMED)/woven

# produce preprocessed and woven source lists
ppSources := $(patsubst %.c,$(PREPROCESSED)/%.c,$(sources))
wovenSources := $(patsubst %.c,$(WOVEN)/%.c,$(sources))
# create list of directories where the preprocessed files will be stored
# the patsubst will remove all trailing slashes
ppDirs := $(patsubst  %/,%,$(sort $(dir $(ppSources))))

# Misc env settings
JAVA = java
CC = gcc -Wall
CFLAGS = 
COMPOSESTAR = $(JAVA) -jar ../../Testing/bin/lib/ComposestarCwC.jar 

ifeq "$(OS)" "Windows_NT"
CP = copy
RM = del /F /Q
RMDIR = cmd /c rmdir /S /Q
MKDIR = mkdir
else
CP = cp
RM = rm -f
RMDIR = rm -R -f
MKDIR = mkdir -p 
endif

# if DOWEAVE != yes then weaving will not be performed
ifndef DOWEAVE
DOWEAVE = yes
endif

.PHONY : all
all : $(executable)

# Make the intermediate directories
.PHONY : init
init : $(ppDirs) $(WOVEN)

# Creates the directories when the preprocessed files will be stored
$(ppDirs) $(WOVEN) :
ifeq "$(OS)" "Windows_NT"
	-$(MKDIR) $(subst /,\,$@)
else
	-$(MKDIR) $@
endif

# Clean 
.PHONY : clean
clean :
	-$(RM) $(executable)
	-$(RM) *.o
	-$(RMDIR) $(INTERMED)

# Produce preprocessed files
$(PREPROCESSED)/%.c : %.c
	$(CPP) -c $(CFLAGS) -dD -C -o $@ $<

ComposeStar.h :
ifeq "$(OS)" "Windows_NT"
	$(CP) ..\..\Compiletime\src\Composestar\CwC\WEAVER\ComposeStar.h .
else
	$(CP) ../../Compiletime/src/Composestar/CwC/WEAVER/ComposeStar.h .
endif

main.c : ComposeStar.h

ifeq ($(DOWEAVE), yes)

# compile the woven sources
$(objects) : %.o : $(WOVEN)/%.c
	$(CC) -c $(CFLAGS) -o $@ $<

# Add the ComposeStar.c file to the list
objects += $(WOVEN)/ComposeStar.o

# Performs the actual weaving	
.PHONY : weave
weave : init $(ppSources)
	$(COMPOSESTAR) --intermediate $(INTERMED) --custom-filter "#Composestar.CwC.Filters.ExtraFilters" $(ppSources) $(concerns)	

$(executable) : weave $(objects)
	$(CC) -o $@ $(LDFLAGS) $(objects)

else

# Compile without weaving

$(objects) : %.o : %.c
	$(CC) -c $(CFLAGS) -o $@ $<

$(executable) : $(objects)
	$(CC) -o $@ $(LDFLAGS) $(objects)

endif
