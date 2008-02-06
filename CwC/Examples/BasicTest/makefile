
sources = main.c calc.c
objects := $(patsubst %.c,%.o,$(sources))

concerns = mainprint.cps calcfix.cps

# Intermediate directory
INTERMED = obj
# Directory for the preprocessed files
PREPROCESSED = $(INTERMED)/preprocessed
# Directory for the woven files
WOVEN = $(INTERMED)/woven

# produce preprocessed and woven source lists
ppSources := $(patsubst %.c,$(PREPROCESSED)/%.c,$(sources))
wovenSources := $(patsubst %.c,$(WOVEN)/%.c,$(sources))

# Misc env settings
CC = gcc
CFLAGS = 
RM = del /F /Q
RMDIR = cmd /c rmdir /S /Q

COMPOSESTAR = ../../Testing/bin/lib/ComposestarCwC.jar 

.PHONY : all
all : basictest.exe

# Make the directories
.PHONY : init
init : $(INTERMED) $(PREPROCESSED) $(WOVEN)

$(INTERMED) $(PREPROCESSED) $(WOVEN):
	mkdir $(subst /,\,$@)

# Clean 
.PHONY : clean
clean :
	-$(RM) *.o
	-$(RMDIR) $(INTERMED)

# Produce preprocessed files
$(PREPROCESSED)/%.c : %.c
	$(CPP) -c $(CFLAGS) -o $@ $<

# compile 
$(objects) : %.o : $(WOVEN)/%.c
	$(CC) -c $(CFLAGS) -o $@ $<

# Performs the actual weaving	
.PHONY : weave
weave : $(ppSources)
	java -jar $(COMPOSESTAR) --intermediate $(INTERMED) $(ppSources) $(concerns)

# dummy, to fake weaving
#$(WOVEN)/%.c : $(PREPROCESSED)/%.c
#	type $(subst /,\,$<) > $(subst /,\,$@)


basictest.exe : init weave $(objects) $(WOVEN)/ComposeStar.o
	$(CC) -o $@ $(objects) $(WOVEN)/ComposeStar.o

  
