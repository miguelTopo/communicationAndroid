package co.edu.udistrital.communicationapp.rest;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import co.edu.udistrital.communicationapp.application.ApplicationProperties;
import co.edu.udistrital.communicationapp.util.PropertyKey;

public class ApplicationService {

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

    private static HttpURLConnection buildUrlConnection(String requestPath, String method, Map<String, String> queryParamList, byte[] postData) {
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
            urlConnection.setRequestMethod(method);

            if (postData != null) {
                urlConnection.setInstanceFollowRedirects(false);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("charset", "utf-8");
                urlConnection.setRequestProperty("Content-Length", Integer.toString(postData.length));
                urlConnection.setUseCaches(false);
                try (DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream())) {
                    wr.write(postData);
                }
            }
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

    protected static String executePost(String requestPath, byte[] postData) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = buildUrlConnection(requestPath, POST, null, postData);
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

    protected static String executePost(String requestPath, Map<String, String> queryParamList, byte[] postData) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = buildUrlConnection(requestPath, POST, queryParamList, postData);
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
            urlConnection = buildUrlConnection(requestPath, POST, queryParamList, null);
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
