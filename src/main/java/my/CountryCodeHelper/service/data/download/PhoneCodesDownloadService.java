package my.CountryCodeHelper.service.data.download;

import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.external.ExtMethods;
import my.CountryCodeHelper.external.ExtRequest;
import my.CountryCodeHelper.external.ExtResponse;
import my.CountryCodeHelper.external.country.CountryIO;
import my.CountryCodeHelper.external.country.CountryIOExec;
import my.CountryCodeHelper.service.data.update.DataUpdateService;
import my.CountryCodeHelper.service.data.update.PhonesUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhoneCodesDownloadService extends DataDownloadService {
    private static final Logger logger = LoggerFactory.getLogger(PhoneCodesDownloadService.class);

    @Autowired
    public PhoneCodesDownloadService(PhonesUpdateService phonesUpdateService) {
        setDataUpdateService(phonesUpdateService);
    }

    @Override
    public ExtResponse downloadData() throws DownloadingException {
        logger.info("... Downloading phones from country.io");
        ExtRequest request = new ExtRequest.Builder()
                .setExtSystem(new CountryIO())
                .setMethod(ExtMethods.GET_PHONE_CODES)
                .build();
        CountryIOExec exec = new CountryIOExec(request);
        return exec.execute();
    }

    @Override
    protected void setDataUpdateService(DataUpdateService dataUpdateService) {
        this.dataUpdateService = dataUpdateService;
    }
}
