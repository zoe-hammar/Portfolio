/*****************************************************************************
 * Author:                     Justin Hammar
 * Date Created:               8/31/14
 * Last Modification Date:     8/31/14
 * Filename:                   saveLoad.h
 *
 * Overview:
 *     Functions that allow you to create and load save files.
 *
 *****************************************************************************/

#ifndef SAVELOAD_H
#define SAVELOAD_H

#include "avatar.h"

namespace HammarTBA
{
    //creates a save file that has the file name that the user specifies
    void save(Avatar& pc);
    //loads the previously saved file that the user specifies
    void load(const char fileName[], Avatar& pc, vector<Room*>& world);
    void load(Avatar& pc, vector<Room*>& world);
} // HammarTBA

#endif // SAVELOAD_H
