package co.edu.udistrital.communicationapp.application;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {

    private Context context;
    private Properties properties;

    public ApplicationProperties() {
        this.context = CommunicationApplication.getAppContext();
    }

    private Properties getProperties(String fileName) {
        Properties currentPropeties = new Properties();
        try {
            //Creación de manejador AssetManager para obtener recursos de propeties
            AssetManager assetManager = context.getAssets();
            //Se abre el inputStream del archivo solicitado para identificar si el archivo existe
            InputStream inputStream = assetManager.open(fileName);
            //Se leen las propiedades y se almacena en la variable properties
            currentPropeties.load(inputStream);
        } catch (Exception e) {
            Log.e("ApplicationProperties", e.toString());
        }
        return currentPropeties;
    }

    public String getPropertyByKey(String key) {
        if (key == null || key.trim().isEmpty())
            return "";
        if (this.properties == null || this.properties.isEmpty())
            this.properties = getProperties("properties.properties");
        return this.properties.getProperty(key);
    }
}
