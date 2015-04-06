/*****************************************************************************
 * Author:                     Justin Hammar
 * Date Created:               8/29/14
 * Last Modification Date:     8/31/14
 * Filename:                   avatar.cpp

 *
 * Overview:
 *     A class that represents an avatar (the player character).
 *
 *****************************************************************************/

#include <iostream>
using std::cout;
using std::endl;
#include <string>
using std::string;
#include <vector>
using std::vector;

#include "avatar.h"
#include "room.h"
using HammarTBA::Room;
#include "item.h"
using HammarTBA::Item;

namespace HammarTBA
{
    Avatar::Avatar(Room* curRoom) {
        this->curRoom = curRoom;
    }


    Room* Avatar::getCurRoom() {
        return curRoom;
    }

    void Avatar::setCurRoom(Room* room) {
        curRoom = room;
    }


    void Avatar::look()
    {
        cout << "*** " << curRoom->getName() << " ***" << endl
            << endl
            << curRoom->getDescription() << endl;
        curRoom->listContents();
        curRoom->listExits();
    }

    void Avatar::examine(string itemName)
    {
        Item* temp = this->findItem(itemName);

        if (temp != NULL) {
            cout << temp->getDescription();
            return;
        }

        temp = curRoom->findItem(itemName);

        if (temp != NULL)
            cout << temp->getDescription();
        else
            cout << "That item isn't on your person or in the room! \n";
    }


    void Avatar::go(string direction)
    {
        //get the room that the exit takes you to (if that exit exists)
        Room* nextRoom = curRoom->findExit(direction);

        if (nextRoom != NULL) {
            curRoom = nextRoom;
            this->look();
        }
        else
            cout << "That's not an exit, man!\n";
    }


    void Avatar::listInventory()
    {
        cout << "Inventory: \n";

        for (int i = 0; i < inventory.size(); ++i)
            cout << inventory[i]->getName() << endl;
    }

    Item* Avatar::findItem(string itemName)
    {
        for (vector<Item*>::iterator iter = inventory.begin(); iter < inventory.end(); ++iter)
        {
            if ( (*iter)->getName() == itemName ) {
                return *iter;
            }
        }
        //there's no such item in the the PC's inventory
        return NULL;
    }

    void Avatar::addItem(Item* item) {
        inventory.push_back(item);
    }


    void Avatar::take(string itemName)
    {
        Item* temp = curRoom->removeItem(itemName);

        if ( temp != NULL ) {
            this->addItem(temp);
            cout << "Taken. \n";
        }
        else
            cout << "There's no such item in the room! \n";
    }

    void Avatar::drop(string itemName)
    {
        Item* temp = NULL;

        for (vector<Item*>::iterator iter = inventory.begin(); iter < inventory.end(); ++iter)
        {
            if ( (*iter)->getName() == itemName ) {
                temp = *iter;
                inventory.erase(iter);
            }
        }

        if ( temp != NULL ) {
            curRoom->addItem(temp);
            cout << "Dropped. \n";
        }
        else
            cout << "You can't drop something you don't have! \n";
    }

    void Avatar::use(string itemName)
    {
        Item* item = this->findItem(itemName);

        if ( item != NULL )
            item->use();
        else
            cout << "You can't use something you don't have! \n";
    }
} // HammarTBA
