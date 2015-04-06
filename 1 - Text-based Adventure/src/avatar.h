/*****************************************************************************
 * Author:                     Justin Hammar
 * Date Created:               8/29/14
 * Last Modification Date:     8/31/14
 * Filename:                   avatar.h
 *
 * Overview:
 *     A class that represents an avatar (the player character).
 *
 *****************************************************************************/

#ifndef AVATAR_H
#define AVATAR_H

#include <string>
using std::string;
#include <vector>
using std::vector;

#include "room.h"
#include "item.h"

namespace HammarTBA
{
    class Avatar
    {
    public:
        Avatar(Room* curRoom);

        Room* getCurRoom();
        void setCurRoom(Room* room);

        void look();
        void examine(string itemName);
        void go(string direction);

        void listInventory();
        Item* findItem(string itemName);
        void addItem(Item *item);

        void take(string itemName);
        void drop(string itemName);
        void use(string itemName);
    private:
        Room* curRoom;
        vector<Item*> inventory;
    };
} // HammarTBA

#endif // AVATAR_H
