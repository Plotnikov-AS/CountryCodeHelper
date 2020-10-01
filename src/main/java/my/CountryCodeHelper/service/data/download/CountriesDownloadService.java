package my.CountryCodeHelper.service.data.download;

import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.external.ExtMethods;
import my.CountryCodeHelper.external.ExtRequest;
import my.CountryCodeHelper.external.ExtResponse;
import my.CountryCodeHelper.external.country.CountryIO;
import my.CountryCodeHelper.external.country.CountryIOExec;
import my.CountryCodeHelper.service.data.update.CountriesUpdateService;
import my.CountryCodeHelper.service.data.update.DataUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountriesDownloadService extends DataDownloadService {
    private static final Logger logger = LoggerFactory.getLogger(CountriesDownloadService.class);

    @Autowired
    public CountriesDownloadService(CountriesUpdateService countriesUpdateService) {
        setDataUpdateService(countriesUpdateService);
    }

    @Override
    public ExtResponse downloadData() throws DownloadingException {
        logger.info("... Downloading countries from country.io");
        ExtRequest request = new ExtRequest.Builder()
                .setExtSystem(new CountryIO())
                .setMethod(ExtMethods.GET_COUNTRY_NAMES)
                .build();
        CountryIOExec exec = new CountryIOExec(request);
        return exec.execute();
    }

    @Override
    protected void setDataUpdateService(DataUpdateService dataUpdateService) {
        this.dataUpdateService = dataUpdateService;
    }
}
