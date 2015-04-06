/*****************************************************************************
 * Author:                     Justin Hammar
 * Date Created:               8/27/14
 * Last Modification Date:     8/31/14
 * Filename:                   parser.cpp
 *
 * Overview:
 *     This will interpret a string as a command and will tell the game to
 *     respond accordingly, if possible.
 * Input:
 *     A string to be interpreted as a command
 * Output:
 *     None, unless the parser can't make sense of the command, in which case
 *     it will tell the user as such.
 *
 *****************************************************************************/

#include <iostream>
using std::cout;
using std::endl;
#include <sstream>
using std::stringstream;
#include <string>
using std::string;
#include <cctype>
#include <map>
using std::map;
#include <vector>
using std::vector;
#include <cstdlib>
#include <stdexcept>
using std::out_of_range;

#include "parser.h"
#include "avatar.h"
using HammarTBA::Avatar;
#include "saveLoad.h"
using HammarTBA::save;
using HammarTBA::load;

namespace HammarTBA
{
    Parser::Parser() {
        fillVerbMap();
        fillNounMap();
    }

    bool Parser::parse(string input, Avatar& pc, vector<Room*>& world)
    {
        words.clear();

        input = strToLower(input);

        breakIntoWords(input);

        //If the user hits return without typing anything, the game will chide them
        //and bring them back to the prompt to try again.
        if (words.size() <= 0)
        {
            cout << "Dude, I can't read your mind. You have to actually type something! \n"
                << "Don't be afraid to ask for HELP. \n";
            //keep the game going
            return false;
        }

        try {
            //Replace the verb with a synonym the parser recognizes, if possible
            words[0] = verbMap.at( words[0] );
        }
        catch (out_of_range)
        {
            cout << "Don't be so hard on the little parser! He's just a wee one!\n"
                << "Don't be afraid to ask for HELP.\n";
            //keep the game going
            return false;
        }

        if (words.size() == 1)
        {
            if (words[0] == "NORTH" || words[0] == "SOUTH" || words[0] == "EAST" || words[0] == "WEST")
                pc.go(words[0]);
            else if (words[0] == "LOOK")
                pc.look();
            else if (words[0] == "INVENTORY")
                pc.listInventory();
            else if (words[0] == "HELP")
                printHelp();
            else if (words[0] == "SAVE")
                save(pc);
            else if (words[0] == "LOAD")
                load(pc, world);
            else if (words[0] == "QUIT")
            {
                cout << "Exiting program." << endl
                    << endl;
                //end this game
                return true;
            }
            else
                cout << words[0] << " what? Try again.\n";
        }
        else if (words.size() == 2)
        {
            try {
                //Replace the noun with a synonym the parser recognizes, if possible
                words[1] = nounMap.at( words[1] );
            }
            catch (out_of_range) {
                /**/;
            }
            if (words[0] == "EXAMINE")
                pc.examine(words[1]);
            else if (words[0] == "TAKE")
                pc.take(words[1]);
            else if (words[0] == "DROP")
                pc.drop(words[1]);
            else if (words[0] == "USE")
                pc.use(words[1]);
            else if (words[0] == "LOAD")
                load(words[1].c_str(), pc, world);
        }
        else
            cout << "Don't mix your commands with gobblygook. Try again. \n"
                << "Don't be afraid to ask for HELP. \n";

        //keep the game going
        return false;
    }

    string Parser::strToLower(string str)
    {
        for (int i = 0; i < str.length(); ++i)
        {
            if ( isupper( str.at(i) ) )
                str[i] = tolower( str.at(i) );
        }
        return str;
    }

    void Parser::breakIntoWords(string input)
    {
        stringstream ss;
        string word;

        ss.str("");
        ss.clear();

        ss << input;

        while (ss >> word)
            this->words.push_back(word);
    }

    void Parser::printHelp()
    {
        cout << "COMMAND             | RESULT \n"
            << "-------------------------------------------------------------------------------- \n"
            << "NORTH(N), SOUTH(S), | \n"
            << "EAST(E), WEST(W)....| Go in the stated direction \n"
            << "LOOK(L).............| Get details about the room you're in \n"
            << "INVENTORY(INV)(I)...| Get a listing of the items you have \n"
            << "EXAMINE(X)..........| Get details about a given item (e.g. EXAMINE CHEESE) \n"
            << "TAKE(GET)...........| Acquire a given item (e.g. TAKE BREAD) \n"
            << "DROP................| Ditch a given item (e.g. DROP SPOON) \n"
            << "USE.................| Use a given item (e.g. USE KNIFE) \n"
            << "HELP................| Access this help screen \n"
            << "SAVE................| Save your progress in the game \n"
            << "LOAD(RESTORE).......| Load a previously saved game (e.g. LOAD, LOAD SAVE.TXT)\n"
            << "QUIT................| Exit the program \n";
    }

    void Parser::fillVerbMap()
    {
        verbMap["north"] = "NORTH";
        verbMap["n"] = "NORTH";
        verbMap["south"] = "SOUTH";
        verbMap["s"] = "SOUTH";
        verbMap["east"] = "EAST";
        verbMap["e"] = "EAST";
        verbMap["west"] = "WEST";
        verbMap["w"] = "WEST";

        verbMap["look"] = "LOOK";
        verbMap["l"] = "LOOK";
        verbMap["examine"] = "EXAMINE";
        verbMap["x"] = "EXAMINE";
        verbMap["inventory"] = "INVENTORY";
        verbMap["inv"] = "INVENTORY";
        verbMap["i"] = "INVENTORY";

        verbMap["take"] = "TAKE";
        verbMap["get"] = "TAKE";
        verbMap["drop"] = "DROP";
        verbMap["use"] = "USE";

        verbMap["help"] = "HELP";
        verbMap["h"] = "HELP";
        verbMap["load"] = "LOAD";
        verbMap["restore"] = "LOAD";
        verbMap["save"] = "SAVE";
        verbMap["quit"] = "QUIT";
        //verbMap["q"] = "QUIT";

//        //commands I might implement if I have time
//        verbMap["open"] = "OPEN";
//        verbMap["close"] = "CLOSE";
//        verbMap["shut"] = "CLOSE";
//        verbMap["lock"] = "LOCK";
//        verbMap["unlock"] = "UNLOCK";
//
//        verbMap["equip"] = "EQUIP";
//        verbMap["search"] = "SEARCH";void fillNounMap();
//
//        verbMap["again"] = "AGAIN";

    }

    void Parser::fillNounMap()
    {
        nounMap["amulet"] = "AMULET";
    }
} // HammarTBA
