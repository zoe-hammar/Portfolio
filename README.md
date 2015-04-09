### Text-based Adventure  
**CS 165 - Accelerated Intro to Computer Science**  
**August 2014**  
This is a simple text-based adventure.  In it, you can do many basic text-based adventure activities, like go from room to room, examine items, take and drop items, use items, save and load save files, and more.  The focus was on making the underlying engine as modular and extensible as I could manage, so adding more rooms, placing more items in rooms, and even adding entirely new items is relatively painless.

---

### Virtual Piano  
**Professional Enrichment Project**  
**June 2010**  
This is a virtual piano like one you might see in Garage Band.  By pressing the corresponding computer keys or by clicking on the GUI keys with the mouse, appropriate MIDI sounds are generated.  The keys also get visually highlighted when they are played.

---

### Simple Message Board website 
**CS 494 - Web Development**  
**December 2014**  
This is a simple message board.  It allows users to create/login to an account and then add posts to a public message board.  AJAX is used to do error checking on account creation/login as well as display posts on the message board as soon as users submit them.  Additionally, prepared statements are used with the database in order to protect against injection attacks.

---

### Small Shell
**CS 344 - Operating Systems**  
**February 2015**  
This is a small Linux shell program.  In addition to being able to execute any Linux program/command along with arguments, it can execute programs in the background, redirect input/output, and inform you when processes complete or are terminated.  Finally, sending a terminate signal will terminate the foreground process that the shell is running instead of the shell itself.

---

### Text Encryptor-Decryptor
**CS 344 - Operating Systems**  
**March 2015**  
This is a set of programs that programs that can encrypt or decrypt a message using a One-Time-Pad system.  It uses a client-server architecture to accomplish this.  The client (e.g. `otp_enc`) does error checking on the message and key passed to it and then sends it over a socket to the "server" (or daemon, e.g. `otp_enc_d`).  The server then performs the encryption/decryption and sends it back to the client.

---

### Traveling Salesman Approximation
**CS 325 - Analysis of Algorithms**  
**March 2015**  
This approximation algorithm uses the Nearest Neighbor and 2-opt methods to find a circuit in a graph defined by 2D coordinates.
