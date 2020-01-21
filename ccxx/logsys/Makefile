compiler := g++
flags := -O2 -std=c++17 -Wall -c
incpaths := -Isrc/config -Isrc/fileutils -Isrc/levelsdesc -Isrc/log -Isrc/logsys
builddir := build
libname := logsys

all:
	$(compiler) $(flags) $(incpaths) main.cpp
	$(compiler) main.o src/$(builddir)/$(libname).a -lsqlite3 -o staticout
	$(compiler) -Lsrc/$(builddir) main.o -lsqlite3 -l$(libname) -o dynamicout
	rm main.o