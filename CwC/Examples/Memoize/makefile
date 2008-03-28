# Program to produce
ifeq "$OS" "Windows_NT"
executable = fibonacci.exe
else
executable = fibonacci
endif

# List of source files
sources = fibonacci.c 
objects := $(patsubst %.c,%.o,$(sources))

# List of concerns
concerns = memoize.cps

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

ifeq "$OS" "Windows_NT"
RM = del /F /Q
RMDIR = cmd /c rmdir /S /Q
MKDIR = mkdir
else
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
ifeq "$OS" "Windows_NT"
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

ifeq ($(DOWEAVE), yes)

# compile the woven sources
$(objects) : %.o : $(WOVEN)/%.c
	$(CC) -I. -c $(CFLAGS) -o $@ $<

# Add the ComposeStar.c file to the list
objects += $(WOVEN)/ComposeStar.o
objects += caching.o

# Performs the actual weaving	
.PHONY : weave
weave : init $(ppSources)
	$(COMPOSESTAR) --intermediate $(INTERMED) --custom-filter CachingFilters.jar#CachingFilters.Caching $(ppSources) $(concerns)	

$(executable) : weave $(objects)
	$(CC) -o $@ $(LDFLAGS) $(objects)

else

# Compile without weaving

$(objects) : %.o : %.c
	$(CC) -c $(CFLAGS) -o $@ $<

$(executable) : $(objects)
	$(CC) -o $@ $(LDFLAGS) $(objects)

endif
