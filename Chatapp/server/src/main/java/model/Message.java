package model;

public class Message {

    private String sender;
    private String type;
    private String message;
    private String fileName;
    private byte[] fileData;

    public Message(String sender,
                   String type,
                   String message,
                   String fileName,
                   byte[] fileData) {

        this.sender = sender;
        this.type = type;
        this.message = message;
        this.fileName = fileName;
        this.fileData = fileData;
        }

    public String getSender() {
        return sender;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }
}