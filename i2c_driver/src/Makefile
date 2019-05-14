#Makefile for i2c_driver

CXX = g++
CXXFLAGS = -I. -lpthread
EXE = i2c_driver
SOURCES = test_main.cpp i2c_driver.cpp MsgQueue.cpp
OBJECTS = $(SOURCES:.cpp=.o)
DEPS = $(SOURCES:.cpp=.d)

ifeq (${ARCH},target)
CXX=arm-poky-linux-gnueabi-g++
EXE=i2c_driver_crosscompiled
endif

.PHONY: all
all: $(EXE)

$(EXE): $(OBJECTS) $(DEPS)
	$(CXX) $(OBJECTS) -o $@ $(CXXFLAGS)

%.o: %.cpp %.h
	$(CXX) -c $< -o $@ $(CXXFLAGS)

%.d: %.cpp
	$(CXX) -MT $(@:.d=.o) -MM $(CXXFLAGS) $^ > $@

-include $(DEPS)

.PHONY: clean
clean:
	rm -f *.o *.d

dist-clean:
	rm -f *.o *.d $(EXE)

.PHONY: help
help:
	@echo "The following targets are available for this makefile:"
	@echo "------------------------------------"
	@echo "all: fully compiles the program"
	@echo "clean: removes all object files from directory"
	@echo "help: display this help menu"
	@echo "------------------------------------"
