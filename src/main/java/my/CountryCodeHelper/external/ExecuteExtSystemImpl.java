package my.CountryCodeHelper.external;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class ExecuteExtSystemImpl implements ExecuteExtSystem {
    private ExtRequest request;
    private ExtResponse response;
    private URL url = null;

    public ExecuteExtSystemImpl(ExtRequest request) {
        try {
            this.request = request;
            response = new ExtResponse();
            url = new URL(request.getExtSysUrl());
        } catch (MalformedURLException e){
            //TODO log here
            e.printStackTrace();
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
            //TODO log here
            if (connection != null) connection.disconnect();
            response.setErrorCode(ErrorCode.ERROR_CODE_FAILURE);
            return false;
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
