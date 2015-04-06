#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <time.h>

int main(int argc, char* argv[]) {
    int keylength;
    int rnum;         //a random num
    char rchar;       //a random char
    
    if (argc < 2) {
        printf("You must specify the length of the key to be generated\n");
        printf("as the first argument\n");
        exit(1);
    }
    
    //convert the argument string to an integer
    keylength = atoi(argv[1]);
    
    //seed the random number generator
    srand(time(NULL));
    //fill key with random valid characters
    for (int i = 0; i < keylength; ++i) {
        rnum = rand() % 27;
        if (rnum == 26) {
            //space
            printf(" ");
        } else {
            //letter
            rchar = 'A' + (char)rnum;
            printf("%c", rchar);
        }
    }
    return 0;
}