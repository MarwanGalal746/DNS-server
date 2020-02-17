import java.io.*;
import java.net.*;

public class local_server {
    private static final int PORT = 1234;
    private static DatagramSocket datagramSocket;
    private static DatagramPacket packet, outPacket,rp;
    private static byte[] buffer;
    private static byte[] buf;
    private static InetAddress clientAddress;
    private static int clientPort,cp;
    public static String message_from_client=" ";
    public static String arr[][]={
            {"www.yahoo.com","127.0.0.1 13"},
            {"www.google.com","127.9.9.1"}
    };

    public static void main(String[] args)throws IOException {
        // TODO code application logic here
        System.out.println("local Server started.");
        byte[] buffer = new byte[512];
        datagramSocket = new DatagramSocket(1234);
        while (true)
        {
            try {
                DatagramPacket packet =  new DatagramPacket(buffer, buffer.length );
                datagramSocket.receive(packet);
                clientAddress = packet.getAddress();
                clientPort = packet.getPort();
                if(packet.getPort()!=1420&&packet.getPort()!=4774&&packet.getPort()!=7887)
                {
                    cp=packet.getPort();
                }
                //new socket created with random port for thread
                DatagramSocket threadSocket = new DatagramSocket();

                Thread t = new Thread(new CLIENTConnection(threadSocket, packet));

                t.start();
            } catch (Exception e)
            {
                System.err.println("Error in connection attempt.");
            }
        }

    }
    public static class CLIENTConnection implements Runnable{
        private int clientID;

        private DatagramSocket datagramSocket=null;
        private DatagramPacket packet=null;
        private DatagramPacket outPacket;


        public CLIENTConnection(DatagramSocket datagramSocket, DatagramPacket packet) throws IOException
        {
            this.datagramSocket = local_server.datagramSocket;
            this.packet = packet;
        }
        public void run()
        {
            String to="notfound";
            String msg = new String(packet.getData(), 0, packet.getLength());
            String query="";
            if(packet.getPort()==cp) {
                message_from_client= msg;
            }
            for (int i=0;i<arr.length;i++){
                if(msg.equals(arr[i][0])){
                    // string to store ip of host name
                    String temp=arr[i][1];
                    //string to store canonical name
                    String canon="";
                    //string to store the statement which will be sent to client
                    String IP="";
                    to="";
                    to+="URL = "+msg+" ,IP address = ";
                    //loop to check if there is aliases name
                    for(int r=0; r <temp.length() ; r++){
                        IP+=temp.charAt(r);
                        if(temp.charAt(r) == ' ' && ((int)temp.charAt(r+1)<(int)'0' || (int)temp.charAt(r+1)>(int)'9')){
                            for(int j=r+1 ; r<temp.length() ; r++){
                                canon+=temp.charAt(r);
                            }
                            query+="CNAME";
                            break;
                        }
                    }
                    to+=IP;
                    to+=" ,query type = A"+" ,"+query;
                    to+='\n'+"Server name: Local DNS server";
                    //string to store which will be printed in the local server
                    String here="Client request: "+msg+'\n'+"URL    :: "+msg+'\n'+"query type: A"+'\n';
                    here+="IP address:: "+IP+'\n';
                    if(query.equals("CNAME")){
                        to+='\n'+"Canonical name: "+canon+'\n'+"Aliases name: "+msg;
                        here+="Query type: "+query+'\n';
                        here+="Canonical name: "+canon+'\n';
                        here+="Aliases: "+msg;
                    }
                    System.out.println(here);
                    outPacket = new DatagramPacket(to.getBytes(),to.length(),clientAddress,clientPort);
                    try {
                        datagramSocket.send(outPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(to.equals("notfound") && packet.getPort()==cp){
                try {
                    outPacket=new DatagramPacket(msg.getBytes(),msg.length(),InetAddress.getByName("localhost"),1420);
                    datagramSocket.send(outPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(packet.getPort()==1420){
                //System.out.println("okay");
                msg= new String(packet.getData(),0,packet.getLength());
                System.out.println(msg);
                if(!msg.equals("nothere")) {
                    try {
                        outPacket = new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName("localhost"), cp);
                        datagramSocket.send(outPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(msg.equals("nothere")){
                    try {
                        outPacket=new DatagramPacket(message_from_client.getBytes(),message_from_client.length(),InetAddress.getByName("localhost"),4774);
                        datagramSocket.send(outPacket);
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if(packet.getPort()==4774){
                if(!msg.equals("notthere")) {
                    try {
                        outPacket = new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName("localhost"), cp);
                        datagramSocket.send(outPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(msg.equals("notthere")){
                    try {
                        outPacket=new DatagramPacket(message_from_client.getBytes(),message_from_client.length(),InetAddress.getByName("localhost"),7887);
                        datagramSocket.send(outPacket);
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if(packet.getPort()==7887){
                if(!msg.equals("not")) {
                    try {
                        outPacket = new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName("localhost"), cp);
                        datagramSocket.send(outPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

}

