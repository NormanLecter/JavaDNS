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
		
			byte[] sendbyte = new byte[1024]; // konterner wysylanych danych w bajtach
			byte[] receivebyte = new byte[1024]; // konterner odbierania danych w bajtach
			
			BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter domain or IP address... ");
            String domain = in.readLine();
            sendbyte = domain.getBytes();
			DatagramPacket sender = new DatagramPacket(sendbyte, sendbyte.length, address, 53); // 'spakowanie' wszystkiego do wyslania
			client.send(sender);
            
			//ODBIERANIE
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
