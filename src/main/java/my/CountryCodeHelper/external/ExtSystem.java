package my.CountryCodeHelper.external;

import my.CountryCodeHelper.exception.DownloadingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class ExtSystem {
    private static final Logger logger = LoggerFactory.getLogger(ExtSystem.class);
    protected URL url;

    private boolean systemAvailable;
    private InputStream data;

    protected void createSystem() {
        try {
            checkSystemAvailability();
            setData();
        } catch (IOException e) {
            throw new DownloadingException(e);
        }
    }

    public boolean isSystemAvailable() {
        return systemAvailable;
    }

    private void checkSystemAvailability() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.connect();
            if (connection.getResponseCode() != 200) {
                logger.debug("System " + url + " unavailable");
                systemAvailable = false;
            } else {
                systemAvailable = true;
            }
        } catch (IOException e) {
            logger.error("Failed to check system availability with URL " + url.toString());
            systemAvailable = false;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    private void setData() throws IOException {
        data = url.openStream();
        if (data == null)
            logger.debug("Data from " + url + " is empty");
    }

    public InputStream getData() {
        return data;
    }

    protected abstract void setUrl(String url) throws MalformedURLException;
}
