package my.CountryCodeHelper.repo.proxy;

import my.CountryCodeHelper.model.PhoneCode;
import my.CountryCodeHelper.repo.repo.PhoneCodeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class PhoneCodeRepoProxy {
    private static final Logger logger = LoggerFactory.getLogger(PhoneCodeRepoProxy.class);
    private final PhoneCodeRepo phoneCodeRepo;

    @Autowired
    public PhoneCodeRepoProxy(PhoneCodeRepo phoneCodeRepo) {
        this.phoneCodeRepo = phoneCodeRepo;
    }

    public Map<String, PhoneCode> getByCountryCodeIn(Set<String> countryCodes) {
        Set<PhoneCode> phoneCodes = phoneCodeRepo.getByCountryCodeIn(countryCodes);
        if (phoneCodes == null) {
            logger.info("... Phones not found");
            return new HashMap<>();
        } else {
            Map<String, PhoneCode> result = new HashMap<>();
            phoneCodes.forEach((phoneCode -> result.put(phoneCode.getCountryCode(), phoneCode)));
            return result;
        }
    }

    public PhoneCode getByCountryCode(String countryCode) {
        logger.info("... Getting phone from database with country code " + countryCode);
        PhoneCode phoneCode = phoneCodeRepo.getByCountryCode(countryCode);
        if (phoneCode == null) {
            logger.info("... Phone not found");
        }
        return phoneCode;
    }

    public void save(PhoneCode phoneCode) {
        logger.info("... Saving phone in database: " + phoneCode.toString());
        phoneCodeRepo.saveAndFlush(phoneCode);
    }

    public void flush() {
        phoneCodeRepo.flush();
    }

}
