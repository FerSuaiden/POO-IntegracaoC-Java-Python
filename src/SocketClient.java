import java.io.*;
import java.net.Socket;

public class SocketClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    public void sendCommand(String command) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(command);

            String response;
            while ((response = in.readLine()) != null) {
                System.out.println("Response from server: " + response);
                // Processar a resposta do servidor
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
