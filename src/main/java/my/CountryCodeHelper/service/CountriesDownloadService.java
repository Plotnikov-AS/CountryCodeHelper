package my.CountryCodeHelper.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import my.CountryCodeHelper.external.ErrorCode;
import my.CountryCodeHelper.external.ExtRequest;
import my.CountryCodeHelper.external.ExtResponse;
import my.CountryCodeHelper.external.country.CountryIOExec;
import my.CountryCodeHelper.model.Country;
import my.CountryCodeHelper.model.PhoneCode;
import my.CountryCodeHelper.repo.CountryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.sql.Date;
import java.util.Map;

@Service
public class CountriesDownloadService extends DataDownloadService {
    @Autowired
    private CountryRepo countryRepo;
    private final String EXT_SYSTEM_URL = "http://country.io/names.json";

    @Override
    public void downloadData() {
        ExtRequest request = new ExtRequest.Builder()
                .setExtSysUrl(EXT_SYSTEM_URL)
                .build();
        CountryIOExec exec = new CountryIOExec(request);
        ExtResponse response = exec.execute();
        updateData(response);
    }

    @Override
    public void updateData(ExtResponse response) {
        if (response.getErrorCode().equals(ErrorCode.ERROR_CODE_OK)) {
            Gson gson = new Gson();
            Map<String, String> codes2countries = gson.fromJson(new InputStreamReader(response.getReceivedData()), new TypeToken<Map<String, String>>() {
            }.getType());
            codes2countries.forEach((countryCode, countryName) -> {
                Country existingCountry = countryRepo.findByCountryCode(countryCode);
                PhoneCode phoneCode = existingCountry == null ? new PhoneCode() : existingCountry.getPhoneCode();
                phoneCode.setCountryCode(countryCode);
                Country country = existingCountry == null ? new Country() : existingCountry;
                country.setCountryCode(countryCode);
                country.setCountryName(countryName);
                country.setUpdTime(new Date(System.currentTimeMillis()));
                country.setPhoneCode(phoneCode);
                countryRepo.save(country);
            });
        }
    }
}
