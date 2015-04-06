/*****************************************************************************
 * Author:                     Justin Hammar
 * Date Created:               8/28/14
 * Last Modification Date:     8/31/14
 * Filename:                   room.cpp
 *
 * Overview:
 *     A class for representing a room.
 *
 *****************************************************************************/

#include <iostream>
using std::cout;
using std::endl;
#include <string>
using std::string;
#include <vector>
using std::vector;

#include "room.h"
#include "roomExit.h"
using HammarTBA::RoomExit;
#include "item.h"
using HammarTBA::Item;

namespace HammarTBA
{
    Room::Room() {
        this->name = "";
        this->description = "";
    }

    Room::Room(string name, string description) {
        this->name = name;
        this->description = description;
    }

    Room::Room(Room& room)
    {
        this->name = room.name;
        this->description = room.description;

        //I only copy over the pointers here. I would also need to copy the
        //RoomExits and Items they point to as well.
        for (int i = 0; i < exits.size(); ++i)
            this->exits.push_back( room.exits[i] );

        for (int i = 0; i < contents.size(); ++i)
            this->contents.push_back( room.contents[i] );;
    }

    string Room::getName() {
        return name;
    }

    string Room::getDescription() {
        return description;
    }


    void Room::listContents()
    {
        if ( contents.size() == 0)
            return;

        cout << "Room contains ";

        int i;
        for (i = 0; i < contents.size() - 1; ++i)
            cout << contents[i]->getName() << ", ";
        if (contents.size() > 1)
            cout << "and ";
        cout << contents[i]->getName() << endl;
    }

    void Room::listExits()
    {
        cout << "Exits are ";

        if ( exits.size() == 0) {
            cout << "non-existent. \n";
            return;
        }

        int i;
        for (i = 0; i < exits.size() - 1; ++i)
            cout << exits[i]->getName() << ", ";
        if (exits.size() > 1)
            cout << "and ";
        cout << exits[i]->getName() << endl;
    }


    void Room::addExit(RoomExit* exit) {
        exits.push_back(exit);
    }

    Room* Room::findExit(string direction)
    {
        for (vector<RoomExit*>::iterator iter = exits.begin(); iter < exits.end(); ++iter)
        {
            if ( (*iter)->getName() == direction ) {
                return (*iter)->getDestination();
            }
        }
        //exit does not exist
        return NULL;
    }


    Item* Room::findItem(string itemName)
    {
        for (vector<Item*>::iterator iter = contents.begin(); iter < contents.end(); ++iter)
        {
            if ( (*iter)->getName() == itemName ) {
                return *iter;
            }
        }
        //there's no such item in the room
        return NULL;
    }

    void Room::addItem(Item* item) {
        contents.push_back(item);
    }

    Item* Room::removeItem(string itemName)
    {
        for (vector<Item*>::iterator iter = contents.begin(); iter < contents.end(); ++iter)
        {
            if ( (*iter)->getName() == itemName ) {
                Item* temp = *iter;
                contents.erase(iter);
                return temp;
            }
        }
        //that item isn't even in the room
        return NULL;
    }

    Room::~Room()
    {
        for (int i = 0; i < exits.size(); ++i) {
            delete exits[i];
            exits[i] = NULL;
        }
        for (int i = 0; i < contents.size(); ++i) {
            delete contents[i];
            contents[i] = NULL;
        }
    }
} // HammarTBA
