package my.CountryCodeHelper.external.country;

import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.external.ExtSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class CountryIO extends ExtSystem {
    private static Logger logger = LoggerFactory.getLogger(CountryIO.class);

    private String COUNTRY_NAMES_URL = "http://country.io/names.json";
    private String PHONES_URL = "http://country.io/phone.json";

    @Override
    protected void setUrl(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    public void getCountryNames() throws DownloadingException {
        try {
            setUrl(COUNTRY_NAMES_URL);
            createSystem();
        } catch (MalformedURLException e) {
            throw new DownloadingException("Wrong URL");
        }
    }

    public void getPhones() throws DownloadingException {
        try {
            setUrl(PHONES_URL);
            createSystem();
        } catch (MalformedURLException e) {
            throw new DownloadingException("Wrong URL");
        }
    }
}
