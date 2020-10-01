package my.CountryCodeHelper.service.data.refresh;

import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.service.data.DataService;
import my.CountryCodeHelper.service.data.download.CountriesDownloadService;
import my.CountryCodeHelper.service.data.download.DataDownloadService;
import my.CountryCodeHelper.service.data.download.PhoneCodesDownloadService;
import my.CountryCodeHelper.service.data.update.CountriesUpdateService;
import my.CountryCodeHelper.service.data.update.DataUpdateService;
import my.CountryCodeHelper.service.data.update.PhonesUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Refresher {
    private static final Logger logger = LoggerFactory.getLogger(Refresher.class);
    private static Map<Class<? extends DataService>, Thread> runningServices = new HashMap<>();

    public static synchronized void refresh(DataDownloadService... dataDownloadServices) {
        for (DataDownloadService dataDownloadService : dataDownloadServices) {
            try {
                runDownload(dataDownloadService);
            } catch (DownloadingException e) {
                logger.error(dataDownloadService.getClass().getName() + " refresh failed: " + e.getMessage());
                runningServices.remove(dataDownloadService.getClass());
            }
        }
    }

    private static synchronized void runDownload(DataDownloadService dataDownloadService) {
        logger.info("... Creating download thread for " + dataDownloadService.getClass().getSimpleName());
        Thread downloadThread = new Thread(dataDownloadService);
        runningServices.put(dataDownloadService.getClass(), downloadThread);
        logger.info("... Services on air: " + runningServicesToPrettyString());
        downloadThread.start();
        createWatcher(dataDownloadService, downloadThread);
    }

    private static synchronized void runUpdate(DataService dataService) {
        DataUpdateService dataUpdateService = ((DataDownloadService) dataService).getDataUpdateService();
        logger.info("... Creating update thread for " + dataUpdateService.getClass().getSimpleName());
        Thread updateThread = new Thread(dataUpdateService);
        runningServices.put(dataUpdateService.getClass(), updateThread);
        updateThread.start();
        createWatcher(dataUpdateService, updateThread);
    }

    private static synchronized void createWatcher(DataService dataService, Thread thread) {
        logger.info("... Creating watcher for " + dataService.getClass().getSimpleName());
        RefreshRunningWatcher checker = new RefreshRunningWatcher(dataService, thread);
        Thread checkerThread = new Thread(checker);
        checkerThread.start();
    }

    public static synchronized boolean isRefreshRunning(Class<? extends DataService> service) {
        return runningServices.containsKey(service);
    }

    public static synchronized void setFinished(DataService dataService) {
        if (dataService instanceof DataDownloadService) {
            runUpdate(dataService);
        }
        runningServices.remove(dataService.getClass());
        logger.info("... Services on air: " + runningServicesToPrettyString());
    }

    private static String runningServicesToPrettyString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Class<? extends DataService>, Thread> entry : runningServices.entrySet()) {
            sb.append(entry.getKey().getSimpleName());
            sb.append(" ");
        }
        return sb.toString();
    }

    public static synchronized boolean isCountriesRefreshRunning() {
        return isRefreshRunning(CountriesDownloadService.class) || isRefreshRunning(CountriesUpdateService.class);
    }

    public static synchronized boolean isPhonesRefreshRunning() {
        return isRefreshRunning(PhoneCodesDownloadService.class) || isRefreshRunning(PhonesUpdateService.class);
    }
}
