package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Server {

    static class ClienteUDPInfo {
        InetAddress ip;
        int puerto;
        String nombre;

        ClienteUDPInfo(InetAddress ip, int puerto, String nombre) {
            this.ip = ip;
            this.puerto = puerto;
            this.nombre = nombre;
        }
    }

    private static final ArrayList<ClienteUDPInfo> clientes = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(5000);
        byte[] buffer = new byte[1024];

        System.out.println("Servidor UDP escuchando en 5000...");

        while (true) {
            DatagramPacket recibido = new DatagramPacket(buffer, buffer.length);
            socket.receive(recibido);

            String msg = new String(recibido.getData(), 0, recibido.getLength()).trim();
            InetAddress ip = recibido.getAddress();
            int puerto = recibido.getPort();

            // Formato esperado: "NOMBRE|mensaje"
            // Ej: "Cliente 1|Hola"
            String[] partes = msg.split("\\|", 2);
            if (partes.length != 2) continue;

            String nombre = partes[0];
            String texto = partes[1];

            // Registrar cliente si no existe
            ClienteUDPInfo actual = null;
            for (ClienteUDPInfo c : clientes) {
                if (c.ip.equals(ip) && c.puerto == puerto) {
                    actual = c;
                    break;
                }
            }

            if (actual == null) {
                if (clientes.size() >= 3) {
                    String lleno = "Server: Sala llena (máximo 3 usuarios).";
                    byte[] data = lleno.getBytes();
                    DatagramPacket p = new DatagramPacket(data, data.length, ip, puerto);
                    socket.send(p);
                    continue;
                }
                actual = new ClienteUDPInfo(ip, puerto, nombre);
                clientes.add(actual);

                String join = "Server: se ha unido " + nombre;
                broadcast(socket, join, ip, puerto);
                continue;
            }

            String salida = nombre + ": " + texto;
            broadcast(socket, salida, ip, puerto);
        }
    }

    private static void broadcast(DatagramSocket socket, String mensaje, InetAddress ipEmisor, int puertoEmisor) throws Exception {
        byte[] data = mensaje.getBytes();
        for (ClienteUDPInfo c : clientes) {
            if (!(c.ip.equals(ipEmisor) && c.puerto == puertoEmisor)) {
                DatagramPacket p = new DatagramPacket(data, data.length, c.ip, c.puerto);
                socket.send(p);
            }
        }
    }
}
