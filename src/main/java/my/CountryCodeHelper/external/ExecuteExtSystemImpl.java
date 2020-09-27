package my.CountryCodeHelper.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class ExecuteExtSystemImpl implements ExecuteExtSystem {
    private static Logger logger = LoggerFactory.getLogger(ExecuteExtSystemImpl.class);

    private ExtRequest request;
    private ExtResponse response;
    private URL url = null;

    public ExecuteExtSystemImpl(ExtRequest request) {
        try {
            this.request = request;
            response = new ExtResponse();
            url = new URL(request.getExtSysUrl());
        } catch (MalformedURLException e){
            logger.debug("Failed to creating URL with " + request.getExtSysUrl());
            response.setErrorCode(ErrorCode.ERROR_CODE_FAILURE);
        }
    }

    protected boolean extSystemIsAvailable() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.connect();
            int code = connection.getResponseCode();
            return code == 200;
        } catch (IOException e) {
            logger.error("Failed to check system availability with URL " + url.toString());
            response.setErrorCode(ErrorCode.ERROR_CODE_FAILURE);
            return false;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    protected abstract InputStream receiveData() throws IOException;

    public ExtRequest getRequest() {
        return request;
    }

    public void setRequest(ExtRequest request) {
        this.request = request;
    }

    public ExtResponse getResponse() {
        return response;
    }

    public void setResponse(ExtResponse response) {
        this.response = response;
    }
}
