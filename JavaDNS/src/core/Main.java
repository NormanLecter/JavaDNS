package core;
import java.io.*;
import java.net.*;
import core.ProtosMessageDNS.*;
import core.ProtosMessageDNS.MessageDNS.Answer;
import core.ProtosMessageDNS.MessageDNS.Question;


public class Main {
	private static InetAddress addr;
	private static int port = 53;
	private static String adress;
	private static String received;
	
	public static void main(String[] args) {
		adress = "";
		received = "9 9";
		
		DatagramSocket client;
		try {
			client = new DatagramSocket();
			addr = InetAddress.getByName("192.168.43.28");
			
			/* START PROTOBUF */
			MessageDNS.Builder MsgDNS = MessageDNS.newBuilder(); // ogolny builder, ogolna instancja - OGARNIJ WYSLANIE JAKO TO WLASNIE
			MessageDNS.Header.Builder HeaderDNS = MessageDNS.Header.newBuilder(); // id jako levelOfDomian
			MessageDNS.Question.Builder QuestionDNS = MessageDNS.Question.newBuilder(); // QNAME jako domena (ip lub nazwa)
			
			MessageDNS.Answer.Builder AnswerDNS = MessageDNS.Answer.newBuilder();
			
			/* KONIEC PROTOBUF */

			
			while(getLevelOfDomain(received) != -1) {
				
				
		        if(adress.length() < 1) {
					BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			        System.out.println("Enter domain or IP address AND level od domain... ");
			        String X = in.readLine();
			       if(X.length()<2048){
			        for(int i = X.length(); i<=2048; i++){
			        	X+=" ";
			        }
			        }
			        else{
			        	System.out.println("Too long address, please enter again... ");
			        	throw(new Exception());
			        }
			        send(client, addr, port, X, 0);
		        }
	
		        String data = receive(client);
		            
		        received = data;
		        int levelOfDomain = getLevelOfDomain(data);
		            
		        if(levelOfDomain == 0) {
		         	adress = "";
		           	continue;
		        }
		            
		        levelOfDomain++;
		       // send(client, addr, port, getMessage(levelOfDomain, adress));
		    }
			client.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
	}
	
	private static String receive(DatagramSocket server) throws IOException {
		byte[] receivebyte = new byte[1024];
        DatagramPacket receiver = new DatagramPacket(receivebyte, receivebyte.length);
        server.receive(receiver);
   
        addr = receiver.getAddress();
        port = receiver.getPort();
        
        String s = new String(receiver.getData());
        System.out.println("From " + addr + ", port: " + port + " received: " + s);
        return s;
	}
	
	private static void send(DatagramSocket server, InetAddress addr, int port, String NAME, int TTL) throws IOException {
		MessageDNS.Answer.Builder answer = MessageDNS.Answer.newBuilder();
		answer.setNAME(NAME);
		answer.setTTL(TTL);
		
		byte[] bytes = answer.build().toByteArray();
		
		DatagramPacket sender = new DatagramPacket(bytes, bytes.length, addr, port);
        server.send(sender);
        System.out.println(bytes.length);
		System.out.println("  To " + addr + ", port: " + port + " sent: " + bytes.toString());
		Answer answer2 = MessageDNS.Answer.parseFrom(bytes);
		System.out.println(answer2.getNAME());
		System.out.println(answer2.getTTL());
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
			} else break;;
		}
		s.reverse();
		return s.toString();
	}
	
	private static String getMessage(int level, String adress) {
		String s = "";
		s += Integer.toString(level);
		s += " ";
		s += adress;
		return s;
	}
}
