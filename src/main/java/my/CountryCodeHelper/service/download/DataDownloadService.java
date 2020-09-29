package my.CountryCodeHelper.service.download;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.external.ExtResponse;

import java.io.InputStreamReader;
import java.util.Map;

public abstract class DataDownloadService {

    public void execute() {
        updateData(downloadData());
    }

    public abstract ExtResponse downloadData() throws DownloadingException;

    public abstract void updateData(ExtResponse response) throws DownloadingException;

    public Map<String, String> parseResponseToMap(ExtResponse response) {
        Gson gson = new Gson();
        return gson.fromJson(new InputStreamReader(response.getReceivedData()), new TypeToken<Map<String, String>>() {
        }.getType());
    }

}
