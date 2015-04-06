#//-----------------------------------------------------------------
#//  Requirement #20: Demonstrate a makefile
#//-----------------------------------------------------------------
#this is a comment

CXX = g++
CXXFLAGS = -Wall -c -std=c++0x
CXXFLAGS += -pedantic-errors
LDFLAGS =

OBJS = parser.o avatar.o room.o roomExit.o item.o  main.o

SRCS = parser.cpp avatar.cpp room.cpp roomExit.cpp item.cpp main.cpp

HEADERS = parser.h avatar.h room.h roomExit.h item.h 

tba: ${OBJS} ${HEADERS}
	${CXX} ${LDFLAGS} ${OBJS} -o $@

${OBJS}: ${SRCS}
	${CXX} ${CXXFLAGS} -c $(@:.o=.cpp)
