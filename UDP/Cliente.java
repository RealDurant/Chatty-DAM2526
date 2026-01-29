package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) throws Exception {
        Scanner teclado = new Scanner(System.in);

        System.out.print("Introduce tu nombre: ");
        String nombre = teclado.nextLine();

        DatagramSocket socket = new DatagramSocket();
        InetAddress serverIP = InetAddress.getByName("localhost");
        int serverPort = 5000;

        new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];
                while (true) {
                    DatagramPacket recibido = new DatagramPacket(buffer, buffer.length);
                    socket.receive(recibido);
                    String msg = new String(recibido.getData(), 0, recibido.getLength()).trim();
                    System.out.println(msg);
                }
            } catch (Exception ignored) {}
        }).start();

        enviar(socket, serverIP, serverPort, nombre + "|(se ha conectado)");

        while (true) {
            String texto = teclado.nextLine();
            enviar(socket, serverIP, serverPort, nombre + "|" + texto);
        }
    }

    private static void enviar(DatagramSocket socket, InetAddress ip, int puerto, String msg) throws Exception {
        byte[] data = msg.getBytes();
        DatagramPacket p = new DatagramPacket(data, data.length, ip, puerto);
        socket.send(p);
    }
}

