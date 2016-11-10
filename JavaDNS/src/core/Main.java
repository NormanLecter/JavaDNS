package core;
import java.io.*;
import java.net.*;
import java.util.*;

public class Main {
	
	public static void main(String[] args) {
		
		try{
			// WYSYLANIE
			DatagramSocket client = new DatagramSocket(); // socket do przesylania i odbierania  datagramow
			InetAddress address = InetAddress.getByName("192.168.41.106"); // klasa przechowujaca adres IP
		
			byte[] sendbyte = new byte[1024]; // konterner wysylanych danych
			byte[] receivebyte = new byte[1024]; // konterner odbierania danych
			
			BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Wprowadz domene albo adres IP... ");
            String domain = in.readLine();
            System.out.println("Wczytalem i przygotowuje do wyslania... " + domain);
            sendbyte = domain.getBytes();
			DatagramPacket sender = new DatagramPacket(sendbyte, sendbyte.length, address, 1309); // spakowanie wszystkiego do wyslania
			client.send(sender);
            
			//ODBIERANIE
			DatagramPacket receiver = new DatagramPacket(receivebyte,receivebyte.length);
            client.receive(receiver);
            String data =new String(receiver.getData());
            System.out.println("IP adres / nazwa domeny : "+ data.trim()); // odczytanie usuwajac biale znaki
            client.close();
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
}
