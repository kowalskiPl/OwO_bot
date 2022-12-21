package src.utilities;

import javax.naming.ConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    public static Properties readConfig(InputStream inputStream) throws ConfigurationException {
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (properties.isEmpty()) {
            throw new ConfigurationException("Config failed to load");
        }
        return properties;
    }
}
