package network;

import javafx.application.Platform;
import ui.ChatFrame;
import service.DownloadService;

import java.io.DataInputStream;
import java.io.File;

public class PacketReceiver implements Runnable {

    private DataInputStream in;
    private ChatFrame frame;

    public PacketReceiver(DataInputStream in, ChatFrame frame) {
        this.in = in;
        this.frame = frame;
    }

    @Override
    public void run() {
        try {
            while (true) {

                String type = in.readUTF();
                String sender = in.readUTF();

                if ("TEXT".equals(type)) {
                    String msg = in.readUTF();

                    Platform.runLater(() ->
                            frame.addText(sender, msg)
                    );
                } else if ("FILE".equals(type)) {
                    String fileName = in.readUTF();
                    String mimeType = in.readUTF();
                    long length = in.readLong();
                    byte[] data = in.readNBytes((int) length);

                    System.out.println("[PacketReceiver] got FILE from " + sender + ": " + fileName + " (" + mimeType + ") size=" + length);

                    File savedFile = DownloadService.save(fileName, data);

                    Platform.runLater(() ->
                        frame.addFile(sender, fileName, mimeType, savedFile)
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}