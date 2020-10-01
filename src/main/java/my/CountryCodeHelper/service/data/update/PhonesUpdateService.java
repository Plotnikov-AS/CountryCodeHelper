package my.CountryCodeHelper.service.data.update;

import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.model.PhoneCode;
import my.CountryCodeHelper.repo.proxy.PhoneCodeRepoProxy;
import my.CountryCodeHelper.service.data.ResponseParser;
import my.CountryCodeHelper.service.data.refresh.Refresher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Service
public class PhonesUpdateService extends DataUpdateService {
    private final static Logger logger = LoggerFactory.getLogger(PhonesUpdateService.class);
    private final PhoneCodeRepoProxy phoneCodeRepo;

    @Autowired
    public PhonesUpdateService(PhoneCodeRepoProxy phoneCodeRepo) {
        this.phoneCodeRepo = phoneCodeRepo;
    }

    @Override
    protected synchronized void update() throws DownloadingException, DataAccessResourceFailureException {
        switch (response.getErrorCode()) {
            case ERROR_CODE_OK:
                Map<String, String> phones2countries = ResponseParser.parseToMap(response);
                if (phones2countries == null)
                    throw new DownloadingException();
                logger.info("... Check if countries updating not finished");
                waitUntilCountriesUpdatingNotFinished();
                logger.info("... Updating phones in database");
                Set<String> codes = new TreeSet<>();
                phones2countries.forEach((key, value) -> codes.add(key));
                Map<String, PhoneCode> phoneCodesFromDB = phoneCodeRepo.getByCountryCodeIn(codes);
                for (Map.Entry<String, String> entry : phones2countries.entrySet()) {
                    PhoneCode phone = phoneCodesFromDB.get(entry.getKey());
                    if (phone == null || phone.getPhoneCode().equalsIgnoreCase(entry.getValue()))
                        continue;
                    phone.setPhoneCode(entry.getValue());
                    phoneCodeRepo.save(phone);
                }
                break;
            case ERROR_CODE_FAILURE:
                throw new DownloadingException("External system unavailable");
        }
    }

    private synchronized void waitUntilCountriesUpdatingNotFinished() {
        while (Refresher.isCountriesRefreshRunning()) {
            try {
                logger.info("... Countries updating now. Waiting");
                wait(100);
            } catch (InterruptedException e) {
                throw new DownloadingException(e);
            }
        }
    }
}
