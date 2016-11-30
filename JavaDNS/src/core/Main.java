package core;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
 
import org.json.JSONArray;
import org.json.JSONObject;
 
import com.google.protobuf.InvalidProtocolBufferException;
 
import core.ProtosMessageDNS.*;
import core.ProtosMessageDNS.MessageDNS.Header;
import core.ProtosMessageDNS.MessageDNS.Question;
 
public class Main {
	private static final int MAX_ANSWER_SIZE = 2054;
        
	private static InetAddress addr;
	private static int port;
	private static int levelOfDomain;
	private static String domain;
    private static MessageDNS.Header.Builder HeaderDNS = MessageDNS.Header.newBuilder().setId(0).setQR(false).setOpcode(3)
    		.setAA(false).setTC(false).setRD(false).setZ(false).setRCODE(0).setQDCOUNT(0).setANCOUNT(0).setNSCOUNT(0).setARCOUNT(0);
 
    public static void main(String args[]) {
    	boolean serverRunning = true;
    	try {
    		DatagramSocket server = new DatagramSocket(53);
    		while(serverRunning) {
    			receive(server);
                     
    			if(!correctAddress(domain.trim())) {
    				levelOfDomain = 0;
    				domain = "Address is incorrect";
    			} else
                    
    			switch(levelOfDomain) {
                case 0: {
                	File file = new File("adresses/extension.json");
                    try {
                    	String json = "";
                    	Scanner scanner = new Scanner(file);
                    	while(scanner.hasNext())
                    		json += scanner.next();
                                
                    	JSONObject obj = new JSONObject(json);
                    	JSONArray array = obj.getJSONArray("data");
                                
                    	boolean exist = false;
                    	for(int i = 0; i < array.length(); i++) 
                    		if(array.get(i).equals(getDomain().trim()))
                    			exist = true;
                    	scanner.close();
                                
                    	if(!exist) {
                    		throw new Exception("404 not found");
                    	} else {
                    		levelOfDomain++;
                    		
                    		File file2 = new File("adresses/" + getDomain().trim() + ".json");
                        	scanner = new Scanner(file2);
                        	json = "";
                        	while(scanner.hasNext())
                        		json += scanner.next();
                        	JSONObject obj2 = new JSONObject(json);
                        	domain = obj2.getString("ip");
                    	}
                    } catch (Exception e) {
                    	e.printStackTrace();
                        levelOfDomain = 0;
                        domain = "404 not found";
                        fullMsg();
                    }
                    break; }
                case 2: {
                	File file = new File("adresses/" + getDomain().trim() + ".json");
                	try {
                		String json = "";
                		Scanner scanner = new Scanner(file);
                		while(scanner.hasNext())
                			json += scanner.next();
                                
                		JSONObject obj = new JSONObject(json);
                		JSONArray array = obj.getJSONArray("data");
                                
                		boolean exist = false;
                		for(int i = 0; i < array.length(); i++) {
                			if(array.get(i).equals(getNameOfSite().trim()))
                				exist = true;
                			}
                		scanner.close();
                		if(!exist) {
                			throw new Exception("404 not found");
                			} else {
                        		levelOfDomain++;
                        		
                        		File file2 = new File("adresses/" + getNameOfSite().trim() + ".json");
                            	scanner = new Scanner(file2);
                            	json = "";
                            	while(scanner.hasNext())
                            		json += scanner.next();
                            	JSONObject obj2 = new JSONObject(json);
                            	domain = obj2.getString("ip");
                        	}
                	} catch (Exception e) {
                		e.printStackTrace();
                        levelOfDomain = 0;
                        domain = "404 not found";
                        fullMsg();
                    }
                	break; }
                        
                case 4: {
                	File file = new File("adresses/" + getNameOfSite().trim() + ".json");
                	try {
                		String json = "";
                		Scanner scanner = new Scanner(file);
                		while(scanner.hasNext())
                			json += scanner.next();
                		JSONObject obj = new JSONObject(json);
                		JSONArray array = obj.getJSONArray("data");
                		domain = array.getString(0);
                		fullMsg();
                		levelOfDomain = 0;                      
                		scanner.close();
                	} catch (Exception e) {
                		e.printStackTrace();
                		levelOfDomain = 0;
                		}       
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
    	byte[] receivebyte = new byte[2105]; //!!!
	    DatagramPacket receiver = new DatagramPacket(receivebyte, receivebyte.length);
	    server.receive(receiver);
	    addr = receiver.getAddress();
	    port = receiver.getPort();
	    try {
	      	Header HeaderDNS = MessageDNS.Header.parseFrom(receivebyte);
	        Question QuestionDNS = HeaderDNS.getQUESTIONFIELD();
	        levelOfDomain = HeaderDNS.getId();
	        domain = QuestionDNS.getQNAME();
	        System.out.println("<- port: "+ port + " address: " + addr + " receive: " + " level of domain - "+ levelOfDomain + " message - " + domain);
	        domain = QuestionDNS.getQNAME().toLowerCase();
	    } catch (InvalidProtocolBufferException e1) {
	    	e1.printStackTrace();
	    }
	}
        
    private static void send(DatagramSocket server) throws IOException {
    	fullMsg(); 
    	MessageDNS.Answer.Builder AnswerDNS = MessageDNS.Answer.newBuilder().setNAME(domain).setTYPE("answer").setCLASS("answer").setTTL(0);
    	HeaderDNS.setId(levelOfDomain).setANSWERFIELD(AnswerDNS);
    	byte[] bytes = HeaderDNS.build().toByteArray();
    	DatagramPacket sender = new DatagramPacket(bytes, bytes.length, addr, port);
    	server.send(sender);
    	System.out.println("-> port: " + port + " address: "+ addr + " send: " + " level of domain - " + HeaderDNS.getId() + " message - " + AnswerDNS.getNAME());
    }
        
    private static void fullMsg() {
        if(domain.length() <= MAX_ANSWER_SIZE) {
        	for(int i = domain.length(); i <= MAX_ANSWER_SIZE; i++) {
        		domain += " ";
        	}
        }
    }
                        
    private static String getDomain() {             
    	int dots = 0;           
                                
    	for(Character c : domain.toCharArray())                 
    		if(c == '.')            
    			dots++;         
                                
    	StringBuffer s = new StringBuffer();            
    	char[] array = domain.toCharArray();            
    	for(int i = array.length-1; i >= 0; i--) {              
    		if(dots <= 1) {         
    			s.deleteCharAt(s.length()-1);           
    			break;          
    		}               
                                        
    		s.append(array[i]);             
                                        
    		if(array[i] == '.')             
    			dots--;         
    		}               
    	s.reverse();            
                                
    	return s.toString();            
    }               
                        
    private static String getNameOfSite() {         
    	StringBuffer s = new StringBuffer();            
    	s.append(domain);               
    	for(int i = 0; i < 4 && i < s.length(); i++)            
    		s.deleteCharAt(0);                               
    	return s.toString();            
    }
        
    private static boolean correctAddress(String address) {
    	if(address.length() < 7) 
    		return false;
                
    	StringBuilder s = new StringBuilder(address);
    	if(!s.substring(0, 4).equals("www."))
    		return false;
                
    	if(!s.substring(4, s.length()-2).contains("."))
    		return false;
                
    	for(int i = 1; i < s.length(); i++)
    		if(s.charAt(i-1) == '.' && s.charAt(i) == '.')
    			return false;
                
    	if(s.charAt(s.length()-1) == '.')
    		return false;
    	return true;
    }
}
