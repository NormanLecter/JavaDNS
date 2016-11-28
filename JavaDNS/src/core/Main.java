package core;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import core.ProtosMessageDNS.MessageDNS;
import core.ProtosMessageDNS.MessageDNS.Answer;
import core.ProtosMessageDNS.MessageDNS.Header;
 
import com.google.protobuf.InvalidProtocolBufferException;
 
public class Main {
	private static final int MAX_ANSWER_SIZE = 2054;
    private static String domain = "";
    private static InetAddress addressIP;
    private static int port = 53;
    private static int levelOfDomain = 0;
    private static MessageDNS.Header.Builder HeaderDNS = MessageDNS.Header.newBuilder().setId(0).setQR(false)
    		.setOpcode(3).setAA(false).setTC(false).setRD(false).setZ(false).setRCODE(0).setQDCOUNT(0)
    		.setANCOUNT(0).setNSCOUNT(0).setARCOUNT(0);
    
    public static void main(String[] args) {
        try {
        	addressIP = InetAddress.getByName("150.254.144.3");
            DatagramSocket client = new DatagramSocket(); 
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            
            while(levelOfDomain != -1) {
            	if(domain.length() < 1) {
            		System.out.println();
            		System.out.println("Enter domain or IP address...");
            		domain = in.readLine();
            		System.out.println();
 
            		if(domain.length() > MAX_ANSWER_SIZE) {
            			domain = "";
            		    System.out.println("Domain address is too long, please try again.");
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
    	byte[] receivebyte = new byte[2103]; //!!!
    	DatagramPacket receiver = new DatagramPacket(receivebyte, receivebyte.length);
    	server.receive(receiver);
    	addressIP = receiver.getAddress();
    	port = receiver.getPort();
    	try {
    		Header HeaderDNS = MessageDNS.Header.parseFrom(receivebyte);
    		Answer AnswerDNS = HeaderDNS.getANSWERFIELD();
    		levelOfDomain = HeaderDNS.getId();
    		domain = AnswerDNS.getNAME();
    		System.out.println("<- port: "+ port + " address: " + addressIP + " receive: " + " level of domain - "+ levelOfDomain + " message - " + domain);
    	} catch (InvalidProtocolBufferException e1) {
    		e1.printStackTrace();
    	}
    }
        
    private static void send(DatagramSocket server) throws IOException {
    	fullMsg(); 
    	MessageDNS.Question.Builder QuestionDNS = MessageDNS.Question.newBuilder().setQNAME(domain).setQTYPE("question").setQCLASS("question");
    	HeaderDNS.setId(levelOfDomain).setQUESTIONFIELD(QuestionDNS).setQDCOUNT(1);
        byte[] bytes = HeaderDNS.build().toByteArray();
        
        DatagramPacket sender = new DatagramPacket(bytes, bytes.length, addressIP, port);
        server.send(sender);
        System.out.println("-> port: " + port + " address: "+ addressIP + " send: " + " level of domain - " + HeaderDNS.getId() + " message - " + QuestionDNS.getQNAME());
    }
        
    private static void fullMsg() {
        if(domain.length() <= MAX_ANSWER_SIZE) {
        	for(int i = domain.length(); i <= MAX_ANSWER_SIZE; i++) {
        		domain += " ";
        	}
        }
    }
}