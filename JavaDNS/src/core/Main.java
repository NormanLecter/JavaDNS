package core;
import java.io.*;
import java.net.*;
import java.util.*;
//import core.WebsiteDataProto.*;


public class Main {
	
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
	
	public static void main(String[] args) {
		// Z GITA CALY KATALOG COM.GOOGLE.PROTOBUF : package com.google.protobuf
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
            int k = 0;
            String domain = "";
            domain += k;
            domain += " "; 
            domain += in.readLine();
            sendbyte = domain.getBytes();
			DatagramPacket sender = new DatagramPacket(sendbyte, sendbyte.length, address, 53); // 'spakowanie' wszystkiego do wyslania
			client.send(sender);
            
			//ODBIERANIE
            int levelOfDomain = 0;
			DatagramPacket receiver = new DatagramPacket(receivebyte,receivebyte.length);
            client.receive(receiver);
            String data =new String(receiver.getData());
            
            System.out.println("IP address / domain : "+ data.trim()); // odczytanie usuwajac biale znaki
            client.close();
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
}
