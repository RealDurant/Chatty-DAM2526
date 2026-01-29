package TCP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void iniciarServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("Se ha conectado un nuevo cliente: " + socket);

                ClienteHandler clienteHandler = new ClienteHandler(socket);
                Thread thread = new Thread(clienteHandler);
                thread.start();
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
            cerrarServer();
        }
    }

    public void cerrarServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        Server server = new Server(serverSocket);
        server.iniciarServer();
    }
}