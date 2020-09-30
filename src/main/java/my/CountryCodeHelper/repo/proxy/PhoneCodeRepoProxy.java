package my.CountryCodeHelper.repo.proxy;

import my.CountryCodeHelper.model.PhoneCode;
import my.CountryCodeHelper.repo.repo.PhoneCodeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

@Component
@CacheConfig(cacheNames = "countries")
public class PhoneCodeRepoProxy {
    private static final Logger logger = LoggerFactory.getLogger(PhoneCodeRepoProxy.class);
    private final PhoneCodeRepo phoneCodeRepo;

    @Autowired
    public PhoneCodeRepoProxy(PhoneCodeRepo phoneCodeRepo) {
        this.phoneCodeRepo = phoneCodeRepo;
    }

    @CachePut
    public PhoneCode getByCountryCode(String countryCode) {
        logger.info("... Getting entity from DB with country code " + countryCode);
        return phoneCodeRepo.getByCountryCode(countryCode);
    }

    public void save(PhoneCode phoneCode) {
        logger.info("... Saving entity in DB: " + phoneCode.toString());
        phoneCodeRepo.save(phoneCode);
    }

}
