/*****************************************************************************
 * Author:                     Justin Hammar
 * Date Created:               8/29/14
 * Last Modification Date:     8/31/14
 * Filename:                   item.h
 *
 * Overview:
 *     A class that represents an item.
 *
 *****************************************************************************/

#ifndef ITEM_H
#define ITEM_H

#include <string>
using std::string;
#include <vector>
using std::vector;

namespace HammarTBA
{
    class Item
    {
    public:
        Item(string name, string description);
        string getName();
        string getDescription();
        virtual void use() = 0;
        Item& operator=(Item& rhs);
    protected:
        string name;
        string description;
    };
} // HammarTBA

#endif // ITEM_H
