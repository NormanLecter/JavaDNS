package core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.google.protobuf.InvalidProtocolBufferException;

import core.ProtosMessageDNS.*;
import core.ProtosMessageDNS.MessageDNS.Answer;
import core.ProtosMessageDNS.MessageDNS.Header;
import core.ProtosMessageDNS.MessageDNS.Question;

public class Main {
	private static final int MAX_ANSWER_SIZE = 2054;
	
	private static InetAddress addr;
	private static int port;
	
	public static void main(String args[]) {
		boolean serverRunning = true;
		
	    try {
	    	DatagramSocket server = new DatagramSocket(53);
	    	
	        while(serverRunning) {
	            String str = receive(server);
	            String adress = getAdress(str);
	           /* 
	            int levelOfDomain = getLevelOfDomain(str);
	            
	            switch(levelOfDomain) {
	            case 0: {
	            	// sprawdzanie czy istnieje taka .domena je¿eli tak to odes³anie ze zwiêkszeniem levelOfDomain
	            	String domain = getDomain(str);

	            	levelOfDomain++;     	
                    //send(server, addr, port, getMessage(levelOfDomain, adress));

	            	break; }
	            	
	            case 2: {
	            	// sprawdzenie czy istnieje taka strona.domena je¿eli tak to odes³anie ze zwiêkszeniem levelOfDomain
	            	String nameOfSite = getNameOfSite(str);
	 
	            	levelOfDomain++;    	
                   // send(server, addr, port, getMessage(levelOfDomain, adress));
	            	break; }
	            	
	            case 4: {
	            	// TODO odnalezienie adresu IP strony i odes³anie wrz z levelOfDomain = 0;
                   // send(server, addr, port, getMessage(0, "26.07.19.96"));
	            	break; }
	            }
	            */
	       }
	    } catch(Exception e) {
	    	System.out.println(e);
	    }
	    System.out.println("Server down.");
    }	
	
	private static String receive(DatagramSocket server) throws IOException {
		byte[] receivebyte = new byte[MAX_ANSWER_SIZE];

        DatagramPacket receiver = new DatagramPacket(receivebyte, receivebyte.length);
        server.receive(receiver);
	
        addr = receiver.getAddress();
        port = receiver.getPort();
        
		try {
			Answer answer = MessageDNS.Answer.parseFrom(receivebyte);
			System.out.println(answer.getNAME());
			System.out.println(answer.getTTL());
	        System.out.println("From " + addr + ", port: " + port + " received: " + receivebyte);
		
		} catch (InvalidProtocolBufferException e1) {
			e1.printStackTrace();
		}

        return "";
	}
	
	private static void send(DatagramSocket server, InetAddress addr, int port, String NAME, int TTL) throws IOException {
		MessageDNS.Answer.Builder answer = MessageDNS.Answer.newBuilder();
		answer.setNAME(NAME);
		answer.setTTL(TTL);
		
		byte[] bytes = answer.build().toByteArray();
		
		DatagramPacket sender = new DatagramPacket(bytes, bytes.length, addr, port);
        server.send(sender);
		System.out.println("  To " + addr + ", port: " + port + " sent: " + bytes.toString());
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
