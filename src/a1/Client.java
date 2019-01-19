/* The client sent message to the echo server 
 * and receive from server.
 */
package a1; 

import java.io.*;
import java.net.*;

public class Client {

	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendReceiveSocket;

	public Client() {
		try {
			// Construct a Datagram socket and bind it to any available socket
			// Socket send and receive UDP Datagram packets.
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException e) { // exception for the socket can not be created
			e.printStackTrace();
			System.exit(1);
		}
	}

	// Converts the message to be sent into a byte array
	private void byteMsg(byte[] bMsg, char rInit, String filename, String mode) {
		int i = 0;

		bMsg[i] = (byte) 0; // leading zero
		i++;

		// set message request, 1 for read, 2 for write, 0 for error
		if (rInit == 'r') {
			bMsg[i] = (byte) 1;
		} else if (rInit == 'w') {
			bMsg[i] = (byte) 2;
		} else {
			bMsg[i] = (byte) 0;
		}
		i++;

		// add in the filename
		for (byte b : filename.getBytes()) {
			bMsg[i] = b;
			i++;
		}

		bMsg[i] = (byte) 0;
		i++;

		// add in the mode
		for (byte b : mode.getBytes()) {
			bMsg[i] = b;
			i++;
		}

		bMsg[i] = (byte) 0;
	}

	// prints out the message to be sent in bytes (HEX)
	private void printByteMsg(byte[] bMsg, int len) {
		System.out.println("\nClient: Packet in bytes (HEX):");
		for (int i = 1; i <= len; i++) {
			if (Integer.toHexString(bMsg[i - 1]).length() == 1) {
				System.out.print("0");
			}
			System.out.print(Integer.toHexString(bMsg[i - 1]) + " ");
			if (i % 4 == 0) {
				System.out.print("\n");
			}
		}
	}

	// Sends Datagram packets to the server
	// Prints out the packets details for each
	private void sendPacket(byte[] bMsg) {
		// Create the Datagram packet
		try {
			sendPacket = new DatagramPacket(bMsg, bMsg.length, 
					InetAddress.getLocalHost(), 23); 
			
		} catch (UnknownHostException e) {//through unknown host
			e.printStackTrace();
			System.exit(1);
		}

		// Prints out the sending packet info
		System.out.println("\nClient: Sending packet...");
		System.out.println("To host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		System.out.println("Length: " + sendPacket.getLength());

		// Send the Datagram packet to the server via the send/receive socket.
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Client: Packet sent.");
	}

	// Receive a Datagram packet on the sendReceive socket
	// and print out the packets details
	private void receivePacket() {
		// Wait for incoming Datagram packet
		byte data[] = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);

		try {
			// Block until a Datagram is received via sendReceiveSocket.
			sendReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Process and output the received Datagram.
		System.out.println("\n------------------------");
		System.out.println("Client: Packet received:");
		System.out.println("From host: " + receivePacket.getAddress());
		System.out.println("Host port: " + receivePacket.getPort());
		System.out.println("Length: " + receivePacket.getLength());
		this.printByteMsg(data, receivePacket.getLength());
		System.out.println("\n");
	}

	// Send and receive Datagram Packets
	public void sendAndReceive() {
		String filename = "test.txt";
		String mode = "netascii";
		String request = "Read request";
		char rInit = 'r';

		// send 11 packets to the server - 5 read, 5 write, 1 error.
		for (int i = 0; i < 11; i++) {
			// type of packet being sent
			if (i % 2 == 0) { // write
				request = "Read request";
				rInit = 'r';
			} else { // read
				request = "Write request";
				rInit = 'w';
			}

			if (i == 10) { // error
				request = "ERROR";
				rInit = 'e';
			}

			System.out.println("--------------------------------------------------------");
			System.out.println("--------------------------------------------------------");
			System.out.println("Client: Preparing to send packet #" + (i + 1) + " - " + request);

			// create and convert message into bytes
			byte[] bMsg = new byte[4 + filename.length() + mode.length()]; // 4, for bytes before/between/after strings
			this.byteMsg(bMsg, rInit, filename, mode);

			// print the message as a string and in bytes
			System.out.println("\nClient: Packet as a string:");
			System.out.println(new String(bMsg, 0, bMsg.length));
			this.printByteMsg(bMsg, bMsg.length);

			// send the packet to port 23
			this.sendPacket(bMsg);

			// Waiting for receive.
			this.receivePacket();
		} 

		// Finished, close the socket.
		sendReceiveSocket.close();
	}

	public static void main(String[] args) {
		Client c = new Client();
		c.sendAndReceive();
	}

}
