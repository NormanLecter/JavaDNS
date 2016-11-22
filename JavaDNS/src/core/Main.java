package core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.google.protobuf.InvalidProtocolBufferException;

import core.ProtosMessageDNS.*;
import core.ProtosMessageDNS.MessageDNS.Answer;

public class Main {
	private static final int MAX_ANSWER_SIZE = 2054;
	
	private static InetAddress addr;
	private static int port;
	private static int levelOfDomain;
	private static String domain;
	
	public static void main(String args[]) {
		boolean serverRunning = true;
		
	    try {
	    	DatagramSocket server = new DatagramSocket(53);
	    	
	        while(serverRunning) {
	            String str = receive(server);
	             
	            switch(levelOfDomain) {
	            case 0: {
	            	levelOfDomain++;
	            	break; }
	            	
	            case 2: {
	            	levelOfDomain++;
	            	break; }
	            	
	            case 4: {
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
	
	private static String receive(DatagramSocket server) throws IOException {
		byte[] receivebyte = new byte[MAX_ANSWER_SIZE];

        DatagramPacket receiver = new DatagramPacket(receivebyte, receivebyte.length);
        server.receive(receiver);
	
        addr = receiver.getAddress();
        port = receiver.getPort();
        
		try {
			Answer answer = MessageDNS.Answer.parseFrom(receivebyte);
			levelOfDomain = answer.getTTL();
			domain = answer.getNAME();
			
	        System.out.println("From " + addr + ", port: " + port + " received: " + domain + " " + levelOfDomain);
		
		} catch (InvalidProtocolBufferException e1) {
			e1.printStackTrace();
		}

        return "";
	}
	
	private static void send(DatagramSocket server) throws IOException {
        MessageDNS.Answer.Builder answerSend = MessageDNS.Answer.newBuilder();
        answerSend.setNAME(domain);
        answerSend.setTTL(levelOfDomain);
        byte[] bytes = answerSend.build().toByteArray();
        DatagramPacket sender = new DatagramPacket(bytes, bytes.length, addr, port);
        server.send(sender);
        System.out.println("-> To " + addr + ", port: " + port + " send: " + answerSend.getNAME() + " " + answerSend.getTTL());
	}
}
