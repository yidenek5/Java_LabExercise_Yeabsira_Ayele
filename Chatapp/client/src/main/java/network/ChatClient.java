package network;

import ui.ChatFrame;
import util.ClientConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ChatClient {

    private DataOutputStream out;

    private PacketSender sender;

    public ChatClient(ChatFrame frame, String username) {

        new Thread(() -> {
            try {

                Socket socket = new Socket(ClientConstants.HOST, ClientConstants.PORT);

                DataInputStream in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

                sender = new PacketSender(out);

                sender.sendText("SYSTEM", username + " joined");

                frame.addSystemText("Connected as " + username);

                new Thread(new PacketReceiver(in, frame)).start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public PacketSender getSender() {
        return sender;
    }
}