package my.CountryCodeHelper.external;

import java.io.InputStream;

public class ExtResponse {
    private InputStream receivedData;
    private ErrorCode errorCode;

    public ExtResponse() {
    }

    public ExtResponse(InputStream receivedData, ErrorCode errorCode) {
        this.receivedData = receivedData;
        this.errorCode = errorCode;
    }

    public InputStream getReceivedData() {
        return receivedData;
    }

    public void setReceivedData(InputStream receivedData) {
        this.receivedData = receivedData;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
