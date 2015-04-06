#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <sys/wait.h>
#include <netinet/in.h>
#include <fcntl.h>

/*------------------------------------------------------------------------------
 receive the contents of a text file through a socket
 newsockfd: the socket to receive the data through
 length: length of the text file to be received
 PRECONDITION: a connection through the socket has already been established
 POSTCONDITION: the contents of the text file have been received. The socket is still open
------------------------------------------------------------------------------*/
char* recvFile_d(int newsockfd, long length);

/*------------------------------------------------------------------------------
 send the contents of a text string through a socket
 newsockfd: the socket to send the data through
 text: the c string that we're sending
 length: length of 'text' (not including null terminators, if any)
 PRECONDITION: a connection through the socket has already been established
 POSTCONDITION: the contents of the text string have been sent. The socket is still open
------------------------------------------------------------------------------*/
void sendFile_d(int newsockfd, const char* text, long length);

int main(int argc, char* argv[]) {
    int sockfd;
    int newsockfd;
    int portnum;
    struct sockaddr_in server;
    struct sockaddr_in client;
    int text_len;
    int key_len;
    char buffer[64];
    char* text;
    char* key;
    int pid;
    int status;
    
    if (argc < 2) {
        printf("You must specify the port number to listen on\n");
        printf("as the first argument\n");
        exit(1);
    }
    
    portnum = atoi(argv[1]);
    
    //set up the socket address struct
    server.sin_family = AF_INET;
    server.sin_addr.s_addr = INADDR_ANY;
    server.sin_port = htons(portnum);
    
    //create the socket
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) {
        perror("socket");
        exit(1);
    }
    //bind the socket to the port
    if ( bind(sockfd, (struct sockaddr*) &server, sizeof(server)) < 0 ) {
        perror("bind");
        exit(1);
    }
    //listen for incoming connections
    listen(sockfd, 5);
    
    //continuously handle incoming connections
    while (1) {
        //grab the next connection in the listen queue
        socklen_t cli_len = sizeof(client);
        newsockfd = accept(sockfd, (struct sockaddr*) &client, &cli_len);
        if (newsockfd < 0) {
            perror("accept");
        }
        //fork
        pid = fork();
        switch(pid) {
        case -1: 
            perror("fork");
            break;
        case 0:
            //child handles the connection
            
            close(sockfd);
            memset(buffer, '\0', sizeof(buffer));
            
            //receive authentication message and reply
            read(newsockfd, buffer, sizeof(buffer)-1);
            if (strcmp(buffer, "dec_jh") != 0) {
                perror("invalid client");
                //write error back to client
                char response[]  = "invalid";
                write(newsockfd, response, sizeof(response));
                _Exit(2);
            } else {
                //write confirmation back to client
                char response[] = "dec_d_jh";
                write(newsockfd, response, sizeof(response));
            }
            
            memset(buffer, '\0', sizeof(buffer));
            
            //receive and echo length of upcoming text
            read(newsockfd, buffer, sizeof(buffer));
            write(newsockfd, buffer, strlen(buffer));

            text_len = atoi(buffer);
            text = recvFile_d(newsockfd, text_len);
            
            memset(buffer, '\0', sizeof(buffer));
            
            //receive and echo length of upcoming text
            read(newsockfd, buffer, sizeof(buffer));
            write(newsockfd, buffer, strlen(buffer));
            
            key_len = atoi(buffer);
            key = recvFile_d(newsockfd, key_len);
            
            int t;
            int k;
            int c;
            
            //decrypt
            for (int i = 0; i < text_len; ++i) {
                //leave newlines unchanged
                if (text[i] != '\n') {
                    //convert chars to ints in range [0,26]
                    if (text[i] == ' ') {
                        t = 26;
                    } else {
                        t = text[i] - 'A';
                    }
                    if (key[i] == ' ') {
                        k = 26;
                    } else {
                        k = key[i] - 'A';
                    }
                    
                    c = (t - k + 27) % 27;
                    //replace the char in text with its decrypted form
                    if (c == 26) {
                        text[i] = ' ';
                    } else {
                        text[i] = 'A' + (char)c;
                    }
                }
            }
            
            //send plaintext
            sendFile_d(newsockfd, text, text_len);
            
            free(text);
            free(key);
            
            _Exit(0);
            break;
        default:
            //parent
            close(newsockfd);
            //check for finished children
            do {
                pid = waitpid(-1, &status, WNOHANG);
            } while (pid > 0);
        }
    }
    close(sockfd);
    return 0;
}

char* recvFile_d(int newsockfd, long length) {
    char* str;
    char response[] = "f recv";
    int nread;

    //allocate a c string big enough to hold the incoming text
    str = (char*)malloc(length);

    //keep receiving chunks of the plaintext
    for (long i = 0; i < length; i += (long)nread) {
        nread = read(newsockfd, str + i, length - i);
        if (nread < 0) {
            perror("daemon read");
            _Exit(1);
        }
    }
    //tell it you're done receiving
    write(newsockfd, response, strlen(response));
    
    return str;
}

void sendFile_d(int newsockfd, const char* text, long length) {
    int nread;        //number of bytes read
    int nwrite;       //number of bytes written
    
    //keep sending chunks of the ciphertext
    for (long i = 0; i < length; i += (long)nwrite) {
        nwrite = write(newsockfd, text + i, length - i);
        if (nwrite < 0) {
            perror("daemon write");
            _Exit(1);
        }
    }
}