package co.edu.udistrital.communicationapp.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.UUID;

import co.edu.udistrital.communicationapp.application.CommunicationApplication;

public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();


    public static String getPath(Context context, Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        return cursor.getString(columnIndex);

    }

    private static File[] getPrimaryExternalStorage(String environmentDirType) {
        return CommunicationApplication.getAppContext().getExternalFilesDirs(environmentDirType);
    }

    public static File getAlarmsStorage() {
        return getPrimaryExternalStorage(Environment.DIRECTORY_ALARMS)[0];
    }

    public static File getDCIMStorage() {
        return getPrimaryExternalStorage(Environment.DIRECTORY_DCIM)[0];
    }

    public static File getDocumentsStorage() {
        return getPrimaryExternalStorage(Environment.DIRECTORY_DOCUMENTS)[0];
    }

    public static File getDownloadsStorage() {
        return getPrimaryExternalStorage(Environment.DIRECTORY_DOWNLOADS)[0];
    }

    public static File getMoviesStorage() {
        return getPrimaryExternalStorage(Environment.DIRECTORY_MOVIES)[0];
    }

    public static File getNotificationsStorage() {
        return getPrimaryExternalStorage(Environment.DIRECTORY_NOTIFICATIONS)[0];
    }

    public static File getPicturesStorage() {
        return getPrimaryExternalStorage(Environment.DIRECTORY_PICTURES)[0];
    }

    public static File getPodcastsStorage() {
        return getPrimaryExternalStorage(Environment.DIRECTORY_PODCASTS)[0];
    }

    public static File getRingtonesStorage() {
        return getPrimaryExternalStorage(Environment.DIRECTORY_RINGTONES)[0];
    }

    public static File getMusicStorage() {
        return getPrimaryExternalStorage(Environment.DIRECTORY_MUSIC)[0];
    }

    public static boolean delete(String fileName) {
        boolean delete = CommunicationApplication.getAppContext().deleteFile(fileName);
        if (!delete)
            Log.w(TAG, "El archivo no se eliminó del app. Verifique la ruta y la existencia del archivo");
        return delete;
    }

    private static File getPrivateAlbumStorageDir(String albumName, String environmentDirType) {
        File file = new File(CommunicationApplication.getAppContext().getExternalFilesDir(
                environmentDirType), albumName);
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        return file;
    }

    public static File getPicturePrivateAlbum() {
        return getPrivateAlbumStorageDir("", Environment.DIRECTORY_PICTURES);
    }

    public static String getFileName(String extension) {
        Calendar cal = DateUtil.getCurrentCalendar();
        return cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + UUID.randomUUID().toString().substring(0, 10) + extension;
    }

    public static byte[] fileToByteArray(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }


    public static File getFileFromURI(Context context, Uri uri, String extension) {
        try {
            File directory = FileUtils.getPicturePrivateAlbum();
            InputStream stream = context.getContentResolver().openInputStream(uri);
            File f = new File(directory, FileUtils.getFileName(".jpg"));
            OutputStream out = new FileOutputStream(f);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = stream.read(buffer)) > 0)
                out.write(buffer, 0, length);
            out.close();
            stream.close();
            if (f.exists())
                Log.i(TAG, "El archivo fue creado exitosamente");
            else
                Log.e(TAG, "Ocurrió un error al intentar almacenar el archivo");
            return f;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

}
