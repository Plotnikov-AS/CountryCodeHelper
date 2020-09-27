package my.CountryCodeHelper.external.country;

import my.CountryCodeHelper.external.ErrorCode;
import my.CountryCodeHelper.external.ExecuteExtSystemImpl;
import my.CountryCodeHelper.external.ExtRequest;
import my.CountryCodeHelper.external.ExtResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class CountryIOExec extends ExecuteExtSystemImpl {
    private static Logger logger = LoggerFactory.getLogger(ExecuteExtSystemImpl.class);

    public CountryIOExec(ExtRequest request) {
        super(request);
    }

    @Override
    public ExtResponse execute() {
        try {
            if (extSystemIsAvailable()) {
                getResponse().setReceivedData(receiveData());
                getResponse().setErrorCode(ErrorCode.ERROR_CODE_OK);
            }
            else {
                getResponse().setErrorCode(ErrorCode.ERROR_CODE_FAILURE);
            }
        } catch (IOException e) {
            logger.error("Cannot receive data from " + getRequest().getExtSysUrl());
            logger.error(e.getMessage());
            getResponse().setErrorCode(ErrorCode.ERROR_CODE_FAILURE);
        }
        return getResponse();
    }

    @Override
    protected InputStream receiveData() throws IOException {
        String dataPath = getRequest().getExtSysUrl();
        return new URL(dataPath).openStream();
    }
}
