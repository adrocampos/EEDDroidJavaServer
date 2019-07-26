import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Executors;


public class Server {

    private static int port = 65432;

    public static void main(String[] args) throws Exception {
        try (var listener = new ServerSocket(port)) {
            System.out.println("The  server is running...");

            //Listens the incoming data
            var pool = Executors.newFixedThreadPool(20);
            while (true) {
                pool.execute(new Receiver(listener.accept()));
            }
        }
    }

    private static class Receiver implements Runnable {
        private Socket socket;
        String[] receivedStrings;
        double[] receivedDoubles;

        Receiver(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try {
                var in = new Scanner(socket.getInputStream());
                var out = new PrintWriter(socket.getOutputStream(), true);

                while (in.hasNextLine()) {

                    receivedStrings = in.nextLine().split(",");
                    receivedDoubles = new double[receivedStrings.length];

                    for (int i = 0; i < receivedStrings.length; i++) {
                        receivedDoubles[i] = Double.parseDouble(receivedStrings[i]);
                    }
                    System.out.println(Arrays.toString(receivedDoubles));

                }
            } catch (Exception e) {
                System.out.println("Error:" + socket);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                }
                System.out.println("Closed: " + socket);
            }
        }
    }
}