/*****************************************************************************
 * Author:                     Justin Hammar
 * Date Created:               8/31/14
 * Last Modification Date:     8/31/14
 * Filename:                   amulet.h
 *
 * Overview:
 *     A class that represents a magic amulet. Using it teleports you out of
 *     dungeon, allowing you to win the game.
 *
 *****************************************************************************/

#ifndef AMULET_H
#define AMULET_H

#include "item.h"

namespace HammarTBA
{
    class Amulet : public Item
    {
    public:
        Amulet(string name, string description);
        virtual void use();
    protected:
        //keeps track of whether this is the first time the amulet is used or not
        bool isFirstUse;
    };
} // HammarTBA

#endif // AMULET_h
