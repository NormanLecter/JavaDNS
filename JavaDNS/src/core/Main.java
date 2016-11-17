package core;
import java.io.*;
import java.net.*;
import java.util.*;
//import core.WebsiteDataProto.*;


public class Main {
	private static String adress;
	private static String received;
	
	private static int getLevelOfDomain(String received) {
		String s = "";
		received = "0 1";
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
	
	public static void main(String[] args) {
		adress = "";
		
		// Z GITA CALY KATALOG COM.GOOGLE.PROTOBUF : package com.google.protobuf
		while(getLevelOfDomain(received) == 0 || getLevelOfDomain(received) == -1) {
			try{
				// WYSYLANIE
				//websiteData ONET = websiteData.newBuilder().setIpAddress("192.168.1.2").setName("Onet").setLevelOfDomain(0).build(); // test protoBuffer
				//byte[] webSend = ONET.newBuilderForType().toByteArray(); ===> BRAKUJE? ALBO BEZ TEGO I W DATAGRAM WRZUCIC LEVEL_OF_DOMAIN?
				
				DatagramSocket client = new DatagramSocket(); // socket do przesylania i odbierania  datagramow
				InetAddress address = InetAddress.getByName("192.168.41.106"); // klasa przechowujaca adres IP
			
				byte[] sendbyte = new byte[1024]; // konterner wysylanych danych w bajtach
				byte[] receivebyte = new byte[1024]; // konterner odbierania danych w bajtach
				
				BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
	            System.out.println("Enter domain or IP address AND level od domain... ");
	            
	            if(adress.length() < 1) {
	            	adress = in.readLine();
		            int k = 0;
		            String domain = "";
		            domain += k;
		            domain += " "; 
		            domain += adress;
		            sendbyte = domain.getBytes();
					DatagramPacket sender = new DatagramPacket(sendbyte, sendbyte.length, address, 53); // 'spakowanie' wszystkiego do wyslania
					client.send(sender);
	            }
				//ODBIERANIE
				DatagramPacket receiver = new DatagramPacket(receivebyte,receivebyte.length);
	            client.receive(receiver);
	            String data = new String(receiver.getData());
	            
	            int levelOfDomain = getLevelOfDomain(data);
	            levelOfDomain++;
	            String msg = getMessage(levelOfDomain, adress);
	            
	            sendbyte = msg.getBytes();
				DatagramPacket sende2r = new DatagramPacket(sendbyte, sendbyte.length, address, 53); // 'spakowanie' wszystkiego do wyslania
				client.send(sende2r);
	                
	            client.close();
			}
			catch(Exception e){
				System.out.println(e);
			}
		}
	}
	
	private static String getMessage(int level, String adress) {
		String s = "";
		s += Integer.toString(level);
		s += " ";
		s += adress;
		return s;
	}
}
