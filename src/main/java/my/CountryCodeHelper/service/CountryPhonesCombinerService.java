package my.CountryCodeHelper.service;

import my.CountryCodeHelper.exception.DataNotFoundException;
import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.model.Country;
import my.CountryCodeHelper.model.PhoneCode;
import my.CountryCodeHelper.repo.proxy.CountryRepoProxy;
import my.CountryCodeHelper.repo.proxy.PhoneCodeRepoProxy;
import my.CountryCodeHelper.service.download.CountriesDownloadService;
import my.CountryCodeHelper.service.download.PhoneCodesDownloadService;
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
    private static final Long TWO_HOURS = 7200000L;

    @Autowired
    public CountryPhonesCombinerService(CountryRepoProxy countryRepo, PhoneCodeRepoProxy phoneCodeRepo, CountriesDownloadService countriesDownloadService, PhoneCodesDownloadService phoneCodesDownloadService) {
        this.countryRepo = countryRepo;
        this.phoneCodeRepo = phoneCodeRepo;
        this.countriesDownloadService = countriesDownloadService;
        this.phoneCodesDownloadService = phoneCodesDownloadService;
    }

    public synchronized List<Map<String, String>> getCombinedCountryAndPhone(String countryName) {
        try {
            refreshData();
            while (PhoneCodesDownloadService.isRefreshRunning()) {
                logger.info("... Waiting for phone refresh finishing");
                wait(250);
            }
            logger.info("... Start combining country name and phone code");
            List<Map<String, String>> country2phonesList = new ArrayList<>();
            Set<Country> countries = countryRepo.findByCountryNameContainingIgnoreCase(countryName);
            logger.info("... Getted countries: " + countries);
            countries.forEach(country -> {
                Map<String, String> country2phones = new HashMap<>();
                country2phones.put("countryCode", country.getCountryCode());
                country2phones.put("countryName", country.getCountryName());
                PhoneCode phoneCode = country.getPhoneCode();
                if (phoneCode == null) {
                    logger.info("... Country with empty phone code. Start refreshing database from country.io");
                    phoneCodesDownloadService.execute();
                    phoneCode = phoneCodeRepo.getByCountryCode(country.getCountryCode());
                }
                if (phoneCode == null) {
                    logger.warn("... Phone code still null. Trying to get directly");
                    Map<String, String> codes2phones = phoneCodesDownloadService.parseResponseToMap(phoneCodesDownloadService.downloadData());
                    phoneCode = new PhoneCode();
                    phoneCode.setPhoneCode(codes2phones.get(country.getCountryCode()));
                    phoneCode.setCountryCode(country.getCountryCode());
                    phoneCode.setCountry(country);
                    phoneCodeRepo.save(phoneCode);
                }
                country2phones.put("phoneCode", phoneCode.getPhoneCode() == null ? "" : phoneCode.getPhoneCode());
                country2phonesList.add(country2phones);
            });
            return country2phonesList;
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } catch (DataAccessResourceFailureException e) {
            logger.warn("Cant reach database.");
            return getCombinedCountryAndPhoneDirectlyFromExtSystem(countryName);
        }
        throw new DataNotFoundException("Nothing found for " + countryName);
    }

    public synchronized List<Map<String, String>> getCombinedCountryAndPhoneDirectlyFromExtSystem(String countryName) throws DownloadingException {
        logger.info("... Getting info directly from ext system");
        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> codes2countries = countriesDownloadService.parseResponseToMap(countriesDownloadService.downloadData());
        Map<String, String> codes2phones = phoneCodesDownloadService.parseResponseToMap(phoneCodesDownloadService.downloadData());
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

    private synchronized void refreshData() throws DataAccessResourceFailureException {
        try {
            if (isNeedToRefresh()) {
                logger.info("... Need to refresh data in database");
                if (!CountriesDownloadService.isRefreshRunning()) {
                    logger.info("... Start refreshing countries");
                    countriesDownloadService.execute();
                }
                if (!PhoneCodesDownloadService.isRefreshRunning()) {
                    logger.info("... Start refreshing phones");
                    phoneCodesDownloadService.execute();
                }
            }
        } catch (DownloadingException e) {
            logger.warn("Data refreshing failed: " + e.getMessage());
        }
    }

    private synchronized boolean isNeedToRefresh() {
        return ((System.currentTimeMillis() - CountriesDownloadService.getUpdatedTime()) > TWO_HOURS)
                || ((System.currentTimeMillis() - PhoneCodesDownloadService.getUpdatedTime()) > TWO_HOURS);
    }
}
