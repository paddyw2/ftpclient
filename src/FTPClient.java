/**
 * FastFTP Class
 * FastFtp implements a basic FTP application based on UDP data transmission and 
 * alternating-bit stop-and-wait concept
 * @author      XYZ
 * @version     1.0, 1 Feb 2017
 *
 */

import java.net.*;
import java.io.*;
import java.util.Arrays;

import java.util.*;

public class FTPClient {

    private Socket socket;
    private DatagramSocket UDPSocket;
    private String serverName;
    private int serverPort;
    private String fileName;
    private int timeout;

    /**
     * Constructor to initialize the program 
     * 
     * @param serverName    server name
     * @param server_port   server port
     * @param file_name     name of file to transfer
     * @param timeout       Time out value (in milli-seconds).
     */
    public FTPClient(String server_name, int server_port, String file_name, int timeout) {
    
        /* Initialize values */
        serverName = server_name;
        serverPort = server_port;
        fileName = file_name;
        this.timeout = timeout;

        /* send TCP handshake */

        // set up socket
        socket = null;
        try {
            socket = new Socket(serverName, serverPort);
        } catch (Exception e) {
            System.out.println("Socket initialization error");
        }

        // set up output stream, and send initial handshake
        try {
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.writeUTF(fileName);
        } catch (Exception e) {
            System.out.println("Output error");
        }

        // set up input strea, and wait for handshake response
        byte response = -1;
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            boolean readData = true;
            while(readData) {
                if(input.available() > 0) {
                    response = input.readByte();
                    readData = false;
                }
            }
        } catch (Exception e) {
            System.out.println("Input error");
            System.out.println(e.getMessage());
        }

        if(response != 0)
            System.out.println("Handshake failure");
        else
            System.out.println("Handshake success");

        sendData();

    }
    

    /**
     * Send file content as Segments
     * 
     */
    public void send() {
        
        /* send logic goes here. You may introduce addtional methods and classes */
    }

    public void sendData()
    {
        while(true) {
        /* send file over UDP */
        // segment info
        byte[] payload = new byte[Segment.MAX_PAYLOAD_SIZE];
       
        String tester = "just a wee message mate";
        payload = tester.getBytes();

        // creating a segment with the payload and seqNum 1
        Segment seg1 = new Segment(0, payload);


        // create sender socket
        try {
            UDPSocket = new DatagramSocket();
        } catch (Exception e) {
            System.out.println("UDP socket init failure");
            System.out.println(e.getMessage());
        }

        try {
            UDPSocket.setSoTimeout(2000);
        } catch (Exception e) {
            System.out.println("boo");
        }

        byte[] sendData = seg1.getBytes();

        InetAddress IPAddress = null;

        // create IP address
        try {
            IPAddress = InetAddress.getByName("localhost");
        } catch (Exception e) {
            System.out.println("Inet error");
            System.out.println(e.getMessage());
        }

        // create sender packet
        DatagramPacket sendPacket =  new DatagramPacket(sendData, sendData.length, IPAddress, serverPort);

        // try send packet
        try {
            UDPSocket.send(sendPacket);
        } catch (Exception e) {
            System.out.println("Packet send error");
            System.out.println(e.getMessage());
        }

        // create receiving packet
        byte[] receiveData = new byte[Segment.MAX_PAYLOAD_SIZE];
        DatagramPacket pkt = new DatagramPacket(receiveData, receiveData.length);
        
        // wait for client response

        boolean noTimeout = true;
        try {
            UDPSocket.receive(pkt);
        } catch (Exception e) {
            noTimeout = false;
            System.out.println("Timeout: Packet receive error");
            System.out.println(e.getMessage());
        }
        // if packet received, break loop
        if(noTimeout)
            break;

        }
    }

       /**
        * A simple test driver
         * 
        */
    public static void main(String[] args) {
        
        String server = "localhost";
        String file_name = "";
        int server_port = 8888;
        int timeout = 50; // milli-seconds (this value should not be changed)

        
        // check for command line arguments
        if (args.length == 3) {
            // either provide 3 parameters
            server = args[0];
            server_port = Integer.parseInt(args[1]);
            file_name = args[2];
        }
        else {
            System.out.println("wrong number of arguments, try again.");
            System.out.println("usage: java FTPClient server port file");
            System.exit(0);
        }

        
        FTPClient ftp = new FTPClient(server, server_port, file_name, timeout);
        
        System.out.printf("sending file \'%s\' to server...\n", file_name);
        ftp.send();
        System.out.println("file transfer completed.");
    }

}
