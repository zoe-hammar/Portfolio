/*****************************************************************************
 * Author:                     Justin Hammar
 * Date Created:               8/29/14
 * Last Modification Date:     8/31/14
 * Filename:                   roomExit.cpp
 *
 * Overview:
 *     A class that represents an exit of a room and the room you
 *     can get to by going through that exit.
 *
 *****************************************************************************/

#include <string>
using std::string;

#include "roomExit.h"
#include "room.h"
using HammarTBA::Room;

namespace HammarTBA
{
    RoomExit::RoomExit(string name, Room* destination) {
        this->name = name;
        this->destination = destination;
    }

    string RoomExit::getName() {
        return name;
    }
    Room* RoomExit::getDestination() {
        return destination;
    }
} // HammarTBA
