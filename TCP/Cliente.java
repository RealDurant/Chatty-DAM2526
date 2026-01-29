package TCP;


import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String nombreCliente;

    public Cliente(Socket socket, String nombreCliente1) {
        try {
            this.socket = socket;
            this.nombreCliente = nombreCliente1;

            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bufferedWriter.write(nombreCliente);
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (IOException e) {
            cerrarComunicacion();
        }
    }

    public void escucharMensaje() {
        new Thread(() -> {
            String mensaje;
            while (socket.isConnected()) {
                try {
                    mensaje = bufferedReader.readLine();
                    if (mensaje == null) break;
                    System.out.println(mensaje);
                } catch (IOException e) {
                    break;
                }
            }
            cerrarComunicacion();
        }).start();
    }

    public void enviarMensaje() {
        try {
            Scanner teclado = new Scanner(System.in);
            while (socket.isConnected()) {
                String texto = teclado.nextLine();
                String mensaje = nombreCliente + ": " + texto;

                bufferedWriter.write(mensaje);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            cerrarComunicacion();
        }
    }

    private void cerrarComunicacion() {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner teclado = new Scanner(System.in);
        System.out.print("Introduce tu nombre: ");
        String nombreCliente = teclado.nextLine();

        Socket socket = new Socket("localhost", 5000);
        Cliente cliente = new Cliente(socket, nombreCliente);

        cliente.escucharMensaje();
        cliente.enviarMensaje();
    }
}

