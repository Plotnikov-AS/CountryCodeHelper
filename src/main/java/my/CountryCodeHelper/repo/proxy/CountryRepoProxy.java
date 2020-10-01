package my.CountryCodeHelper.repo.proxy;

import my.CountryCodeHelper.model.Country;
import my.CountryCodeHelper.repo.repo.CountryRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@CacheConfig(cacheNames = "countries")
public class CountryRepoProxy {
    private final static Logger logger = LoggerFactory.getLogger(CountryRepoProxy.class);
    private final CountryRepo countryRepo;
    private final CacheManager cacheManager;

    @Autowired
    public CountryRepoProxy(CountryRepo countryRepo, CacheManager cacheManager) {
        this.countryRepo = countryRepo;
        this.cacheManager = cacheManager;
    }

    @Cacheable
    public Map<String, Country> getByCountryCodeIn(Set<String> countryCodes) {
        Set<Country> countries = countryRepo.getByCountryCodeIn(countryCodes);
        if (countries == null) {
            logger.info("... Country not found");
            return null;
        } else {
            Map<String, Country> result = new HashMap<>();
            countries.forEach((country -> result.put(country.getCountryCode(), country)));
            return result;
        }
    }


    @Cacheable
    public Country getByCountryCode(String countryCode) {
        logger.info("... Getting country from database with country code " + countryCode);
        Country country = countryRepo.getByCountryCode(countryCode);
        if (country == null) {
            logger.info("... Country not found");
        } else {
            logger.info("... Getted country: " + country.toString());
        }
        return country;
    }

    @Cacheable
    public Set<Country> findByCountryNameContainingIgnoreCase(String countryName) {
        logger.info("... Getting set of countries from database by name, containing " + countryName);
        Set<Country> countries = countryRepo.findByCountryNameContainingIgnoreCase(countryName);
        if (countries == null) {
            logger.info("... Countries not found");
        } else {
            logger.info("... Getted countries: " + countries);
        }
        return countries;
    }

    public void save(Country country) {
        logger.info("... Saving country: " + country.toString());
        countryRepo.save(country);
    }

    @Scheduled(cron = "0 0/30 * * * ?")
    public void clearCacheSchedule() {
        try {
            cacheManager.getCache("countries").clear();
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
        }
    }
}
