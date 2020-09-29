package my.CountryCodeHelper.repo.proxy;

import my.CountryCodeHelper.model.Country;
import my.CountryCodeHelper.repo.repo.CountryRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@CacheConfig(cacheNames = "countries")
public class CountryRepoProxy {
    private final static Logger logger = LoggerFactory.getLogger(CountryRepoProxy.class);
    private final CountryRepo countryRepo;

    public CountryRepoProxy(CountryRepo countryRepo) {
        this.countryRepo = countryRepo;
    }


    @Cacheable
    public Country getByCountryCode(String countryCode) {
        logger.info("Getting entity from database with country code " + countryCode);
        return countryRepo.getByCountryCode(countryCode);
    }

    @Cacheable
    public Set<Country> findByCountryNameContainingIgnoreCase(String countryName) {
        logger.info("Getting set of entities from database by name, containing " + countryName);
        return countryRepo.findByCountryNameContainingIgnoreCase(countryName);
    }

    public void save(Country country) {
        logger.info("Saving country: " + country.toString());
        countryRepo.save(country);
    }
}
