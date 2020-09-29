package my.CountryCodeHelper.exception;

public class DownloadingException extends RuntimeException {
    public DownloadingException() {
        super();
    }

    public DownloadingException(String message) {
        super(message);
    }

    public DownloadingException(Throwable e) {
        super(e);
    }
}
