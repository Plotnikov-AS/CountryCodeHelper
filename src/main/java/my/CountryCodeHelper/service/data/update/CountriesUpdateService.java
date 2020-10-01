package my.CountryCodeHelper.service.data.update;

import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.model.Country;
import my.CountryCodeHelper.model.PhoneCode;
import my.CountryCodeHelper.repo.proxy.CountryRepoProxy;
import my.CountryCodeHelper.service.data.ResponseParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Service
public class CountriesUpdateService extends DataUpdateService {
    private final static Logger logger = LoggerFactory.getLogger(CountriesUpdateService.class);
    private final CountryRepoProxy countryRepo;

    @Autowired
    public CountriesUpdateService(CountryRepoProxy countryRepo) {
        this.countryRepo = countryRepo;
    }

    @Override
    protected synchronized void update() throws DownloadingException {
        switch (response.getErrorCode()) {
            case ERROR_CODE_OK:
                Map<String, String> codes2countries = ResponseParser.parseToMap(response);
                logger.info("... Updating countries in database");
                Set<String> codes = new TreeSet<>();
                codes2countries.forEach((key, value) -> codes.add(key));
                Map<String, Country> countriesFromDB = countryRepo.getByCountryCodeIn(codes);
                for (Map.Entry<String, String> entry : codes2countries.entrySet()) {
                    Country existingCountry = countriesFromDB.get(entry.getKey());
                    PhoneCode phoneCode = existingCountry == null ? new PhoneCode() : existingCountry.getPhoneCode();
                    phoneCode.setCountryCode(entry.getKey());
                    Country country = existingCountry == null ? new Country() : existingCountry;
                    country.setCountryCode(entry.getKey());
                    country.setCountryName(entry.getValue());
                    country.setPhoneCode(phoneCode);
                    if (country.equals(existingCountry))
                        continue;
                    countryRepo.save(country);
                }
                break;
            case ERROR_CODE_FAILURE:
                throw new DownloadingException("External system unavailable");
        }
    }
}
