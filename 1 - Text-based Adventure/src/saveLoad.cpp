/*****************************************************************************
 * Author:                     Justin Hammar
 * Date Created:               8/31/14
 * Last Modification Date:     8/31/14
 * Filename:                   saveLoad.cpp
 *
 * Overview:
 *     Functions that allow you to create and load save files.
 *
 *****************************************************************************/

#include <fstream>
using std::ifstream;
using std::ofstream;
#include <iostream>
using std::cout;
using std::cin;
using std::getline;
using std::endl;
using std::ios;
#include <string>
using std::string;
#include <vector>
using std::vector;
#include <cstdlib>

#include "saveLoad.h"
#include "avatar.h"
using HammarTBA::Avatar;
#include "room.h"
using HammarTBA::Room;

namespace HammarTBA
{
    void save(Avatar& pc)
    {
        string input;
        ofstream fout;

        fout.setf(ios::fixed | ios::showpoint);

        cout << endl
            << "What should the save file be called? (include extension) ";
        cin >> input;
        cin.ignore(1024, '\n');

        fout.open( input.c_str() );
        if ( fout.fail() )
        {
            cout << "Unable to open file. Exiting.";
            exit(1);
        }

        //print the name of the room the player is currently in into the save file
        fout << pc.getCurRoom()->getName() << endl;

        cout << endl
            << "Write to " << input << " was successful. \n"
            << endl;

        fout.close();
    }

    void load(const char fileName[], Avatar& pc, vector<Room*>& world)
    {
        ifstream fin;
        string data;

        fin.open(fileName);
        if ( fin.fail() )
        {
            cout << "Unable to open file. Exiting.";
            exit(1);
        }

        getline(fin, data);

        for (vector<Room*>::iterator iter = world.begin(); iter < world.end(); ++iter)
        {
            if ( (*iter)->getName() == data )
            {
                pc.setCurRoom(*iter);
                fin.close();
                cout << endl
                    << "Save file read successfully. \n"
                    << endl
                    << endl
                    << endl;
                pc.look();
                return;
            }
        }

        cout << "Invalid save file. Could not find listed room. Exiting program. \n";

        fin.close();

        exit(1);
    }

    void load(Avatar& pc, vector<Room*>& world)
    {
        string input;
        ifstream fin;
        string data;

        cout << endl
            << "What it the name of the save file? (include extension) ";
        cin >> input;
        cin.ignore(1024, '\n');

        fin.open( input.c_str() );
        if ( fin.fail() )
        {
            cout << "Unable to open file. Exiting.";
            exit(1);
        }

        getline(fin, data);

        for (vector<Room*>::iterator iter = world.begin(); iter < world.end(); ++iter)
        {
            if ( (*iter)->getName() == data )
            {
                pc.setCurRoom(*iter);
                fin.close();
                cout << endl
                    << "Save file read successfully. \n"
                    << endl
                    << endl
                    << endl;
                pc.look();
                return;
            }
        }

        cout << "Invalid save file. Could not find listed room. Exiting program. \n";

        fin.close();

        exit(1);
    }
} // HammarTBA
