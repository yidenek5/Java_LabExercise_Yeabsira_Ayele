package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;

import database.MessageRepository;
import javafx.application.Platform;
import model.Message;
import service.BroadcastService;
import service.FileService;
import ui.ServerFrame;

public class ClientHandler extends Thread {

    private final Socket socket;
    private final ServerFrame frame;

    private final MessageRepository repo =
            new MessageRepository();

    public ClientHandler(Socket socket,
                         ServerFrame frame) {

        this.socket = socket;
        this.frame = frame;
    }
    @Override
    public void run() {

        DataOutputStream out = null;

        try {

            DataInputStream in =
                    new DataInputStream(socket.getInputStream());

            out =
                    new DataOutputStream(socket.getOutputStream());

            BroadcastService.addClient(out);
                replayHistory(out);

            while (true) {

                String type = in.readUTF();
                String sender = in.readUTF();
                System.out.println("[ClientHandler] received type=" + type + " from " + sender);

                if (PacketType.TEXT.equals(type)) {

                    String msg = in.readUTF();

                    repo.save(
                            new Message(
                                    sender,
                                    type,
                                    msg,
                                    null,
                                    null
                            )
                    );

                    BroadcastService.broadcastText(
                            sender,
                            msg,
      out
                    );

                    Platform.runLater(() ->
                            frame.addText(sender, msg)
                    );
                } else if (PacketType.RELOAD.equals(type)) {
                    System.out.println("[ClientHandler] reload requested by " + sender);
                    replayHistory(out);
                    } else if (PacketType.FILE.equals(type)) {

                        String fileName = in.readUTF();
                        String mimeType = in.readUTF();
                        long length = in.readLong();
                        byte[] data = in.readNBytes((int) length);

                        System.out.println("[ClientHandler] file received from " + sender + ": " + fileName + " (" + mimeType + ") size=" + length);
                            System.out.println("[ClientHandler] read bytes length=" + (data == null ? 0 : data.length));

                        FileService.save(fileName, data);

                        repo.save(
                            new Message(
                                sender,
                                mimeType,
                                null,
                                fileName,
                                data
                            )
                        );

                        long savedSize = data == null ? 0 : data.length;
                        Platform.runLater(() ->
                            frame.addSystemText("Saved file '" + fileName + "' from " + sender + " (" + savedSize + " bytes)")
                        );

                        BroadcastService.broadcastFile(
                            sender,
                            mimeType,
                            fileName,
                            data,
                            out
                        );

                        Platform.runLater(() ->
                            frame.addFile(sender, fileName, mimeType)
                        );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                BroadcastService.removeClient(out);
            }
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }
    }

    private void replayHistory(DataOutputStream out) {
        try {
            List<Message> history = repo.getAll();
            System.out.println("[ClientHandler] replaying " + history.size() + " messages from DB");

            for (Message message : history) {
                synchronized (out) {
                    // message.getType may contain the mime-type for file messages, so detect files
                    if (message.getFileData() != null && message.getFileName() != null) {
                        out.writeUTF(PacketType.FILE);
                        out.writeUTF(message.getSender());
                        out.writeUTF(message.getFileName() == null ? "file" : message.getFileName());
                        out.writeUTF(message.getType());
                        out.writeLong(message.getFileData().length);
                        out.write(message.getFileData());
                    } else {
                        out.writeUTF(PacketType.TEXT);
                        out.writeUTF(message.getSender());
                        out.writeUTF(message.getMessage() == null ? "" : message.getMessage());
                    }
                    out.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}                       