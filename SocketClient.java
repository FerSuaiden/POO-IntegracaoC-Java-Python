import java.io.*;
import java.net.Socket;

public class SocketClient {
    private String address;
    private int port = 12345; // Porta padrão, ajuste conforme necessário
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public SocketClient(String address) {
        this.address = address;
        connect();
    }

    private void connect() {
        try {
            socket = new Socket(address, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected to server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendCommand(String command) {
        out.println(command);
        try {
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
