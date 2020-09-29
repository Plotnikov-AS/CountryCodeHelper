package my.CountryCodeHelper.external.country;

import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.external.ExecuteExtSystemImpl;
import my.CountryCodeHelper.external.ExtRequest;
import my.CountryCodeHelper.external.ExtResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static my.CountryCodeHelper.external.ErrorCode.ERROR_CODE_FAILURE;
import static my.CountryCodeHelper.external.ErrorCode.ERROR_CODE_OK;

public class CountryIOExec extends ExecuteExtSystemImpl {
    private static Logger logger = LoggerFactory.getLogger(CountryIOExec.class);

    private CountryIO countryIO;

    public CountryIOExec(ExtRequest request) {
        super(request);
        countryIO = (CountryIO) getExtSystem();
    }

    @Override
    public ExtResponse execute() throws DownloadingException {
        return createResponse();
    }

    @Override
    protected ExtResponse createResponse() throws DownloadingException {
        ExtResponse response = new ExtResponse();
        switch (request.getMethod()) {
            case GET_PHONE_CODES:
                countryIO.getPhones();
                break;
            case GET_COUNTRY_NAMES:
                countryIO.getCountryNames();
                break;
        }
        if (countryIO.isSystemAvailable() && countryIO.getData() != null) {
            response.setReceivedData(countryIO.getData());
            response.setErrorCode(ERROR_CODE_OK);
        } else {
            response.setErrorCode(ERROR_CODE_FAILURE);
            logger.debug("Failed to " + request.getMethod());
        }
        return response;
    }
}
