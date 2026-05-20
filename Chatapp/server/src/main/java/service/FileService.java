package service;

import java.io.File;
import java.io.FileOutputStream;

public class FileService {

    public static File save(String name, byte[] data) {

        try {

            File dir = new File("recv_files");

            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = uniqueFile(dir, name);

            FileOutputStream fos = new FileOutputStream(file);

            fos.write(data);
            fos.close();

            return file;

         } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static File uniqueFile(File dir, String name) {
        File file = new File(dir, name);

        if (!file.exists()) {
            return file;
        }

        String baseName = name;
        String extension = "";
        int dot = name.lastIndexOf('.');

        if (dot > 0) {
            baseName = name.substring(0, dot);
            extension = name.substring(dot);
        }

        int index = 1;
        while (file.exists()) {
            file = new File(dir, baseName + "_" + index + extension);
            index++;
        }

        return file;
    }
}