package my.CountryCodeHelper.controller;

import my.CountryCodeHelper.service.CountryPhonesCombinerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/code")
public class CodeController {
    private static final Logger logger = LoggerFactory.getLogger(CodeController.class);
    private final CountryPhonesCombinerService combinerService;

    @Autowired
    public CodeController(CountryPhonesCombinerService combinerService) {
        this.combinerService = combinerService;
    }

    public ResponseEntity<Object> emptyRequest() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<Object> showPhoneCodesForCountries(String countryName) {
        if (countryName.isEmpty()) return emptyRequest();
        List<Map<String, String>> country2phones;
        try {
            logger.info("Start searching info for country name, contains " + countryName);
            country2phones = combinerService.getCombinedCountryAndPhone(countryName);
        } catch (DataAccessResourceFailureException e) {
            logger.warn("Cant reach database.");
            country2phones = combinerService.getCombinedCountryAndPhoneDirectlyFromExtSystem(countryName);
        }
        return makeResponse(country2phones);
    }

    private ResponseEntity<Object> makeResponse(List<Map<String, String>> data) {
        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            logger.info("Sending response: " + data);
            return new ResponseEntity<>(data, HttpStatus.OK);
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<Exception> handleAllExceptions(RuntimeException ex) {
        logger.error("INTERNAL ERROR! " + ex.getMessage());
        return new ResponseEntity<>(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
