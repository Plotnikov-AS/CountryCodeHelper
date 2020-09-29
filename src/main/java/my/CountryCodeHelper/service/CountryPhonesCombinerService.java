package my.CountryCodeHelper.service;

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
            if (isNeedToRefresh()) {
                refreshData();
            }
            while (PhoneCodesDownloadService.isRefreshRunning()) {
                wait(250);
            }
        } catch (Exception e) {
            logger.warn("Data refreshing failed: " + e.getMessage());
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
                logger.info("Country with empty phone code. Start refreshing database from country.io");
                phoneCodesDownloadService.execute();
                phoneCode = phoneCodeRepo.getByCountryCode(country.getCountryCode());
            }
            if (phoneCode == null) {
                logger.warn("Country still null. Trying to get directly");
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
    }

    public synchronized List<Map<String, String>> getCombinedCountryAndPhoneDirectlyFromExtSystem(String countryName) throws DownloadingException {
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

    public void refreshData() throws DownloadingException {
        if (CountriesDownloadService.isRefreshRunning()) {
            logger.info("... Countries refresh already running");
        } else {
            countriesDownloadService.execute();
        }
        if (PhoneCodesDownloadService.isRefreshRunning()) {
            logger.info("... Countries refresh already running");
        } else {
            phoneCodesDownloadService.execute();
        }
    }

    private boolean isNeedToRefresh() {
        return ((System.currentTimeMillis() - CountriesDownloadService.getUpdatedTime()) > TWO_HOURS)
                || ((System.currentTimeMillis() - PhoneCodesDownloadService.getUpdatedTime()) > TWO_HOURS);
    }
}
