import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.DatagramSocket;

class HelloWorldApp {
	public static void main(String[] args) {
		System.out.println("Hello World!"); // Display the string.
		try {
			String udpMsg = "hahaha";
			DatagramSocket ds = new DatagramSocket();
			InetAddress serverAddr = InetAddress.getByName("192.168.2.63");
			DatagramPacket dp;
			dp = new DatagramPacket(udpMsg.getBytes(), udpMsg.length(), serverAddr, 5000);
			ds.send(dp);

		} catch (Exception e) {

		}


	}}
