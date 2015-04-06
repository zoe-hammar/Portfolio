/*****************************************************************************
 * Author:                     Justin Hammar
 * Date Created:               8/31/14
 * Last Modification Date:     8/31/14
 * Filename:                   amulet.cpp
 *
 * Overview:
 *     A class that represents a magic amulet. Using it teleports you out of
 *     dungeon, allowing you to win the game.
 *
 *****************************************************************************/

#include <iostream>
using std::cout;
using std::endl;
#include <cstdlib>

#include "amulet.h"
using HammarTBA::Amulet;

namespace HammarTBA
{
    Amulet::Amulet(string name, string description) : Item(name, description) {
        isFirstUse = true;
    }

    void Amulet::use()
    {
        if (isFirstUse)
        {
            cout << "The amulet doesn't respond. Your magic item skills are \n"
                << "obviously rusty, but I'm sure you'll get the hang of it \n"
                << "if you just keep trying to USE it. \n";
            isFirstUse = false;
        }
        else
        {
            cout << "After fiddling with it for awhile, you manage to activate \n"
                << "the AMULET! It begins to speak to you. \n"
                << endl
                << "\"I will transport you out of this dungeon, but first I \n"
                << "must demonstrate some of my knowledge of computer science. \n"
                << "Please bear with me. \n"
                << endl;
            cout << "The AMULET shines bright and then makes a sound like VOIP! \n"
                << "You find yourself in green pastures. You're FREE! \n";
            exit(0);
        }
    }
} // HammarTBA
