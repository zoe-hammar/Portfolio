/*****************************************************************************
 * Author:                     Justin Hammar
 * Date Created:               8/29/14
 * Last Modification Date:     8/31/14
 * Filename:                   roomExit.h
 *
 * Overview:
 *     A class that represents an exit of a room and the room you
 *     can get to by going through that exit.
 *
 *****************************************************************************/

#ifndef ROOMEXIT_H
#define ROOMEXIT_H

#include <string>
using std::string;

#include "room.h"

namespace HammarTBA
{
    class Room;

    class RoomExit
    {
    public:
        RoomExit(string name, Room* destination);

        string getName();
        Room* getDestination();
    private:
        string name;
        Room* destination;
    };
} // HammarTBA

#endif // ROOMEXIT_H
