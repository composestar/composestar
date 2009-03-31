# Program to produce
ifeq "$(OS)" "Windows_NT"
executable = outputfilter.exe
else
executable = outputfilter
endif

# List of source files
sources = main.c  
objects := $(patsubst %.c,%.o,$(sources))

# List of concerns
concerns = offoobar.cps

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
CC = gcc -Wall
CFLAGS = 
RM = del /F /Q
RMDIR = cmd /c rmdir /S /Q
MKDIR = mkdir 

COMPOSESTAR = java -jar ../../Testing/bin/lib/ComposestarCwC.jar 

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
	-$(MKDIR) $(subst /,\,$@)

# Clean 
.PHONY : clean
clean :
	-$(RM) *.o
	-$(RMDIR) $(INTERMED)

# Produce preprocessed files
$(PREPROCESSED)/%.c : %.c
	$(CPP) -c $(CFLAGS) -dD -C -o $@ $<

ComposeStar.h :
	copy ..\..\Compiletime\src\Composestar\CwC\WEAVER\ComposeStar.h .

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
	$(COMPOSESTAR) --intermediate $(INTERMED) $(ppSources) $(concerns)	

$(executable) : weave $(objects)
	$(CC) -o $@ $(LDFLAGS) $(objects)

else

# Compile without weaving

$(objects) : %.o : %.c
	$(CC) -c $(CFLAGS) -o $@ $<

$(executable) : $(objects)
	$(CC) -o $@ $(LDFLAGS) $(objects)

endif
