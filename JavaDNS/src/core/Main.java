package core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Main {
	private static InetAddress addr;
	private static int port;
	
	public static void main(String args[]) {
		boolean serverRunning = true;
		
	    try {
	    	DatagramSocket server = new DatagramSocket(53);
	    	
	        while(serverRunning) {
	            String str = receive(server);
	            String adress = getAdress(str);
	            
	            int levelOfDomain = getLevelOfDomain(str);
	            
	            switch(levelOfDomain) {
	            case 0: {
	            	// sprawdzanie czy istnieje taka .domena je¿eli tak to odes³anie ze zwiêkszeniem levelOfDomain
	            	String domain = getDomain(str);

	            	levelOfDomain++;     	
                    send(server, addr, port, getMessage(levelOfDomain, adress));

	            	break; }
	            	
	            case 2: {
	            	// sprawdzenie czy istnieje taka strona.domena je¿eli tak to odes³anie ze zwiêkszeniem levelOfDomain
	            	String nameOfSite = getNameOfSite(str);
	 
	            	levelOfDomain++;    	
                    send(server, addr, port, getMessage(levelOfDomain, adress));
	            	break; }
	            	
	            case 4: {
	            	// TODO odnalezienie adresu IP strony i odes³anie wrz z levelOfDomain = 0;
                    send(server, addr, port, getMessage(0, "26.07.19.96"));
	            	break; }
	            }
	       }
	    } catch(Exception e) {
	    	System.out.println(e);
	    }
	    System.out.println("Server down.");
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
	
	private static String getMessage(int level, String adress) {
		String s = "";
		s += Integer.toString(level);
		s += " ";
		s += adress;
		return s;
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
			} else break;
		}
		s.reverse();
		return s.toString();
	}
	
	private static String getDomain(String received) {
		int dots = 0;
		
		for(Character c : received.toCharArray()) 
			if(c == '.') 
				dots++;
		
		StringBuffer s = new StringBuffer();
		char[] array = received.toCharArray();
		for(int i = array.length-1; i >= 0; i--) {
			if(dots <= 1) {
				s.deleteCharAt(s.length()-1);
				break;
			}
			
			s.append(array[i]);
			
			if(array[i] == '.') 
				dots--;
		}
		s.reverse();
		
		return s.toString();
	}
	
	private static String getNameOfSite(String received) {
		StringBuffer s = new StringBuffer();
		s.append(getAdress(received));
		for(int i = 0; i < 4; i++) 
			s.deleteCharAt(i);
		
		return s.toString();
	}
}
