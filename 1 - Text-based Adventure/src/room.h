/*****************************************************************************
 * Author:                     Justin Hammar
 * Date Created:               8/28/14
 * Last Modification Date:     8/31/14
 * Filename:                   room.h
 *
 * Overview:
 *     A class for representing a room.
 *
 *****************************************************************************/

#ifndef ROOM_H
#define ROOM_H

#include <string>
using std::string;
#include <vector>
using std::vector;

#include "roomExit.h"
#include "item.h"

namespace HammarTBA
{
    class RoomExit;

    class Room
    {
    public:
        Room();
        Room(string name, string description);
        Room(Room& room);

        string getName();
        string getDescription();

        void listContents();
        void listExits();

        void addExit(RoomExit* exit);
        Room* findExit(string direction);

        Item* findItem(string itemName);
        void addItem(Item *item);
        Item* removeItem(string itemName);

        ~Room();
    protected:
        string name;
        string description;
        vector<RoomExit*> exits;
        vector<Item*> contents;
    };
} // HammarTBA

#endif // ROOM_H
