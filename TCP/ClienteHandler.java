package TCP;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClienteHandler implements Runnable {

    private static final ArrayList<ClienteHandler> clienteHandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String nombreCliente;

    public ClienteHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        this.nombreCliente = bufferedReader.readLine();

        synchronized (clienteHandlers) {
            if (clienteHandlers.size() >= 3) {
                bufferedWriter.write("Server: Sala llena (máximo 3 usuarios).");
                bufferedWriter.newLine();
                bufferedWriter.flush();
                cerrarComunicacion();
                return;
            }
            clienteHandlers.add(this);
        }

        mandarMensajeATodos("Server: se ha unido " + nombreCliente, this);
    }

    @Override
    public void run() {
        String mensajeDesdeCliente;

        try {
            while (socket.isConnected() && (mensajeDesdeCliente = bufferedReader.readLine()) != null) {
                // Reenviar el mensaje a los demás
                mandarMensajeATodos(mensajeDesdeCliente, this);
            }
        } catch (IOException e) {
        } finally {
            cerrarComunicacion();
        }
    }

    private void mandarMensajeATodos(String mensaje, ClienteHandler emisor) {
        synchronized (clienteHandlers) {
            for (ClienteHandler ch : clienteHandlers) {
                // si no quieres que el emisor se lo vea a sí mismo
                if (ch != emisor) {
                    try {
                        ch.bufferedWriter.write(mensaje);
                        ch.bufferedWriter.newLine();
                        ch.bufferedWriter.flush();
                    } catch (IOException e) {
                        ch.cerrarComunicacion();
                    }
                }
            }
        }
    }

    private void quitarClienteHandler() {
        synchronized (clienteHandlers) {
            clienteHandlers.remove(this);
        }
        mandarMensajeATodos("Server: se ha ido " + nombreCliente, this);
    }

    private void cerrarComunicacion() {
        quitarClienteHandler();
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
