package core;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
//import core.WebsiteDataProto.*;


public class Main {
	private static String adress;
	private static String received;
	
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
	
	public static void main(String[] args) {
		adress = "";
		received = "9 9";
		
		DatagramSocket client;
		try {
			client = new DatagramSocket();
			InetAddress address = InetAddress.getByName("192.168.41.105"); // klasa przechowujaca adres IP
		// socket do przesylania i odbierania  datagramow
		
		
		// Z GITA CALY KATALOG COM.GOOGLE.PROTOBUF : package com.google.protobuf
		while(getLevelOfDomain(received) != 0) {
			try{
				// WYSYLANIE
				//websiteData ONET = websiteData.newBuilder().setIpAddress("192.168.1.2").setName("Onet").setLevelOfDomain(0).build(); // test protoBuffer
				//byte[] webSend = ONET.newBuilderForType().toByteArray(); ===> BRAKUJE? ALBO BEZ TEGO I W DATAGRAM WRZUCIC LEVEL_OF_DOMAIN?
				
				
			
				byte[] sendbyte = new byte[1024]; // konterner wysylanych danych w bajtach
				byte[] receivebyte = new byte[1024]; // konterner odbierania danych w bajtach
				

	            if(adress.length() < 1) {
					BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
		            System.out.println("Enter domain or IP address AND level od domain... ");
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
	            System.out.println("odebralem: " + data);
	            received = data;
	            int levelOfDomain = getLevelOfDomain(data);
	            levelOfDomain++;
	            String msg = getMessage(levelOfDomain, adress);
	            System.out.println("wyslalem: " + msg);
	            
	            sendbyte = msg.getBytes();
				DatagramPacket sende2r = new DatagramPacket(sendbyte, sendbyte.length, address, 53); // 'spakowanie' wszystkiego do wyslania
				client.send(sende2r);
	                

			}
			catch(Exception e){
				System.out.println(e);
			}
		}
        client.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
