package my.CountryCodeHelper.service.download;

import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.external.ExtMethods;
import my.CountryCodeHelper.external.ExtRequest;
import my.CountryCodeHelper.external.ExtResponse;
import my.CountryCodeHelper.external.country.CountryIO;
import my.CountryCodeHelper.external.country.CountryIOExec;
import my.CountryCodeHelper.model.PhoneCode;
import my.CountryCodeHelper.repo.proxy.PhoneCodeRepoProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PhoneCodesDownloadService extends DataDownloadService {
    private static final Logger logger = LoggerFactory.getLogger(PhoneCodesDownloadService.class);
    private final PhoneCodeRepoProxy phoneCodeRepo;
    private static Long updatedTime = 0L;
    private static boolean refreshRunning = false;

    @Autowired
    public PhoneCodesDownloadService(PhoneCodeRepoProxy phoneCodeRepo) {
        this.phoneCodeRepo = phoneCodeRepo;
    }

    @Override
    public synchronized ExtResponse downloadData() throws DownloadingException {
        try {
            logger.info("... Downloading phones from country.io");
            setIsRefreshRunning(true);
            while (CountriesDownloadService.isRefreshRunning()) {
                try {
                    wait(250);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
            }
            ExtRequest request = new ExtRequest.Builder()
                    .setExtSystem(new CountryIO())
                    .setMethod(ExtMethods.GET_PHONE_CODES)
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
                Map<String, String> phones2countries = parseResponseToMap(response);
                logger.info("... " + phones2countries.size() + " phones downloaded");
                logger.info("... Updating data in database");
                phones2countries.forEach((countryCode, phoneCode) -> {
                    PhoneCode phone = phoneCodeRepo.getByCountryCode(countryCode);
                    if (phone != null) {
                        phone.setPhoneCode(phoneCode);
                        phoneCodeRepo.save(phone);
                    } else {
                        logger.debug("Object with code " + countryCode + " not exist in table_phone_code");
                    }
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
        PhoneCodesDownloadService.refreshRunning = isRefreshRunning;
    }

    public static synchronized boolean isRefreshRunning() {
        return PhoneCodesDownloadService.refreshRunning;
    }
}
