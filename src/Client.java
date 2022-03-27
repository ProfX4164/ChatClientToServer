import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Client {

    public static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 8189;

    public static final String CONNECTION_ERROR_MESSAGE = "Error with connect to server!";
    private String host;
    private int port;
    private Thread thread;

    public static void main(String[] args) {
        Client client = new Client();
        client.connectServer();
    }

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Client() {
        this(SERVER_HOST, SERVER_PORT);
    }

    public void connectServer() {
        try (Socket socket = new Socket(host, port);
             DataInputStream socketInput = new DataInputStream(socket.getInputStream());
             DataOutputStream socketOutput = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.println("Client start working");
            thread = threadInputMessage(socketInput);
            sendOutputMessage(socketOutput);
        } catch (IOException e) {
            String errorMessage = CONNECTION_ERROR_MESSAGE;
            System.err.println(errorMessage);
            e.printStackTrace();
        } finally {
            if (thread != null) thread.interrupt();
        }
    }

    private void sendOutputMessage(DataOutputStream socketOutput) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String message = scanner.next();
                socketOutput.writeUTF("Client " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-YYYY HH:mm:ss")) + ": " + message + System.lineSeparator());
                if (message.equals("/end")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Thread threadInputMessage(DataInputStream socketInput) {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    String message = socketInput.readUTF();
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
