#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <fcntl.h>

#define BUFSIZE 1024

/*------------------------------------------------------------------------------
 send the contents of a text file through a socket
 fd: the file descriptor of the file
 sockfd: the socket to send the data through
 PRECONDITION: text file is already open and a connection through the socket
 has already been established
 POSTCONDITION: the contents of the text file have been sent. The text file 
 has been closed but the socket is still open
------------------------------------------------------------------------------*/
void sendFile(int fd, int sockfd);

/*------------------------------------------------------------------------------
 receive the contents of a text file through a socket and print it to stdout
 sockfd: the socket to receive the data through
 length: length of the text to be received
 PRECONDITION: a connection through the socket has already been established
 POSTCONDITION: the contents of the text have been printed to stdout. The socket is still open
------------------------------------------------------------------------------*/
void recvFile(int sockfd, long length);

void stringdump(const char* str, int num);

int main(int argc, char* argv[]) {
    int plainfd;
    int keyfd;
    long text_len;
    int sockfd;
    int portnum;
    char buffer[64];
    char auth[] = "enc_jh";
    struct sockaddr_in server;
    struct hostent* serv_addr;
    
    if (argc < 4) {
        printf("usage: otp_enc plaintextFile encryptKey portNumber\n");
        exit(1);
    }
    
    plainfd = open(argv[1], O_RDONLY);
    keyfd = open(argv[2], O_RDONLY);
    if (plainfd < 0 || keyfd < 0) {
        perror("open");
        exit(1);
    }
    portnum = atoi(argv[3]);
    
    //make sure the plaintext contains only valid characters
    while (read(plainfd, buffer, 1) != 0) {
        if (buffer[0] != ' ' && (buffer[0] < 'A' || buffer[0] > 'Z')) {
            //I'm treating newlines as "valid" but I'll just leave them as is
            //throughout encryption/decryption
            if (buffer[0] != '\n') {
                perror("plaintext contains invalid characters");
                exit(1);
            }
        }
    }
    //check if the key is long enough
    text_len = lseek(plainfd, 0, SEEK_END);
    if (lseek(keyfd, 0, SEEK_END) < text_len) {
        perror("key is too short");
        exit(1);
    }
    
    //prepare server information
    serv_addr = gethostbyname("localhost");
    if (serv_addr == NULL) {
        perror("ip address resolution");
        exit(1);
    }
    server.sin_family = AF_INET;
    server.sin_port = htons(portnum);
    memcpy(&server.sin_addr, serv_addr->h_addr, serv_addr->h_length);
    
    //create a client socket with socket()
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) {
        perror("socket");
        exit(1);
    }
    //connect to a server with connect()
    if (connect(sockfd, (struct sockaddr*) &server, sizeof(server))) {
        perror("connect");
        exit(2);
    }

    memset(buffer, '\0', sizeof(buffer));
    
    //--------------------------------------------------------------------------
    //confirm that I have connected to the right daemon
    write(sockfd, auth, sizeof(auth));
    read(sockfd, buffer, sizeof(buffer));
    if (strcmp(buffer, "enc_d_jh") != 0) {
        perror("unable to contact otp_enc_d on given port");
        exit(2);
    }
    
    //send the plaintext
    sendFile(plainfd, sockfd);
    //send the key
    sendFile(keyfd, sockfd);
    
    //receive the ciphertext from the daemon and print to stdout
    recvFile(sockfd, text_len);
    
    close(sockfd);
    
    return 0;
}

void sendFile(int fd, int sockfd) {
    long length;        //length of the file described by fd
    int slength;        //length of the string version of length
    char len_echo[16];  //the length string that the server got from us
    int nread;          //number of bytes read
    int nwrite;         //number of bytes written
    char buf[BUFSIZE];
    
    //get the length of the input files
    length = lseek(fd, 0, SEEK_END);
    //set the file pointers back to the beginning of the files
    lseek(fd, 0, SEEK_SET);
    
    memset(len_echo, '\0', sizeof(len_echo));
    
    //tell the daemon how big the text file is
    slength = sprintf(buf, "%ld", length);
    write(sockfd, buf, slength);
    read(sockfd, len_echo, sizeof(len_echo));
    if (strcmp(buf, len_echo) != 0) {
        perror("length discrepancy");
        exit(1);
    }
    
    //send the file
    while (1) {
        //grab a chunk from the file
        nread = read(fd, buf, sizeof(buf));
        if (nread == 0) {
            //we're done reading, so wrap things up
            close(fd);
            break;
        }
        //send the chunk
        for (int i = 0; i < nread; i += nwrite) {
            //keep sending subchunks until the entire chunk is sent
            nwrite = write(sockfd, buf + i, nread - i);
            if (nwrite < 0) {
                perror("client write");
                exit(1);
            }
        }
    }
    
    memset(buf, '\0', 64);
    
    //read the confirmation from the daemon
    nread = read(sockfd, buf, 63);
    if (nread < 0) {
        perror("client read confirm");
    }
    if (strcmp(buf, "f recv") != 0) {
        perror("arrival verification");
        exit(1);
    }
}

void recvFile(int sockfd, long length) {
    char buf[BUFSIZE];
    int nread;

    //keep receiving chunks of the ciphertext
    for (long i = 0; i < length; i += (long)nread) {
        nread = read(sockfd, buf, sizeof(buf)-1);
        if (nread < 0) {
            perror("client read");
            _Exit(1);
        }
        buf[nread] = '\0';
        printf("%s", buf);
    }
}

void stringdump(const char* str, int num) {
    fprintf(stderr, "str: ");
    for (int i = 0; i < num; ++i) {
        fprintf(stderr, "%i ", str[i]);
    }
    fprintf(stderr, "\n");
}