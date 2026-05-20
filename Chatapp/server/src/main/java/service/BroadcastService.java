package service;

import java.io.DataOutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BroadcastService {
//all active client connections (output channels)
    private static final List<DataOutputStream> clients =
            new CopyOnWriteArrayList<>();

    public static void addClient(DataOutputStream out) {
        clients.add(out);
    }

    public static void removeClient(DataOutputStream out) {
        clients.remove(out);
    }

    public static void broadcastText(String sender,
                                     String message,
                                     DataOutputStream origin) {

        for (DataOutputStream client : clients) {

            if (client == origin) continue;
                        try {

                synchronized (client) {

                    client.writeUTF("TEXT");
                    client.writeUTF(sender);
                    client.writeUTF(message);
                    client.flush();
                }

            } catch (Exception e) {
                removeClient(client);
            }
        }
    }

    public static void broadcastFile(String sender,
                                     String mimeType,
                                     String fileName,
                                     byte[] data,
                                     DataOutputStream origin) {

        for (DataOutputStream client : clients) {

            if (client == origin) continue;

            try {

                synchronized (client) {
                    System.out.println("[BroadcastService] sending FILE to client (sender=" + sender + ", file=" + fileName + ", mime=" + mimeType + ")");

                    client.writeUTF("FILE");
                    client.writeUTF(sender);
                    client.writeUTF(fileName);
                    client.writeUTF(mimeType);
                    client.writeLong(data.length);
                    client.write(data);
                    client.flush();
                }

            } catch (Exception e) {
                removeClient(client);
            }
        }
    }
}