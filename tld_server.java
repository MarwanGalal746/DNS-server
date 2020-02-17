import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class tld_server {
    public static String arr[][] = {
            {"www.yahoo.com","127.0.0.1 13"},
            {"www.google.com","127.9.9.1"},
            {"www.fci.com","192.168.10.10"},
            {"www.ibm.com","192.168.2.149 servereast.backup2.ibm.com"}
    };
    public static DatagramSocket datagramSocket;
    public static byte buffer[];
    public static DatagramPacket packin , packout;
    public static InetAddress host;
    public static void main(String[] args) throws IOException {
        System.out.println("TLD DNS server connected");
        while (true) {
            try {
                host = InetAddress.getLocalHost();
            } catch (Exception e) {
                System.out.println("Can't find host ..");
            }
            handle_local_server();
        }
    }
    public static void handle_local_server() {
        String message;
        try {
            datagramSocket = new DatagramSocket(4774);
            buffer = new byte[256];
            packin = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(packin);
            InetAddress host = InetAddress.getLocalHost();
            int client_port = packin.getPort();
            message = new String(packin.getData(), 0, packin.getLength());
            String to="notthere";
            String query="";
            for (int i=0;i<arr.length;i++){
                if(message.equals(arr[i][0])){
                    // string to store ip of host name
                    String temp=arr[i][1];
                    //string to store canonical name
                    String canon="";
                    //string to store the statement which will be sent to client
                    String IP="";
                    to="";
                    to+="URL = "+message+" ,IP address = ";
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
                    to+='\n'+"Server name: TLD DNS server";
                    //string to store which will be printed in the local server
                    String here="Client request: "+message+'\n'+"URL    :: "+message+'\n'+"query type: A"+'\n';
                    here+="IP address:: "+IP+'\n';
                    if(query.equals("CNAME")){
                        to+='\n'+"Canonical name: "+canon+'\n'+"Aliases name: "+message;
                        here+="Query type: "+query+'\n';
                        here+="Canonical name: "+canon+'\n';
                        here+="Aliases: "+message;
                    }
                    System.out.println(here);
                    break;
                }
            }
            if(!message.equals("notthere"))
                System.out.println("nothere");
            packout = new DatagramPacket(to.getBytes() , to.length() , host , client_port);
            datagramSocket.send(packout);
            buffer = new byte[256];


        }
        catch (Exception e){
            datagramSocket.close();
        }
    }
}