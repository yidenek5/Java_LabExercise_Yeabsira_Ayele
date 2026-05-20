package network;

import javafx.application.Platform;
import ui.ServerFrame;

import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import util.Constants;

public class ChatServer {

    private final ServerFrame frame;

    public ChatServer(ServerFrame frame) {
        this.frame = frame;
    }

    public void start() {
        new Thread(() -> {
                try (ServerSocket server = new ServerSocket(Constants.PORT)) {
                Platform.runLater(() ->
                        frame.addText("System", "Server Started")
                );
                while (true) {

                    Socket socket = server.accept();
                    ClientHandler handler =
                            new ClientHandler(socket, frame);

                    handler.start();
                }

            } catch (BindException e) {
                Platform.runLater(() -> frame.addSystemText("Port " + Constants.PORT + " is already in use"));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }
}