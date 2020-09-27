package my.CountryCodeHelper.controller;

import my.CountryCodeHelper.model.Country;
import my.CountryCodeHelper.repo.CountryRepo;
import my.CountryCodeHelper.repo.PhoneCodeRepo;
import my.CountryCodeHelper.service.CountriesDownloadService;
import my.CountryCodeHelper.service.PhoneCodesDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequestMapping("/main")
public class MainPageController {
    @Autowired
    CountryRepo countryRepo;
    @Autowired
    PhoneCodeRepo phoneCodeRepo;
    @Autowired
    CountriesDownloadService countriesDownloadService;
    @Autowired
    PhoneCodesDownloadService phoneCodesDownloadService;

    @GetMapping
    public String showPhoneCodesForCountries() {
        return "main";
    }

    @PostMapping
    public String showPhoneCodesForCountries(String countryName_txt, Model model) {
        List<Map<String, String>> country2phones = findCountriesAndPhoneCodes(countryName_txt);
        model.addAttribute("country2phones", country2phones);
        return "main";
    }

    private List<Map<String, String>> findCountriesAndPhoneCodes(String countryName) {
        countriesDownloadService.updateDataIfNeed();
        phoneCodesDownloadService.updateDataIfNeed();
        List<Map<String, String>> country2phonesList = new ArrayList<>();
        Set<Country> countries = countryRepo.findByCountryNameContaining(countryName);
        countries.forEach(country -> {
            Map<String, String> country2phones = new HashMap<>();
            country2phones.put("countryName", country.getCountryName());
            country2phones.put("phoneCode", phoneCodeRepo.getByPhone2country(country.getId()).getPhoneCode());
            country2phonesList.add(country2phones);
        });
        return country2phonesList;
    }
}
