package my.CountryCodeHelper.service.download;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.external.ExtResponse;
import org.springframework.dao.DataAccessResourceFailureException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public abstract class DataDownloadService {

    public void execute() throws DownloadingException, DataAccessResourceFailureException {
        updateData(downloadData());
    }

    public abstract ExtResponse downloadData() throws DownloadingException;

    public abstract void updateData(ExtResponse response) throws DownloadingException, DataAccessResourceFailureException;

    public Map<String, String> parseResponseToMap(ExtResponse response) throws DownloadingException {
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
