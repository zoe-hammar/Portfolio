/*****************************************************************************
 * Author:                     Justin Hammar
 * Date Created:               8/30/14
 * Last Modification Date:     8/31/14
 * Filename:                   main.cpp
 *
 * Overview:
 *     This handles the details of a specific text-based adventure. It also
 *     is responsible for
 * Input:
 *     A command-line argument for loading a save file and typed commands
 *     from the keyboard.
 * Output:
 *     None. Handled by other parts of the program.
 *****************************************************************************/

#include <iostream>
using std::cout;
using std::cin;
using std::endl;
#include <string>
using std::string;
using std::getline;
#include <vector>
using std::vector;

#include "parser.h"
using HammarTBA::Parser;
#include "avatar.h"
using HammarTBA::Avatar;
#include "room.h"
using HammarTBA::Room;
#include "roomExit.h"
using HammarTBA::RoomExit;
#include "item.h"
using HammarTBA::Item;
#include "amulet.h"
using HammarTBA::Amulet;
#include "saveLoad.h"
using HammarTBA::load;

//sets up a number of rooms and puts them in world
void setupWorld(vector<Room*>& world);

int main(int intc, char* argv[])
{
    vector<Room*> world;
    char c;
    bool endgame = false;

    do
    {
        bool newgame = true;
        Parser parser;
        string input;

        setupWorld(world);

        Avatar pc(world[0]);

        if (intc == 1) {
            cout << endl
                << "Just so you know, you can include one command-line argument \n"
                << "that specifies a save file that you would like to load \n";
        }
        else if (intc == 2 && !endgame)
        {
            newgame = false;
            load(argv[1], pc, world);
        }
        else if (intc > 2) {
            cout << endl
                << "Too many command-line arguments. No attempt to load a save \n"
                << "file will be made. \n";
        }

        if (newgame)
        {
            cout << endl
                << endl
                << endl
                << "Oh no! You're trapped in a dungeon! How did you get here? \n"
                << "Who cares? But somehow you know that only a magic AMULET \n"
                << "can get you out of here... \n"
                << "Type HELP if you need it. \n"
                << endl
                << endl
                << endl;

            pc.look();
        }

        endgame = false;
        while (!endgame)
        {
            cout << endl
                << ">>> ";
            getline(cin, input);
            cout << endl
                << endl
                << endl;

            endgame = parser.parse(input, pc, world);
        }

        world.clear();

        cout << "Play again? (Y/N) ";
        cin.get(c);
        cin.ignore(1024, '\n');

    } while (c == 'y' || c == 'Y');

	return 0;
}

void setupWorld(vector<Room*>& world)
{
    //a temporary string for descriptions
    string desc;


    desc = "You find yourself in yon jail cell. \n"
            "You notice the cell door isn't even locked. Hmph. Some jail cell! \n";
    Room* jailCell = new Room("Jail Cell", desc);
    world.push_back(jailCell);


    desc = "You find yourself outside yon jail cell. \n"
            "This seems to be a hallway. Hallways are nice. \n";
    Room* hallway = new Room("Hallway", desc);
    world.push_back(hallway);


    desc = "You find yourself in some room. \n"
            "'Nuff said. \n";
    Room* someRoom = new Room("Some Room", desc);
    world.push_back(someRoom);


    desc = "You find yourself in another room. \n"
            "'Nuff said. \n";
    Room* anotherRoom = new Room("Another Room", desc);
    world.push_back(anotherRoom);


    desc = "You find yourself in a sweet room. \n"
            "'Nuff said. \n";
    Room* sweetRoom = new Room("Sweet Room", desc);
    world.push_back(sweetRoom);


    jailCell->addExit( new RoomExit("NORTH", hallway) );


    hallway->addExit( new RoomExit("NORTH", anotherRoom) );
    hallway->addExit( new RoomExit("SOUTH", jailCell) );
    hallway->addExit( new RoomExit("WEST", someRoom) );


    someRoom->addExit( new RoomExit("EAST", hallway) );
    someRoom->addExit( new RoomExit("NORTH", sweetRoom) );


    anotherRoom->addExit( new RoomExit("SOUTH", hallway) );
    anotherRoom->addExit( new RoomExit("WEST", sweetRoom) );


    sweetRoom->addExit( new RoomExit("SOUTH", someRoom) );
    sweetRoom->addExit( new RoomExit("EAST", anotherRoom) );

    desc = "The AMULET glows magically.  You should use it. \n";
    sweetRoom->addItem( new Amulet("AMULET", desc) );
}
