package my.CountryCodeHelper.service;

import my.CountryCodeHelper.external.ErrorCode;
import my.CountryCodeHelper.external.ExtMethod;
import my.CountryCodeHelper.external.ExtRequest;
import my.CountryCodeHelper.external.ExtResponse;
import my.CountryCodeHelper.external.country.CountryIOExec;
import my.CountryCodeHelper.model.Country;
import my.CountryCodeHelper.repo.CountryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

@Service
public class CountriesDownloadService extends DataDownloadService {
    @Autowired
    CountryRepo countryRepo;

    @Override
    protected Date checkLastUpdate() {
        Country country = countryRepo.getLatestUpdated();
        return country.getUpdTime();
    }

    @Override
    public void updateDataIfNeed() {
        if (isNeedToRefreshDataInDB()) {
            ExtRequest request = new ExtRequest.Builder()
                    .setExtSysUrl(COUNTRY_IO_URL)
                    .setMethod(ExtMethod.GET_COUNTRIES)
                    .build();
            CountryIOExec exec = new CountryIOExec(request);
            ExtResponse response = exec.execute();
            if (response.getErrorCode().equals(ErrorCode.ERROR_CODE_OK)) {
                try {
                    String filePath = "./src/main/resources/data/names.json";
                    Files.copy(response.getReceivedData(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                    File jsonFile = new File(filePath);
                } catch (IOException e) {
                    //TODO log here
                    e.printStackTrace();
                }
            }
        }
    }
}
