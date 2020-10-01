package my.CountryCodeHelper.controller;

import my.CountryCodeHelper.service.data.download.CountriesDownloadService;
import my.CountryCodeHelper.service.data.download.PhoneCodesDownloadService;
import my.CountryCodeHelper.service.data.refresh.Refresher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/refresh")
public class RefreshController {
    private final static Logger logger = LoggerFactory.getLogger(RefreshController.class);
    private final CountriesDownloadService countriesDownloadService;
    private final PhoneCodesDownloadService phoneCodesDownloadService;

    @Autowired
    public RefreshController(CountriesDownloadService countriesDownloadService, PhoneCodesDownloadService phoneCodesDownloadService) {
        this.countriesDownloadService = countriesDownloadService;
        this.phoneCodesDownloadService = phoneCodesDownloadService;
    }

    @PostMapping
    public ResponseEntity<Object> refresh() {
        logger.info("... Refresh request received");
        if (Refresher.isCountriesRefreshRunning() || Refresher.isPhonesRefreshRunning()) {
            return new ResponseEntity<>("Refresh already running", HttpStatus.NO_CONTENT);
        }
        Refresher.refresh(countriesDownloadService, phoneCodesDownloadService);
        waitUtilRefreshNotFinished();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private synchronized void waitUtilRefreshNotFinished() {
        while (Refresher.isPhonesRefreshRunning() || Refresher.isCountriesRefreshRunning()) {
            try {
                wait(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<Exception> handleAllExceptions(RuntimeException ex) {
        logger.error("INTERNAL ERROR! " + ex.getMessage());
        return new ResponseEntity<>(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
