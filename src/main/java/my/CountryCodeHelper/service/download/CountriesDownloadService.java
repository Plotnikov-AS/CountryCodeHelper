package my.CountryCodeHelper.service.download;

import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.external.ExtMethods;
import my.CountryCodeHelper.external.ExtRequest;
import my.CountryCodeHelper.external.ExtResponse;
import my.CountryCodeHelper.external.country.CountryIO;
import my.CountryCodeHelper.external.country.CountryIOExec;
import my.CountryCodeHelper.model.Country;
import my.CountryCodeHelper.model.PhoneCode;
import my.CountryCodeHelper.repo.proxy.CountryRepoProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Map;

@Service
public class CountriesDownloadService extends DataDownloadService {
    private static final Logger logger = LoggerFactory.getLogger(CountriesDownloadService.class);
    private final CountryRepoProxy countryRepo;
    private static Long updatedTime = 0L;
    private static boolean refreshRunning = false;

    @Autowired
    public CountriesDownloadService(CountryRepoProxy countryRepo) {
        this.countryRepo = countryRepo;
    }


    @Override
    public synchronized ExtResponse downloadData() throws DownloadingException {
        try {
            logger.info("... Downloading countries from country.io");
            setIsRefreshRunning(true);
            ExtRequest request = new ExtRequest.Builder()
                    .setExtSystem(new CountryIO())
                    .setMethod(ExtMethods.GET_COUNTRY_NAMES)
                    .build();
            CountryIOExec exec = new CountryIOExec(request);
            return exec.execute();
        } finally {
            setIsRefreshRunning(false);
        }
    }

    @Override
    public void updateData(ExtResponse response) throws DownloadingException, DataAccessResourceFailureException {
        switch (response.getErrorCode()) {
            case ERROR_CODE_OK:
                Map<String, String> codes2countries = parseResponseToMap(response);
                logger.info("... " + codes2countries.size() + " countries downloaded ");
                logger.info("... Updating data in database");
                codes2countries.forEach((countryCode, countryName) -> {
                    Country existingCountry = countryRepo.getByCountryCode(countryCode);
                    PhoneCode phoneCode = existingCountry == null ? new PhoneCode() : existingCountry.getPhoneCode();
                    phoneCode.setCountryCode(countryCode);
                    Country country = existingCountry == null ? new Country() : existingCountry;
                    country.setCountryCode(countryCode);
                    country.setCountryName(countryName);
                    country.setUpdTime(new Date(System.currentTimeMillis()));
                    country.setPhoneCode(phoneCode);
                    countryRepo.save(country);
                });
                updatedTime = System.currentTimeMillis();
                break;
            case ERROR_CODE_FAILURE:
                throw new DownloadingException("External system unavailable");
        }
    }

    public synchronized static Long getUpdatedTime() {
        return updatedTime;
    }

    public static synchronized void setIsRefreshRunning(boolean isRefreshRunning) {
        CountriesDownloadService.refreshRunning = isRefreshRunning;
    }

    public static synchronized boolean isRefreshRunning() {
        return CountriesDownloadService.refreshRunning;
    }
}
