/*****************************************************************************
 * Author:                     Justin Hammar
 * Date Created:               8/27/14
 * Last Modification Date:     8/31/14
 * Filename:                   parser.h
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

#ifndef PARSER_H
#define PARSER_H

#include <string>
using std::string;
#include <map>
using std::map;
#include <vector>
using std::vector;

#include "avatar.h"

namespace HammarTBA
{
    class Parser
    {
    public:
        Parser();

        /*input is passed by value while pc and world are passed by reference*/
        //Interprets a string as a command and will tell the game to
        //respond accordingly, if possible.
        //returns true if the game has ended.
        bool parse(string input, Avatar& pc, vector<Room*>& world);
        //prints the list of commands and what they do
        void printHelp();
    protected:
        //contains the "words" of the user's input as separate strings
        vector<string> words;
        //A map that allows the user to use synonyms in place of command verbs
        map<string,string> verbMap;
        map<string,string> nounMap;

        //Returns a lowercase version of the string
        string strToLower(string str);
        //Breaks a line of input into its constituent "words" and returns a vector
        //containing these "word" strings in the order in which they initially appeared.
        void breakIntoWords(string input);
        void fillVerbMap();
        void fillNounMap();
    };
} // HammarTBA

#endif // PARSER_H
