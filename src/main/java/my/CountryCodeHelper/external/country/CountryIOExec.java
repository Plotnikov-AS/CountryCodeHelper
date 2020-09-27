package my.CountryCodeHelper.external.country;

import my.CountryCodeHelper.external.ErrorCode;
import my.CountryCodeHelper.external.ExecuteExtSystemImpl;
import my.CountryCodeHelper.external.ExtRequest;
import my.CountryCodeHelper.external.ExtResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class CountryIOExec extends ExecuteExtSystemImpl {

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
            //TODO log here
            e.printStackTrace();
            getResponse().setErrorCode(ErrorCode.ERROR_CODE_FAILURE);
        }
        return getResponse();
    }

    @Override
    protected InputStream receiveData() throws IOException {
        String dataPath = getRequest().getExtSysUrl();
        switch (getRequest().getMethod()) {
            case GET_COUNTRIES:
                dataPath += "names.json";
                break;
            case GET_PHONE_CODES:
                dataPath += "phone.json";
                break;
        }
        return new URL(dataPath).openStream();
    }
}
