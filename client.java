import java.io.*;
import java.net.*;
import java.util.Scanner;
public class client {
    public static InetAddress host;
    final static int port=1234;
    public static DatagramSocket datagramSocket;
    public static byte buffer[];
    public static DatagramPacket packin, packout;

    public static void main(String[] args) throws IOException {
        System.out.println("Opening port ..");
        try {
            host = InetAddress.getLocalHost();
        } catch (Exception e) {
            System.out.println("Can't find host ..");
        }
        handle_server();
    }
    public static void handle_server() throws IOException {
        String message="";
        try {
            datagramSocket = new DatagramSocket();
            System.out.print("Enter Name / Enter 'quit' to exit\nClient: ");
            Scanner in = new Scanner(System.in);
            String to = in.nextLine();
            do{
                packout = new DatagramPacket(to.getBytes(), to.length(), host, port);
                datagramSocket.send(packout);
                buffer = new byte[256];
                packin = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(packin);
                message = new String(packin.getData(), 0, packin.getLength());
                System.out.println( message);
                System.out.print("Enter Name / Enter 'quit' to exit\nClient: ");
                to = in.nextLine();}while (!to.equals("quit"));
        }
        catch (Exception e){
            datagramSocket.close();
        }
    }
}
