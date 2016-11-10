package core;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Main {
	public static void main(String args[]) {
		boolean serverRunning = true;
		System.out.println("start");
	    try {
	    	DatagramSocket server = new DatagramSocket(53);
	    	
	        while(serverRunning) {
	        	byte[] sendbyte = new byte[1024];
	            byte[] receivebyte = new byte[1024];
	            
	            DatagramPacket receiver = new DatagramPacket(receivebyte, receivebyte.length);
	            server.receive(receiver);
	            
	            String str = new String(receiver.getData());
	            String s = str.trim();
	            
	            System.out.println("odebralem: " + s);
	            
	            InetAddress addr = receiver.getAddress();
	            int port = receiver.getPort();
	            
	            String ip[] = {"123.123.10.20", "321.321.01.02"};
	            String name[] = {"www.google.com", "www.jajestem.com"};
	            
	            for(int i = 0 ; i < ip.length; i++) {
	            	if(s.equals(ip[i])) {
	            		sendbyte = name[i].getBytes();
	                    DatagramPacket sender= new DatagramPacket(sendbyte, sendbyte.length, addr, port);
	                    server.send(sender);
	                    break;
	                } else if(s.equals(name[i])) {
	                	sendbyte = ip[i].getBytes();
	                    DatagramPacket sender = new DatagramPacket(sendbyte, sendbyte.length, addr, port);
	                    server.send(sender);
	                    break;
	                }   
	            }         
	            break;
	       }
	    } catch(Exception e) {
	    	System.out.println(e);
	    }
	    System.out.println("end");
    }
}
