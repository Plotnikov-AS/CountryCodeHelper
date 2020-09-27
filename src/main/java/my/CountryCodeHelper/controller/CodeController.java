package my.CountryCodeHelper.controller;

import my.CountryCodeHelper.model.Country;
import my.CountryCodeHelper.repo.CountryRepo;
import my.CountryCodeHelper.service.CountriesDownloadService;
import my.CountryCodeHelper.service.PhoneCodesDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequestMapping("/code")
public class CodeController {
    @Autowired
    CountryRepo countryRepo;
    @Autowired
    CountriesDownloadService countriesDownloadService;
    @Autowired
    PhoneCodesDownloadService phoneCodesDownloadService;

    @GetMapping()
    public ResponseEntity<Object> showPhoneCodesForCountries(String countryName) {
        List<Map<String, String>> country2phones = findCountriesAndPhoneCodes(countryName);
        return new ResponseEntity<>(country2phones, HttpStatus.OK);
    }

    private List<Map<String, String>> findCountriesAndPhoneCodes(String countryName) {
        countriesDownloadService.downloadData();
        phoneCodesDownloadService.downloadData();
        List<Map<String, String>> country2phonesList = new ArrayList<>();
        Set<Country> countries = countryRepo.findByCountryNameContaining(countryName);
        countries.forEach(country -> {
            Map<String, String> country2phones = new HashMap<>();
            country2phones.put("countryCode", country.getCountryCode());
            country2phones.put("countryName", country.getCountryName());
            country2phones.put("phoneCode", country.getPhoneCode().getPhoneCode());
            country2phonesList.add(country2phones);
        });
        return country2phonesList;
    }
}
