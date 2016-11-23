package core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.google.protobuf.InvalidProtocolBufferException;

import core.ProtosMessageDNS.*;
import core.ProtosMessageDNS.MessageDNS.Answer;

public class Main {
	private static final int MAX_ANSWER_SIZE = 2048;
	
	private static InetAddress addr;
	private static int port;
	private static int levelOfDomain;
	private static String domain;
	
	public static void main(String args[]) {
		boolean serverRunning = true;
		
	    try {
	    	DatagramSocket server = new DatagramSocket(53);
	    	
	        while(serverRunning) {
	            receive(server);
	             
	            switch(levelOfDomain) {
	            case 0: {
	            	levelOfDomain++;
	            	break; }
	            	
	            case 2: {
	            	levelOfDomain++;
	            	break; }
	            	
	            case 4: {
	            	domain = "126.821.24.4";
	            	fullMsg();
	            	levelOfDomain = 0;
	            	
	            	break; }
	            }
	            send(server);
	       }
	    } catch(Exception e) {
	    	System.out.println(e);
	    }
	    System.out.println("Server down.");
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
			
			System.out.println(port + " <- " + addr + " receive: " + levelOfDomain + " " + domain);
		} catch (InvalidProtocolBufferException e1) {
			e1.printStackTrace();
		}
	}
	
	private static void send(DatagramSocket server) throws IOException {
        MessageDNS.Answer.Builder answerSend = MessageDNS.Answer.newBuilder();
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
