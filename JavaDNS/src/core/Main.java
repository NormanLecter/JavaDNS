package core;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.google.protobuf.InvalidProtocolBufferException;

import core.ProtosMessageDNS.MessageDNS;
import core.ProtosMessageDNS.MessageDNS.Answer;
 
public class Main {
	private static final int MAX_ANSWER_SIZE = 2048;
	
	private static String domain = "";
	private static InetAddress addr;
	private static int port = 53;
	private static int levelOfDomain = 0;
        
    public static void main(String[] args) {
    	try {
        	addr = InetAddress.getByName("150.254.144.3");
        	
    		DatagramSocket client = new DatagramSocket(); 
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    		
    		while(levelOfDomain != -1) {
    			if(domain.length() < 1) {
    				System.out.println("Enter domain or IP address...");
    				
    				domain = in.readLine();
    				
    				if(domain.length() > MAX_ANSWER_SIZE) {
    					domain = "";
    					System.out.println("Address is too long.");
    					continue;
    				}
    				
    				send(client);
    			}
	       		receive(client);                                
				      
	    		if(levelOfDomain == 0) {
	    			domain = "";
	    			continue;
	    		}
	    			
	    		levelOfDomain++;
	    		fullMsg();
	    		send(client);
    		}
    	} catch (Exception e1) {
    		e1.printStackTrace();
    	} 
    }  
    
	private static void receive(DatagramSocket server) throws IOException {
		byte[] receivebyte = new byte[MAX_ANSWER_SIZE + 6];

        DatagramPacket receiver = new DatagramPacket(receivebyte, receivebyte.length);
        server.receive(receiver);
        
        addr = receiver.getAddress();
        port = receiver.getPort();
        
		try {
			Answer answer = MessageDNS.Answer.parseFrom(receivebyte);
			levelOfDomain = answer.getTTL();
			domain = answer.getNAME();
			
			System.out.println(port + " <- " + addr + " send: " + levelOfDomain + " " + domain);
		
		} catch (InvalidProtocolBufferException e1) {
			e1.printStackTrace();
		}
	}
	
	private static void send(DatagramSocket server) throws IOException {
        MessageDNS.Answer.Builder answerSend = MessageDNS.Answer.newBuilder();
        fullMsg();
        answerSend.setNAME(domain);
        answerSend.setTTL(levelOfDomain);
        byte[] bytes = answerSend.build().toByteArray();

        DatagramPacket sender = new DatagramPacket(bytes, bytes.length, addr, port);
        server.send(sender);
        System.out.println(port + " -> " + addr + " send: " + answerSend.getTTL() + " " + answerSend.getNAME());
	}
	
    private static void fullMsg() {
    	if(domain.length() <= MAX_ANSWER_SIZE) {
    		for(int i = domain.length(); i <= MAX_ANSWER_SIZE; i++) {
    			domain += " ";
    		}
    	}
    }
}