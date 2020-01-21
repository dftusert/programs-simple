CC = gcc
OBJDIR = $(shell pwd)/build

MAIN_SOURCES = main.c
MAIN_CFLAGS = -Wall -O2
LIBS_FLAGS = -lext
BIN = main

EXT_SOURCES = err.c mdunion.c types.c
EXT_CFLAGS = -Wall -O2
EXT_LIBNAME = libext.so

mkdirs:
	mkdir -p $(OBJDIR)/lib
	mkdir -p $(OBJDIR)/bin

extso: mkdirs
	$(CC) $(EXT_CFLAGS) -c $(EXT_SOURCES)
	mv *.o $(OBJDIR)
	$(CC) -shared $(OBJDIR)/*.o -o $(EXT_LIBNAME)
	mv $(EXT_LIBNAME) $(OBJDIR)/lib
	rm $(OBJDIR)/*.o

buildprog: mkdirs extso
	$(CC) $(MAIN_CFLAGS) -c $(MAIN_SOURCES)
	mv *.o $(OBJDIR)
	$(CC) $(OBJDIR)/*.o -L$(OBJDIR)/lib $(LIBS_FLAGS) -o $(BIN)
	mv $(BIN) $(OBJDIR)/bin
	rm $(OBJDIR)/*.o

runprog: mkdirs extso buildprog
	export LD_LIBRARY_PATH=$(OBJDIR)/lib && $(OBJDIR)/bin/$(BIN)

clean:
	rm -rf $(OBJDIR)