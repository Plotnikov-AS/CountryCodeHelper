package my.CountryCodeHelper.service.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.external.ExtResponse;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public abstract class ResponseParser {
    public static Map<String, String> parseToMap(ExtResponse response) throws DownloadingException {
        try {
            InputStream receivedData = response.getReceivedData();
            if (receivedData == null || receivedData.available() <= 0) {
                throw new DownloadingException("Received data is empty");
            }
            Gson gson = new Gson();
            return gson.fromJson(new InputStreamReader(receivedData), new TypeToken<Map<String, String>>() {
            }.getType());
        } catch (Exception e) {
            throw new DownloadingException(e);
        }
    }
}
