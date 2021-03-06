
SHELL=/bin/sh
SED=sed
AWK=awk
DOS2UNIX=/usr/bin/dos2unix



##### Configuration #####
CPPFLAGS_CORE= -D_MP_INTERNAL -D_AMD64_ -D_USE_THREAD_LOCAL -I lib -fopenmp -msse -msse2 -mfpmath=sse
CXXFLAGS_CORE=-fPIC
ifeq ($(MODE),)
Z3_BUILD_MODE=external
else
Z3_BUILD_MODE=$(MODE)
endif
LIBS=
## -lrt is for timer_create and timer_settime
LDFLAGS=-lrt -lpthread -fopenmp
LDFLAGS_EXTRA=
#########################

Z3=z3
TEST_CAPI=test_capi
TEST=test

BIN_DIR=bin/$(Z3_BUILD_MODE)
OBJ_DIR=obj/$(Z3_BUILD_MODE)
SED_OBJ_DIR=obj\/$(Z3_BUILD_MODE)
OBJ_TEST_DIR=obj-test/$(Z3_BUILD_MODE)
SED_OBJ_TEST_DIR=obj-test\/$(Z3_BUILD_MODE)

MAJ=$(shell grep 'Z3_MAJOR_VERSION' lib/version.h | cut -d ' ' -f 4)
MIN=$(shell grep 'Z3_MINOR_VERSION' lib/version.h | cut -d ' ' -f 4)
Z3_VERSION=$(MAJ).$(MIN)

PLATFORM=linux


ifeq ($(Z3_BUILD_MODE),release)
CPPFLAGS=$(CPPFLAGS_CORE) -DNDEBUG 
CXXFLAGS=-O3 -fomit-frame-pointer $(CXXFLAGS_CORE) 
else
ifeq ($(Z3_BUILD_MODE),smtcomp07)
CPPFLAGS=$(CPPFLAGS_CORE) -DNDEBUG -DSMTCOMP
CXXFLAGS=-O3 -fomit-frame-pointer $(CXXFLAGS_CORE) 
# doesn't work on cygwin
LDFLAGS_EXTRA=-static
else
ifeq ($(Z3_BUILD_MODE),external)
CPPFLAGS=$(CPPFLAGS_CORE) -DNDEBUG -D_EXTERNAL_RELEASE
CXXFLAGS=-O3 -fomit-frame-pointer $(CXXFLAGS_CORE) 
# doesn't work on cygwin
LDFLAGS_EXTRA=-static
else
ifeq ($(Z3_BUILD_MODE),debug)
CPPFLAGS=$(CPPFLAGS_CORE) -DZ3DEBUG -D_TRACE 
CXXFLAGS=-g -Wall $(CXXFLAGS_CORE)
else
ifeq ($(Z3_BUILD_MODE),debugopt)
CPPFLAGS=$(CPPFLAGS_CORE) -DNDEBUG -DZ3DEBUG -D_TRACE 
CXXFLAGS=-g -O1 $(CXXFLAGS_CORE)
else
ifeq ($(Z3_BUILD_MODE),trace)
CPPFLAGS=$(CPPFLAGS_CORE) -DNDEBUG -D_TRACE 
CXXFLAGS=-g -O1 $(CXXFLAGS_CORE)
else
ifeq ($(Z3_BUILD_MODE),profile)
CPPFLAGS=$(CPPFLAGS_CORE) -DNDEBUG
CXXFLAGS=-g -pg -O2 $(CXXFLAGS_CORE)
LDFLAGS_EXTRA=-pg
else
ifeq ($(Z3_BUILD_MODE),gcov)
CPPFLAGS=$(CPPFLAGS_CORE) -DNDEBUG
CXXFLAGS=-g -fprofile-arcs -ftest-coverage -O2 $(CXXFLAGS_CORE)
LDFLAGS_EXTRA=-fprofile-arcs -ftest-coverage 
else
ifeq ($(Z3_BUILD_MODE),external_parallel)
CPPFLAGS=$(CPPFLAGS_CORE) -DNDEBUG -D_EXTERNAL_RELEASE -D_Z3_BUILD_PARALLEL_SMT
CXXFLAGS=-O3 -fomit-frame-pointer $(CXXFLAGS_CORE) 
# doesn't work on cygwin
LDFLAGS_EXTRA=-static
else
ifeq ($(Z3_BUILD_MODE),debug_parallel)
CPPFLAGS=$(CPPFLAGS_CORE) -DZ3DEBUG -D_TRACE -D_Z3_BUILD_PARALLEL_SMT
CXXFLAGS=-g -Wall $(CXXFLAGS_CORE)
else
$(error INVALID BUILD MODE = $(Z3_BUILD_MODE))
endif
endif
endif
endif
endif
endif
endif
endif
endif
endif

LIBFLAGS=-shared -fopenmp 

################################
#
# Extract the source files from 
# the MSVC++ project files.
#
# lib.vcproj   ===> lib.srcs
# shell.vcproj ===> shell.srcs
# 
################################

main: $(BIN_DIR)/$(Z3)

lib.srcs: lib/lib.vcxproj
	@echo Making 'lib.srcs'...
	@cp $< lib0.srcs
	@chmod +rw lib0.srcs
	@$(DOS2UNIX) lib0.srcs
	@$(AWK) '/cpp\"/{ print $$0 }' lib0.srcs > lib1.srcs
	@$(SED) 's|\"||g;s|<ClCompile||g;s|/>||g;s|Include=|lib/|g' lib1.srcs > lib2.srcs
	@$(AWK) 'BEGIN { printf ("LIB_SRCS= "); } { printf($$1 " ") } END { print ""; }' lib2.srcs > $@
	@rm -f lib0.srcs
	@rm -f lib1.srcs
	@rm -f lib2.srcs

shell.srcs: shell/shell.vcxproj
	@echo Making 'shell.srcs'...
	@cp $< shell0.srcs
	@chmod +rw shell0.srcs
	@$(DOS2UNIX) shell0.srcs
	@$(AWK) '/cpp\"/{ print $$0 }' shell0.srcs > shell1.srcs
	@$(SED) 's|\"||g;s|<ClCompile||g;s|/>||g;s|Include=|shell/|g' shell1.srcs > shell2.srcs
	@$(AWK) 'BEGIN { printf ("SHELL_SRCS= "); } { printf($$1 " ") } END { print ""; }' shell2.srcs > $@
	@rm -f shell0.srcs
	@rm -f shell1.srcs
	@rm -f shell2.srcs

test.srcs: test/test.vcxproj
	@echo Making 'test.srcs'...
	@cp $< test0.srcs
	@chmod +rw test0.srcs
	@$(DOS2UNIX) test0.srcs
	@$(AWK) '/cpp\"/{ print $$0 }' test0.srcs > test1.srcs
	@$(SED) 's|\"||g;s|<ClCompile||g;s|/>||g;s|Include=|test/|g' test1.srcs > test2.srcs
	@$(AWK) 'BEGIN { printf ("TEST_SRCS= "); } { printf($$1 " ") } END { print ""; }' test2.srcs > $@
	@rm -f test0.srcs
	@rm -f test1.srcs
	@rm -f test2.srcs


include lib.srcs
include shell.srcs
include test.srcs

LIB_SRCS+=
LIB_OBJS=$(LIB_SRCS:lib/%.cpp=$(OBJ_DIR)/%.o)
SHELL_OBJS=$(SHELL_SRCS:shell/%.cpp=$(OBJ_DIR)/%.o)

OBJS=$(LIB_OBJS) $(SHELL_OBJS)
TEST_CAPI_OBJS=$(OBJ_DIR)/test_capi.o $(LIB_OBJS)
TEST_OBJS=$(TEST_SRCS:test/%.cpp=$(OBJ_TEST_DIR)/%.o)

$(BIN_DIR)/$(Z3): $(OBJ_DIR) $(BIN_DIR) $(OBJS)
	@mkdir -p $(BIN_DIR)
	$(CXX) -o $(BIN_DIR)/$(Z3) $(OBJS) $(LDFLAGS) $(LDFLAGS_EXTRA) $(LIBS)

$(BIN_DIR)/$(TEST): $(OBJ_DIR) $(BIN_DIR) $(LIB_OBJS) $(TEST_OBJS)
	@mkdir -p $(BIN_DIR)
	$(CXX) -o $(BIN_DIR)/$(TEST) $(LIB_OBJS) $(TEST_OBJS) $(LDFLAGS) $(LDFLAGS_EXTRA) $(LIBS)

$(BIN_DIR)/$(TEST_CAPI): $(OBJ_DIR) $(BIN_DIR) $(TEST_CAPI_OBJS)
	@mkdir -p $(BIN_DIR)
	$(CXX) -o $(BIN_DIR)/$(TEST_CAPI) $(TEST_CAPI_OBJS) $(LDFLAGS) $(LDFLAGS_EXTRA) $(LIBS)

$(BIN_DIR)/lib$(Z3).so: $(OBJ_DIR) $(BIN_DIR) $(LIB_OBJS)
	@mkdir -p $(BIN_DIR)
	$(CXX) -o $(BIN_DIR)/lib$(Z3).so  $(LIB_OBJS) $(LIBFLAGS) $(LIBS) 

$(BIN_DIR)/lib$(Z3).a: $(OBJ_DIR) $(BIN_DIR) $(LIB_OBJS)
	@mkdir -p $(BIN_DIR)
	ar -cvr $(BIN_DIR)/lib$(Z3).a $(LIB_OBJS)

$(BIN_DIR):
	mkdir -p $(BIN_DIR)

$(OBJ_DIR):
	mkdir -p $(OBJ_DIR)

$(OBJ_TEST_DIR):
	mkdir -p $(OBJ_TEST_DIR)

smtcomp07: $(BIN_DIR)/$(Z3) 
	rm -r -f z3
	mkdir z3
	cp $(BIN_DIR)/$(Z3) z3/run
	strip z3/run
	cp doc/MAGIC_NUMBER z3/
	cp doc/README-SMTCOMP07 z3/README
	cp doc/NOTICES-SMTCOMP07 z3/NOTICES
	cp doc/z3.pdf z3/
	tar -czf z3.tar.gz z3

test_capi: $(BIN_DIR)/$(TEST_CAPI)

so: $(BIN_DIR)/lib$(Z3).so

a: $(BIN_DIR)/lib$(Z3).a

test: $(BIN_DIR)/$(TEST)

################################
#
# Grobner
# 
################################
lib/grobner_main.cpp: test/igrobner.cpp
	cp test/igrobner.cpp lib/grobner_main.cpp
	chmod +rw lib/grobner_main.cpp

$(OBJ_DIR)/grobner_main.o: lib/grobner_main.cpp
	@mkdir -p $(OBJ_DIR)
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) -DGB_APP -c -o $@ $<

################################
#
# .cpp ===> .o
# 
################################

$(OBJ_DIR)/%.o : lib/%.cpp
	@mkdir -p $(OBJ_DIR)
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) -c -o $@ $<

$(OBJ_TEST_DIR)/%.o : test/%.cpp
	@mkdir -p $(OBJ_TEST_DIR)
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) -c -o $@ $<

$(OBJ_DIR)/%.o : shell/%.cpp
	@mkdir -p $(OBJ_DIR)
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) -c -o $@ $<

$(OBJ_DIR)/%.o : test_capi/%.c
	@mkdir -p $(OBJ_DIR)
	$(CXX)  $(CPPFLAGS) $(CXXFLAGS) -I ../lib -c -o $@ $<

################################
#
# Dependency files
#
# .cpp ===> .d
# 
################################

$(OBJ_DIR)/%.d: lib/%.cpp
	@echo Making dependency file \'$@\' ... 
	@mkdir -p $(OBJ_DIR)
	@$(SHELL) -ec '$(CXX) -MM $(CPPFLAGS) $< | $(SED) '\''s/\($*\)\.o[ :]*/$(SED_OBJ_DIR)\/\1.o $(SED_OBJ_DIR)\/\1.d : /g'\'' > $@; [ -s $@ ] || rm -f $@'

$(OBJ_DIR)/%.d: shell/%.cpp
	@echo Making dependency file \'$@\' ... 
	@mkdir -p $(OBJ_DIR)
	@$(SHELL) -ec '$(CXX) -MM $(CPPFLAGS) $< | $(SED) '\''s/\($*\)\.o[ :]*/$(SED_OBJ_DIR)\/\1.o $(SED_OBJ_DIR)\/\1.d : /g'\'' > $@; [ -s $@ ] || rm -f $@'

$(OBJ_TEST_DIR)/%.d: test/%.cpp
	@echo Making dependency file \'$@\' ... 
	@mkdir -p $(OBJ_TEST_DIR)
	@$(SHELL) -ec '$(CXX) -MM $(CPPFLAGS) $< | $(SED) '\''s/\($*\)\.o[ :]*/$(SED_OBJ_TEST_DIR)\/\1.o $(SED_OBJ_TEST_DIR)\/\1.d : /g'\'' > $@; [ -s $@ ] || rm -f $@'


include $(LIB_SRCS:lib/%.cpp=$(OBJ_DIR)/%.d)
include $(SHELL_SRCS:shell/%.cpp=$(OBJ_DIR)/%.d)
include $(TEST_SRCS:test/%.cpp=$(OBJ_TEST_DIR)/%.d)

################################
#
# Cleanup
# 
################################
.PHONY: clean 

clean:
	rm -f $(BIN_DIR)/$(Z3)
	rm -f $(OBJ_DIR)/*
	rm -f lib.srcs
	rm -f shell.srcs
	find . -name '*.bb' -exec rm -f '{}' ';'
	find . -name '*.bbg' -exec rm -f '{}' ';'
	find . -name '*.da' -exec rm -f '{}' ';'
	find . -name '*.gcov' -exec rm -f '{}' ';'
	find . -name 'cachegrind*' -exec rm -f '{}' ';'
	find . -name 'a.out' -exec rm -f '{}' ';'
	find . -name 'a.exe' -exec rm -f '{}' ';'
	find . -name 'core' -exec rm -f '{}' ';'

################################
#
# Release
# 
# NOTE: In 64-bit systems it is not possible to build a dynamic library using static gmp.
# So, EXTRA_LIBS="" in 64-bit systems.
#     EXTRA_LIBS="$(BIN_DIR)/lib$(Z3)-gmp.so" in 32-bit systems.
################################
release: $(BIN_DIR)/$(Z3) $(BIN_DIR)/lib$(Z3).so  $(BIN_DIR)/lib$(Z3).a
	@rm -f -r z3
	@mkdir -p z3
	@mkdir -p z3/bin
	@mkdir -p z3/lib
	@mkdir -p z3/include
	@mkdir -p z3/examples
	@mkdir -p z3/ocaml
	@mkdir -p z3/python
	@mkdir -p z3/examples/c	
	@mkdir -p z3/examples/c++	
	@mkdir -p z3/examples/python
	@mkdir -p z3/examples/maxsat	
	@mkdir -p z3/examples/theory	
	@mkdir -p z3/examples/ocaml
	@cp lib/z3.h z3/include
	@cp lib/z3_v1.h z3/include
	@cp lib/z3_api.h z3/include
	@cp lib/z3_macros.h z3/include
	@cp ml/z3_stubs.c z3/ocaml
	@cp ml/z3_theory_stubs.c z3/ocaml
	@cp ml/z3.mli z3/ocaml
	@cp ml/z3.ml z3/ocaml
	@cp ml_release/build-lib.sh z3/ocaml
	@$(DOS2UNIX) z3/ocaml/build-lib.sh
	@chmod +rwx z3/ocaml/build-lib.sh
	@cp ml_release/README_$(PLATFORM) z3/ocaml/README
	@$(DOS2UNIX) z3/ocaml/README
	@cp ml_release/build-test.sh z3/examples/ocaml
	@$(DOS2UNIX) z3/examples/ocaml/build-test.sh
	@chmod +rwx z3/examples/ocaml/build-test.sh
	@cp ml_release/README_test_$(PLATFORM) z3/examples/ocaml/README
	@$(DOS2UNIX) z3/examples/ocaml/README
	@$(DOS2UNIX) z3/include/*
	@cp $(BIN_DIR)/$(Z3) z3/bin
	@cp $(BIN_DIR)/lib$(Z3).so z3/lib
	@cp $(BIN_DIR)/lib$(Z3).a z3/lib
	@cp test_capi/test_capi.c z3/examples/c
	@$(DOS2UNIX) z3/examples/c/test_capi.c
	@cp test_capi/README-$(PLATFORM).txt z3/examples/c/README
	@$(DOS2UNIX) z3/examples/c/README
	@cp test_capi/build-external-$(PLATFORM).sh z3/examples/c/build.sh
	@cp test_capi/build-static-$(PLATFORM).sh z3/examples/c/build-static.sh
	@$(DOS2UNIX) z3/examples/c/build.sh
	@chmod +rwx z3/examples/c/build.sh
	@$(DOS2UNIX) z3/examples/c/build-static.sh
	@chmod +rwx z3/examples/c/build-static.sh
	@cp test_capi/exec-external-$(PLATFORM).sh z3/examples/c/exec.sh
	@$(DOS2UNIX) z3/examples/c/exec.sh
	@chmod +rwx z3/examples/c/exec.sh
	@cp maxsat/maxsat.c z3/examples/maxsat
	@$(DOS2UNIX) z3/examples/maxsat/maxsat.c
	@cp maxsat/README-$(PLATFORM).txt z3/examples/maxsat/README
	@$(DOS2UNIX) z3/examples/maxsat/README
	@cp maxsat/build-external-$(PLATFORM).sh z3/examples/maxsat/build.sh
	@cp maxsat/build-static-$(PLATFORM).sh z3/examples/maxsat/build-static.sh
	@$(DOS2UNIX) z3/examples/maxsat/build.sh
	@chmod +rwx z3/examples/maxsat/build.sh
	@$(DOS2UNIX) z3/examples/maxsat/build-static.sh
	@chmod +rwx z3/examples/maxsat/build-static.sh
	@cp maxsat/exec-external-$(PLATFORM).sh z3/examples/maxsat/exec.sh
	@$(DOS2UNIX) z3/examples/maxsat/exec.sh
	@chmod +rwx z3/examples/maxsat/exec.sh
	@cp test_user_theory/test_user_theory.c z3/examples/theory
	@$(DOS2UNIX) z3/examples/theory/test_user_theory.c
	@cp test_user_theory/README-$(PLATFORM).txt z3/examples/theory/README
	@$(DOS2UNIX) z3/examples/theory/README
	@cp test_user_theory/build-external-$(PLATFORM).sh z3/examples/theory/build.sh
	@cp test_user_theory/build-static-$(PLATFORM).sh z3/examples/theory/build-static.sh
	@$(DOS2UNIX) z3/examples/theory/build.sh
	@chmod +rwx z3/examples/theory/build.sh
	@$(DOS2UNIX) z3/examples/theory/build-static.sh
	@chmod +rwx z3/examples/theory/build-static.sh
	@cp test_user_theory/exec-external-$(PLATFORM).sh z3/examples/theory/exec.sh
	@$(DOS2UNIX) z3/examples/theory/exec.sh
	@chmod +rwx z3/examples/theory/exec.sh
	@cp ml_release/exec-$(PLATFORM).sh z3/examples/ocaml/exec.sh
	@$(DOS2UNIX) z3/examples/ocaml/exec.sh
	@chmod +rwx z3/examples/ocaml/exec.sh
	@cp ml/test_mlapi.ml z3/examples/ocaml
	@$(DOS2UNIX) z3/examples/ocaml/test_mlapi.ml
	@cp c++/z3++.h z3/include
	@cp c++/example.cpp z3/examples/c++
	@cp c++/build-external-$(PLATFORM).sh z3/examples/c++/build.sh
	@$(DOS2UNIX) z3/examples/c++/build.sh
	@chmod +rwx z3/examples/c++/build.sh
	@cp c++/exec-external-$(PLATFORM).sh z3/examples/c++/exec.sh
	@$(DOS2UNIX) z3/examples/c++/exec.sh
	@chmod +rwx z3/examples/c++/exec.sh
	@cp python/z3.py z3/python
	@cp python/z3core.py z3/python
	@cp python/z3types.py z3/python
	@cp python/z3consts.py z3/python
	@cp python/z3tactics.py z3/python
	@cp python/z3printer.py z3/python
	@cp python/README-$(PLATFORM).txt z3/examples/python/README
	@cp python/exec-$(PLATFORM).sh z3/examples/python/exec.sh
	@cp python/example.py z3/examples/python
	@$(DOS2UNIX) z3/python/*.py
	@$(DOS2UNIX) z3/examples/python/*.py
	@$(DOS2UNIX) z3/examples/python/*.sh
	@chmod +rwx z3/examples/python/*.sh
	@$(DOS2UNIX) iZ3/pack-iz3-$(PLATFORM).sh
	@chmod +rwx iZ3/pack-iz3-$(PLATFORM).sh
	@iZ3/pack-iz3-$(PLATFORM).sh
	@tar -cvzf z3.tar.gz z3

################################
#
# Support
# 
################################

Makefile: Makefile.in config.status
	./config.status
	make

config.status: configure
	./config.status --recheck

################################
#
# checkgmake
# 
################################
.PHONY: checkgmake

checkgmake:
	@ ./gmaketest --make=$(MAKE) || \
          (echo "Z3 needs GNU-Make to be built"; exit 1)

