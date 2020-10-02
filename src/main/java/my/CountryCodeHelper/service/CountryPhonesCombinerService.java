package my.CountryCodeHelper.service;

import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.model.Country;
import my.CountryCodeHelper.model.PhoneCode;
import my.CountryCodeHelper.repo.proxy.CountryRepoProxy;
import my.CountryCodeHelper.repo.proxy.PhoneCodeRepoProxy;
import my.CountryCodeHelper.service.data.ResponseParser;
import my.CountryCodeHelper.service.data.download.CountriesDownloadService;
import my.CountryCodeHelper.service.data.download.PhoneCodesDownloadService;
import my.CountryCodeHelper.service.data.refresh.Refresher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CountryPhonesCombinerService {
    private static final Logger logger = LoggerFactory.getLogger(CountryPhonesCombinerService.class);
    final CountryRepoProxy countryRepo;
    final PhoneCodeRepoProxy phoneCodeRepo;
    final CountriesDownloadService countriesDownloadService;
    final PhoneCodesDownloadService phoneCodesDownloadService;

    @Autowired
    public CountryPhonesCombinerService(CountryRepoProxy countryRepo, PhoneCodeRepoProxy phoneCodeRepo, CountriesDownloadService countriesDownloadService, PhoneCodesDownloadService phoneCodesDownloadService) {
        this.countryRepo = countryRepo;
        this.phoneCodeRepo = phoneCodeRepo;
        this.countriesDownloadService = countriesDownloadService;
        this.phoneCodesDownloadService = phoneCodesDownloadService;
    }

    public synchronized List<Map<String, String>> getCombinedCountryAndPhone(String countryName) {
        try {
            logger.info("... Start combining country name and phone code");
            List<Map<String, String>> country2phonesList = new ArrayList<>();
            Set<Country> countries = countryRepo.findByCountryNameContainingIgnoreCase(countryName);
            if (countries.isEmpty() && !Refresher.isCountriesRefreshRunning()) {
                logger.info("... No countries in database. Start refreshing database from country.io");
                Refresher.refresh(countriesDownloadService);
                waitUntilServiceNotFinished();
            } else if (countries.isEmpty() && Refresher.isCountriesRefreshRunning()) {
                waitUntilServiceNotFinished();
            }
            country2phonesList = getMapOfCountries(countryName);
            Set<String> codes = new HashSet<>();
            country2phonesList.forEach((map -> codes.add(map.get("countryCode"))));
            Map<String, PhoneCode> phones = phoneCodeRepo.getByCountryCodeIn(codes);
            country2phonesList.forEach(map -> {
                PhoneCode phoneCode = phones.get(map.get("countryCode"));
                map.put("phoneCode", phoneCode == null || phoneCode.getPhoneCode() == null ? "" : phoneCode.getPhoneCode());
            });
            return country2phonesList;
        } catch (DataAccessResourceFailureException e) {
            logger.warn("Cant reach database.");
            return getCombinedCountryAndPhoneDirectlyFromExtSystem(countryName);
        }
    }

    private List<Map<String, String>> getMapOfCountries(String countryName) {
        List<Map<String, String>> country2phonesList = new ArrayList<>();
        Set<Country> countries = countryRepo.findByCountryNameContainingIgnoreCase(countryName);
        countries.forEach(country -> {
            Map<String, String> country2phones = new HashMap<>();
            country2phones.put("countryCode", country.getCountryCode());
            country2phones.put("countryName", country.getCountryName());
            PhoneCode phoneCode = country.getPhoneCode();
            if (phoneCode == null || phoneCode.getPhoneCode() == null && !Refresher.isPhonesRefreshRunning()) {
                logger.info("... Country with empty phone code. Start refreshing database from country.io");
                Refresher.refresh(phoneCodesDownloadService);
            }
            country2phonesList.add(country2phones);
        });
        return country2phonesList;
    }

    private synchronized void waitUntilServiceNotFinished() {
        while (Refresher.isCountriesRefreshRunning() || Refresher.isPhonesRefreshRunning()) {
            try {
                logger.info("... Refresh running. Waiting");
                wait(100);
            } catch (InterruptedException e) {
                throw new DownloadingException(e);
            }
        }
    }

    public synchronized List<Map<String, String>> getCombinedCountryAndPhoneDirectlyFromExtSystem(String countryName) throws DownloadingException {
        logger.info("... Getting info directly from ext system");
        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> codes2countries = ResponseParser.parseToMap(countriesDownloadService.downloadData());
        Map<String, String> codes2phones = ResponseParser.parseToMap(phoneCodesDownloadService.downloadData());
        codes2countries.forEach((code, name) -> {
            if (name.toUpperCase().contains(countryName.toUpperCase())) {
                Map<String, String> resultElem = new HashMap<>();
                resultElem.put("countryCode", code);
                resultElem.put("countryName", name);
                resultElem.put("phoneCode", codes2phones.get(code));
                result.add(resultElem);
            }
        });
        return result;

    }
}
