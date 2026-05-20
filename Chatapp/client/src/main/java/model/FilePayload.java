package model;

import java.io.File;

public class FilePayload {

    private final File file;
    private final String fileName;
    private final String mimeType;
    private final byte[] data;

    public FilePayload(File file, String fileName, String mimeType, byte[] data) {
        this.file = file;
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.data = data;
    }

    public File getFile() {
        return file;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public byte[] getData() {
        return data;
    }
}