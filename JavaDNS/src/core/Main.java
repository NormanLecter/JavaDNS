package core;
import java.io.*;
import java.net.*;
import core.ProtosMessageDNS.*;


public class Main {
	private static InetAddress addr;
	private static int port = 53;
	private static String adress;
	private static String received;
	
	public static void main(String[] args) {
		adress = "";
		received = "9 9";
		
		DatagramSocket client;
		try {
			client = new DatagramSocket();
			addr = InetAddress.getByName("150.254.144.3");
			
			/* TEST PROTOBUF */
			MessageDNS.Builder MsgDNS = MessageDNS.newBuilder();
			/* KONIEC TESTU */
			
			while(getLevelOfDomain(received) != -1) {
				
				
		        if(adress.length() < 1) {
					BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			        System.out.println("Enter domain or IP address AND level od domain... ");
		            adress = in.readLine();
			        int k = 0;
			        String domain = "";
			        domain += k;
			        domain += " "; 
			        domain += adress;
			        send(client, addr, port, domain);
		        }
	
		        String data = receive(client);
		            
		        received = data;
		        int levelOfDomain = getLevelOfDomain(data);
		            
		        if(levelOfDomain == 0) {
		         	adress = "";
		           	continue;
		        }
		            
		        levelOfDomain++;
		        send(client, addr, port, getMessage(levelOfDomain, adress));
		    }
			client.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
	}
	
	private static String receive(DatagramSocket server) throws IOException {
		byte[] receivebyte = new byte[1024];
        DatagramPacket receiver = new DatagramPacket(receivebyte, receivebyte.length);
        server.receive(receiver);
   
        addr = receiver.getAddress();
        port = receiver.getPort();
        
        String s = new String(receiver.getData());
        System.out.println("From " + addr + ", port: " + port + " received: " + s);
        return s;
	}
	
	private static void send(DatagramSocket server, InetAddress addr, int port, String msg) throws IOException {
		DatagramPacket sender = new DatagramPacket(msg.getBytes(), msg.getBytes().length, addr, port);
        server.send(sender);
		System.out.println("  To " + addr + ", port: " + port + " sent: " + msg);
	}
	
	private static int getLevelOfDomain(String received) {
		String s = "";
		for(Character c : received.toCharArray()) {
			if(c != ' ') {
				s += c;
			} else break;
		}
		
		return Integer.parseInt(s);
	}
	
	private static String getAdress(String received) {
		StringBuffer s = new StringBuffer();
		char[] array = received.toCharArray();
		for(int i = array.length-1; i >= 0; i--) {
			if(array[i] != ' ') {
				s.append(array[i]);
			} else break;;
		}
		s.reverse();
		return s.toString();
	}
	
	private static String getMessage(int level, String adress) {
		String s = "";
		s += Integer.toString(level);
		s += " ";
		s += adress;
		return s;
	}
}
