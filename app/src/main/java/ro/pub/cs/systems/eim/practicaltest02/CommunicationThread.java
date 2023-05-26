package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class CommunicationThread extends Thread {

    private final ServerThread serverThread;
    private final Socket socket;

    // Constructor of the thread, which takes a ServerThread and a Socket as parameters
    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    // run() method: The run method is the entry point for the thread when it starts executing.
    // It's responsible for reading data from the client, interacting with the server,
    // and sending a response back to the client.
    @Override
    public void run() {
        // It first checks whether the socket is null, and if so, it logs an error and returns.
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            // Create BufferedReader and PrintWriter instances for reading from and writing to the socket
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");

            // Read operation data sent by the client
            String operationData = bufferedReader.readLine();
            if (operationData == null || operationData.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }


            String[] parts = operationData.split(",");
            String operation = parts[0];
            Integer operand1 = Integer.valueOf(parts[1]);
            Integer operand2 = Integer.valueOf(parts[2]);
            if (operation.equals("mul")) {
                Thread.sleep(2000);
            }

            // Send the information back to the client
            String result;
            switch (operation) {
                case "add":
                    Integer x = operand1 + operand2;
                    if (x > Integer.MAX_VALUE)
                        result = "overflow";
                    else
                        result = String.valueOf(x);
                    break;
                case "mul":
                    Integer y = operand1 * operand2;
                    if (y > Integer.MAX_VALUE)
                        result = "overflow";
                    else
                        result = String.valueOf(y);
                    break;
                default:
                    result = "[COMMUNICATION THREAD] Error";
            }

            // Send the result back to the client
            printWriter.println(result);
            printWriter.flush();
        } catch (IOException | InterruptedException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }

}