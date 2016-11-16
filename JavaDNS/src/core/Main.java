package core;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Main {
	public static void main(String args[]) {
		boolean serverRunning = true;
		
	    try {
	    	DatagramSocket server = new DatagramSocket(53);
	    	
	        while(serverRunning) {
	        	byte[] sendbyte = new byte[1024];
	            byte[] receivebyte = new byte[1024];
	            
	            DatagramPacket receiver = new DatagramPacket(receivebyte, receivebyte.length);
	            server.receive(receiver);
	            
	            String str = new String(receiver.getData());
	            String adress = getAdress(str);
	            int levelOfDomain = getLevelOfDomain(str);
	            
	            InetAddress addr = receiver.getAddress();
	            int port = receiver.getPort();
	            
	            //TODO podlaczyc jsony.
	            String first[] = {"org", "pl"};
	            String org[] = {"joemonster.org", "wikipedia.org"};
	            String pl[] = {"wolniak.pl", "osak.pl"};
	            
	            switch(levelOfDomain) {
	            case 0: {
	            	// sprawdzanie czy istnieje taka .domena je¿eli tak to odes³anie ze zwiêkszeniem levelOfDomain
	            	String domain = getDomain(str);
	            	String msg = "";
	            	
	            	boolean sent = false;
	            	for(String s : first) {
	            		if(s.equals(domain)) {
	    	            	levelOfDomain++;
	    	            	msg = getMessage(levelOfDomain, adress);
	    	            	sent = true;
	            		}
	            	}
	            	
	            	if(!sent) {
    	            	msg = getMessage(-1, adress);
	            	}

            		sendbyte = msg.getBytes();
                    DatagramPacket sender= new DatagramPacket(sendbyte, sendbyte.length, addr, port);
                    server.send(sender);

	            	break; }
	            	
	            case 2: {
	            	// sprawdzenie czy istnieje taka strona.domena je¿eli tak to odes³anie ze zwiêkszeniem levelOfDomain
	            	String nameOfSite = getNameOfSite(str);
	            	String msg = "";
	            	
	            	boolean sent = false;
	            	for(String s : org) {
	            		if(s.equals(nameOfSite)) {
	    	            	levelOfDomain++;
	    	            	msg = getMessage(levelOfDomain, adress);
	    	            	sent = true;
	            		}
	            	}
	            	
	            	if(!sent) {
    	            	msg = getMessage(-1, adress);
	            	}      

            		sendbyte = msg.getBytes();
                    DatagramPacket sender= new DatagramPacket(sendbyte, sendbyte.length, addr, port);
                    server.send(sender);
	            	break; }
	            	
	            case 4: {
	            	// TODO odnalezienie adresu IP strony i odes³anie wrz z levelOfDomain = 0;
	            	
	    	        levelOfDomain++;
	    	        String msg = getMessage(levelOfDomain, "26.07.19.96");

            		sendbyte = msg.getBytes();
                    DatagramPacket sender= new DatagramPacket(sendbyte, sendbyte.length, addr, port);
                    server.send(sender);
	            	break; }
	            }
	       }
	    } catch(Exception e) {
	    	System.out.println(e);
	    }
	    System.out.println("end");
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
			s.deleteCharAt(0);
		
		return s.toString();
	}
}
