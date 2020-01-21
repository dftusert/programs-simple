compiler := g++
compilerFlags := -fPIC -Wall -std=c++17 -O2 -c
sources := */*.cxx
build := build
objdir := LogSYS
libname := logsys
all:
	make makeobjs makestaticlib makesharedlib

makeobjs:
	mkdir -p $(build)
	mkdir -p $(build)/$(objdir)
	$(compiler) $(compilerFlags) $(sources)
	mv *.o $(build)/$(objdir)

makestaticlib:
	ar rsv $(build)/$(libname).a $(build)/$(objdir)/*.o

makesharedlib:
	$(compiler) -shared $(build)/$(objdir)/*.o -o $(build)/lib$(libname).so