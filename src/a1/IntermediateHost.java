/* The intermediate host program intercepts
 * packets before passing them along to 
 * their final destinations.
 */
package a1; 
import java.io.*;
import java.net.*;

public class IntermediateHost {

	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket receiveSocket, sendSocket, sendReceiveSocket;
	private int clientPort;
	
	
	public IntermediateHost() {
		try {
			// Construct a Datagram socket. This socket will be used to
	     	// send/receive UDP Datagram packets.
			sendReceiveSocket = new DatagramSocket();
			
	      	// Construct a Datagram socket and bind it to port 23 
	     	// socket will be used to receive UDP Datagram packets.
	     	receiveSocket = new DatagramSocket(23);  
		} catch (SocketException se) {// exception for the socket can not be created
			se.printStackTrace();
			System.exit(1);
		} 
	}
	
	
	// prints out the message to be sent in bytes (HEX)
	private void printByteMsg(byte[] bMsg, int len) {				
		System.out.println("\nIntermediate: Packet in bytes (HEX):");
		for (int i = 1; i <= len; i++) {
			if (Integer.toHexString(bMsg[i-1]).length() == 1) {
				System.out.print("0");
			}
			System.out.print(Integer.toHexString(bMsg[i-1]) + " ");
			if (i % 4 == 0) {
				System.out.print("\n");
			}
		}
		
	}
	
	
	// Receives packets from the client system
	private void receiveClientPacket(byte[] data) {
		// Block until a Datagram packet is received on the receiveSocket.
		try {        
			System.out.println("Waiting..."); 
			receiveSocket.receive(receivePacket);
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}

		// To be able to respond to the client
		clientPort = receivePacket.getPort();

		// Process and output the received Datagram
		System.out.println("\nIntermediate: Packet received:");
		System.out.println("From host: " + receivePacket.getAddress());
		System.out.println("Host port: " + clientPort);
		System.out.println("Length: " + receivePacket.getLength());
		System.out.println("\nIntermediate: Packet (String):");  
		System.out.println(new String(data,0,receivePacket.getLength()));
		this.printByteMsg(data, receivePacket.getLength());		
	}
	
	
	// Send packets to the client system
	private void sendClientPacket(byte[] data) {
		// Create packet and output its info
		sendPacket = new DatagramPacket(data, receivePacket.getLength(),
				receivePacket.getAddress(), clientPort);
		System.out.println("\n------------------------");
		System.out.println("Intermediate: Forwarding packet to CLIENT:");
		System.out.println("To host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		System.out.println("Length: " + sendPacket.getLength());
		this.printByteMsg(sendPacket.getData(), sendPacket.getLength());


		// Construct a Datagram socket to send a packet to client
		try {
			sendSocket = new DatagramSocket();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	
		// Send the Datagram packet
		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("\nIntermediate: packet sent to CLIENT.\n");
	
		// Close the socket.
		sendSocket.close();		
	}
	
	
	// Send packets to the server system
	private void sendServerPacket(byte[] data) {
		// Create packet and output its info
		sendPacket = new DatagramPacket(data, receivePacket.getLength(),
									receivePacket.getAddress(), 69); 
		System.out.println("\n------------------------");
		System.out.println("Intermediate: Forwarding packet to SERVER:");
		System.out.println("To host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		System.out.println("Length: " + sendPacket.getLength());
		System.out.println("\nIntermediate: Packet as a string:");  
		System.out.println(new String(sendPacket.getData(), 0, sendPacket.getLength()));
		this.printByteMsg(sendPacket.getData(), sendPacket.getLength());
	
		// Send the Datagram packet
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("\nIntermediate: packet sent to SERVER.");	
	}
	
	
	// Receives packets from the server system
	private void receiveServerPacket(byte[] data) {
		// Block until a Datagram packet is received on the sendReceiveSocket.
		try { 
			sendReceiveSocket.receive(receivePacket);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Process and output the received Datagram
		System.out.println("\n------------------------------");
		System.out.println("Intermediate: Packet received:");
		System.out.println("From host: " + receivePacket.getAddress());
		System.out.println("Host port: " + receivePacket.getPort());
		System.out.println("Length: " + receivePacket.getLength());
		this.printByteMsg(data, receivePacket.getLength());		
	}
	
	
	// Receive and pass the packet to server from client.
	public void receiveAndPass() {
		// Construct a DatagramPacket for receiving packets 
		byte data[] = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
		
		int i = 1; 
		while (true) {
			System.out.println("--------------------------------------------------------");
			System.out.println("--------------------------------------------------------");
			System.out.println("Intermediate: Waiting for Packet. Round #" + i);
			i++;
			
			// Receive packet from client
			this.receiveClientPacket(data);

			// Send packet to server
			this.sendServerPacket(data);

			// Receive packet from server		
			this.receiveServerPacket(data);

			// Send packet to client
			this.sendClientPacket(data);	
		} 
	}
	
	public static void main(String[] args) {
		IntermediateHost h = new IntermediateHost();
		h.receiveAndPass();

	}

}
