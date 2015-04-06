/*****************************************************************************
 * Author:                     Justin Hammar
 * Date Created:               8/29/14
 * Last Modification Date:     8/31/14
 * Filename:                   item.cpp
 *
 * Overview:
 *     A class that represents an item.
 *
 *****************************************************************************/

#include <string>
using std::string;
#include <vector>
using std::vector;

#include "item.h"

namespace HammarTBA
{
    Item::Item(string name, string description) {
        this->name = name;
        this->description = description;
    }


    string Item::getName() {
        return name;
    }
    string Item::getDescription() {
        return description;
    }


    Item& Item::operator=(Item& rhs) {
        name = rhs.name;
        description = rhs.description;
    }
} // HammarTBA
