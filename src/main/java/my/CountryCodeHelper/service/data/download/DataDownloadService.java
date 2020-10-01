package my.CountryCodeHelper.service.data.download;

import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.external.ExtResponse;
import my.CountryCodeHelper.service.data.DataService;
import my.CountryCodeHelper.service.data.update.DataUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;

public abstract class DataDownloadService implements Runnable, DataService {
    private static final Logger logger = LoggerFactory.getLogger(DataDownloadService.class);
    protected DataUpdateService dataUpdateService;

    @Override
    public void run() {
        try {
            execute();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void execute() throws DownloadingException, DataAccessResourceFailureException {
            ExtResponse response = downloadData();
            dataUpdateService.setResponse(response);
    }

    public abstract ExtResponse downloadData() throws DownloadingException;

    protected abstract void setDataUpdateService(DataUpdateService dataUpdateService);

    public DataUpdateService getDataUpdateService() {
        return dataUpdateService;
    }
}
