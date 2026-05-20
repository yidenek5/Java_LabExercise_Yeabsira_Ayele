package network;

import model.FilePayload;

import java.io.DataOutputStream;

public class PacketSender {

    private final DataOutputStream out;

    public PacketSender(DataOutputStream out) {
        this.out = out;
    }

    public void sendText(String sender, String message) {
        try {
            synchronized (out) {
                out.writeUTF("TEXT");
                out.writeUTF(sender);
                out.writeUTF(message);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFile(String sender, FilePayload filePayload) {
        try {
            synchronized (out) {
                System.out.println("[PacketSender] sending FILE: " + filePayload.getFileName() + " (" + filePayload.getMimeType() + ") size=" + filePayload.getData().length);
                out.writeUTF("FILE");
                out.writeUTF(sender);
                out.writeUTF(filePayload.getFileName());
                out.writeUTF(filePayload.getMimeType());
                out.writeLong(filePayload.getData().length);
                out.write(filePayload.getData());
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendReload(String sender) {
        try {
            synchronized (out) {
                out.writeUTF("RELOAD");
                out.writeUTF(sender);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}