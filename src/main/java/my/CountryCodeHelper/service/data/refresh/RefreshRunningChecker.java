package my.CountryCodeHelper.service.data.refresh;

import my.CountryCodeHelper.service.data.DataService;
import my.CountryCodeHelper.service.data.download.DataDownloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RefreshRunningChecker implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RefreshRunningChecker.class);
    private Thread refreshThread;
    private DataService dataService;

    RefreshRunningChecker(DataService dataService, Thread refreshThread) {
        this.refreshThread = refreshThread;
        this.dataService = dataService;
    }

    @Override
    public void run() {
        execute();
    }

    private synchronized void execute() {
        while (refreshThread.isAlive()) {
            try {
                wait(100);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
        logger.info("... " + dataService.getClass().getSimpleName() + " finished");
        setFinished();
    }


    public void setFinished() {
        Refresher.setFinished(dataService);
    }

    Thread getRefreshThread() {
        return refreshThread;
    }

    void setRefreshThread(Thread refreshThread) {
        this.refreshThread = refreshThread;
    }

    DataService getDataService() {
        return dataService;
    }

    void setDataService(DataDownloadService dataDownloadService) {
        this.dataService = dataDownloadService;
    }
}
