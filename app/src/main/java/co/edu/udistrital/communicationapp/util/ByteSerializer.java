package co.edu.udistrital.communicationapp.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ByteSerializer {

    public static byte[] parseToByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
        } catch (IOException ie) {
            return null;
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return bytes;
    }

    @Nullable
    public static byte[] parseToByteArray(@NonNull File file) {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(file));
            buffer.read(bytes, 0, bytes.length);
            buffer.close();
            return bytes;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }


}
