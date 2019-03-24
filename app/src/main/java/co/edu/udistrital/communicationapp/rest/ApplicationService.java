package co.edu.udistrital.communicationapp.rest;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import co.edu.udistrital.communicationapp.application.ApplicationProperties;
import co.edu.udistrital.communicationapp.util.FileUtils;
import co.edu.udistrital.communicationapp.util.PropertyKey;

public class ApplicationService {

    private static final String TAG = ApplicationService.class.getSimpleName();

    private static final String CLASS_TAG = ApplicationService.class.getSimpleName();
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static String restCommunicationHost;
    private static ApplicationProperties applicationProperties;


    private static String executeRest(String baseUrl, String method) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJsonString = null;
        try {
            Uri buildUri = Uri.parse(baseUrl).buildUpon()
                    .build();

            URL requestUrl = new URL(buildUri.toString());

            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setRequestMethod(method);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null)
                //Nothing to do
                return null;

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                /*Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                but if does make debuggin a *lot* easier if you print out the completed buffer for debugging*/
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0)
                //Stream was empty. No point in parsing
                return null;

            bookJsonString = buffer.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d(CLASS_TAG, bookJsonString);
            return bookJsonString;
        }
    }

    private static HttpURLConnection addPostJsonData(HttpURLConnection urlConnection, String jsonData) {
        if (urlConnection == null || jsonData == null || jsonData.trim().isEmpty())
            return null;
        try {
            if (jsonData != null && !jsonData.trim().isEmpty()) {
                urlConnection.setDoInput(true);
                //urlConnection.setDoOutput(true);
                urlConnection.setInstanceFollowRedirects(false);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("charset", "utf-8");

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonData);
                writer.flush();
                writer.close();
                os.close();
            }
            return urlConnection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection addMultipartData(HttpURLConnection urlConnection, File file) {
        if (urlConnection == null || file == null)
            return null;
        String boundary = "*****";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        try {
            //urlConnection.setDoInput(true);
            //urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + file.getName() + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            Log.i(TAG, "Headers are written");

            FileInputStream fis = new FileInputStream(file);

            int bytesAvailable = fis.available();

            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            int bytesRead = fis.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fis.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fis.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            fis.close();
            dos.flush();

            return urlConnection;
        } catch (MalformedURLException ex) {
            Log.e(TAG, "URL error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            Log.e(TAG, "IO error: " + ioe.getMessage(), ioe);
        }
        return null;
    }

    private static HttpURLConnection buildUrlConnection(String requestPath, String method, Map<String, String> queryParamList, String jsonData, File file) {
        HttpURLConnection urlConnection = null;
        try {
            Uri.Builder urlBuilder = Uri.parse(getRestCommunicationHost() + requestPath).buildUpon();
            //For each para ingresar los queryparameter si existen
            if (queryParamList != null && !queryParamList.isEmpty()) {
                for (Map.Entry<String, String> entry : queryParamList.entrySet())
                    urlBuilder.appendQueryParameter(entry.getKey(), entry.getValue().replace("+", ""));
            }
            Uri buildUri = urlBuilder.build();
            URL requestUrl = new URL(buildUri.toString());
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setDoOutput(true);
            if (jsonData != null && !jsonData.trim().isEmpty())
                urlConnection = addPostJsonData(urlConnection, jsonData);
            if (file != null && file.exists() && file.canRead())
                urlConnection = addMultipartData(urlConnection, file);

            return urlConnection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String processResponse(HttpURLConnection urlConnection) {
        BufferedReader reader = null;
        String jsonString = null;
        try {
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null)
                //Nothing to do
                return null;

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                /*Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                but if does make debuggin a *lot* easier if you print out the completed buffer for debugging*/
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0)
                //Stream was empty. No point in parsing
                return null;
            jsonString = buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d(CLASS_TAG, jsonString);
            return jsonString;
        }
    }

    protected static String executePost(String requestPath, String jsonData) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = buildUrlConnection(requestPath, POST, null, jsonData, null);
            urlConnection.connect();
            return processResponse(urlConnection);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();


        }
    }

    private static String delimiter = "--";
    private static final String boundary = "SwA" + Long.toString(System.currentTimeMillis()) + "SwA";
    private static OutputStream os = null;

    private static void writeParamData(String paramName, String value) {
        try {
            os.write((delimiter + boundary + "\r\n").getBytes());
            os.write("Content-Type: text/plain\r\n".getBytes());
            os.write(("Content-Disposition: form-data; name=\"" + paramName + "\"\r\n").getBytes());
            ;
            os.write(("\r\n" + value + "\r\n").getBytes());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static void addFormPart(String paramName, String value) {
        writeParamData(paramName, value);
    }

    private static void addFilePart(String paramName, String fileName, byte[] data) throws Exception {
        os.write((delimiter + boundary + "\r\n").getBytes());
        os.write(("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + fileName + "\"\r\n").getBytes());
        os.write(("Content-Type: application/octet-stream\r\n").getBytes());
        os.write(("Content-Transfer-Encoding: binary\r\n").getBytes());
        os.write("\r\n".getBytes());
        os.write(data);
        os.write("\r\n".getBytes());
    }

    private static void finishMultipart() throws Exception {
        os.write((delimiter + boundary + delimiter + "\r\n").getBytes());
    }

    protected static String executeMultipartTest(String requestPath, File file, String jsonData) {
        try {
            HttpURLConnection urlConnection = null;

            Uri.Builder urlBuilder = Uri.parse(getRestCommunicationHost() + requestPath).buildUpon();
            Uri buildUri = urlBuilder.build();
            URL requestUrl = new URL(buildUri.toString());
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setRequestMethod(POST);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            urlConnection.connect();
            os = urlConnection.getOutputStream();
            addFormPart("jsonData", jsonData);
            addFilePart("file", file.getName(), FileUtils.fileToByteArray(file));
            finishMultipart();
            return processResponse(urlConnection);
        } catch (MalformedURLException ex) {
            Log.e(TAG, "URL error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            Log.e(TAG, "IO error: " + ioe.getMessage(), ioe);
        } catch (Exception e) {
            Log.e(TAG, "IO error: " + e.getMessage(), e);
        }
        return "";
    }

    protected static String executeMultipartPost2(String requestPath, File file) {
        String boundary = "*****";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        try {
            HttpURLConnection urlConnection = null;

            Uri.Builder urlBuilder = Uri.parse(getRestCommunicationHost() + requestPath).buildUpon();
            Uri buildUri = urlBuilder.build();
            URL requestUrl = new URL(buildUri.toString());
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);

            urlConnection.setRequestMethod("POST");

            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + file.getName() + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            Log.i(TAG, "Headers are written");

            FileInputStream fis = new FileInputStream(file);

            int bytesAvailable = fis.available();

            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            int bytesRead = fis.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fis.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fis.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            fis.close();
            dos.flush();

            Log.e(TAG, "File Sent, Response: " + String.valueOf(urlConnection.getResponseCode()));

            InputStream is = urlConnection.getInputStream();

            // retrieve the response from server
            int ch;

            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            String s = b.toString();
            Log.i("Response", s);
            dos.close();
            return "";
        } catch (MalformedURLException ex) {
            Log.e(TAG, "URL error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            Log.e(TAG, "IO error: " + ioe.getMessage(), ioe);
        }
        return "";
    }

    protected static String executePost(String requestPath, Map<String, String> queryParamList, String jsonData) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = buildUrlConnection(requestPath, POST, queryParamList, null, null);
            urlConnection.connect();
            return processResponse(urlConnection);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    protected static String executePost(String requestPath, Map<String, String> queryParamList) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = buildUrlConnection(requestPath, POST, queryParamList, null, null);
            urlConnection.connect();
            return processResponse(urlConnection);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    protected static String executeGet(String requestPath) {
        return executeRest(getRestCommunicationHost() + requestPath, GET);
    }

    private static ApplicationProperties getApplicationProperties() {
        if (applicationProperties == null)
            applicationProperties = new ApplicationProperties();
        return applicationProperties;
    }

    private static String getRestCommunicationHost() {
        if (restCommunicationHost == null || restCommunicationHost.trim().isEmpty())
            restCommunicationHost = getApplicationProperties().getPropertyByKey(PropertyKey.REST_COMMUNICATION_HOST);
        return restCommunicationHost;
    }


}
