# UDP Socket Programming Assignment

This basic FTP client transfers files to the specified server using UDP socket programming. The server <code>FTPServer.jar</code> can be run anywhere, and once up and running, the client <code>FTPClient.java</code> can transfer a file, presuming the port number is known.

Files created:
* FTPClient.java

Provided Files:
* FTPServer.java
* Segment.java

### To run:
1. Run server: <code>java -jar FTPServer.jar [port-number] [packet-loss-probability (i.e. 0.2)]</code>
2. Run client (from <code>/bin</code>): <code>java FTPClient localhost [port-number] [file-in-bin]</code>
