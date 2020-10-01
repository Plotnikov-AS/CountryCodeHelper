package my.CountryCodeHelper.service.data.update;

import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.external.ExtResponse;
import my.CountryCodeHelper.service.data.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;

public abstract class DataUpdateService implements Runnable, DataService {
    private static final Logger logger = LoggerFactory.getLogger(DataUpdateService.class);
    protected ExtResponse response;

    @Override
    public void run() {
        try {
            update();
        } catch (DownloadingException e) {
            logger.error(e.getMessage());
        }
    }

    protected abstract void update() throws DownloadingException, DataAccessResourceFailureException;

    public ExtResponse getResponse() {
        return response;
    }

    public void setResponse(ExtResponse response) {
        this.response = response;
    }


}
