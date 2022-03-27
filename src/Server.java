import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Server {

    public static final int PORT = 8189;
    private int port;
    private Thread thread;

    public static void main(String[] args) {
        Server server = new Server();
        server.startWork();
    }

    public Server(int port) {
        this.port = port;
    }

    public Server() {
        this(PORT);
    }

    private void startWork() {
        try (ServerSocket serverSocket = new ServerSocket(PORT);
             Socket clientSocket = serverSocket.accept();
             DataInputStream input = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())
        ) {
            System.out.println("Server start working. It is waiting some new connections");
            System.out.println("Client connected on");
            thread = threadInputMessage(input);
            sendOutputMessage(output);
        } catch (IOException e) {
            System.err.println("Error server connect! Port: " + PORT);
            e.printStackTrace();
        } finally {
            if (thread != null) thread.interrupt();
        }
    }

    private void sendOutputMessage(DataOutputStream output) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String message = scanner.next();
                output.writeUTF("Server " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-YYYY HH:mm:ss")) + ": " + message + System.lineSeparator());
                if (message.equals("/end")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Thread threadInputMessage(DataInputStream input) {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    String message = input.readUTF();
                    System.out.print(message);
                    if (message.equals("/end")) {
                        System.exit(0);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Message can not be written");
                    break;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

}
