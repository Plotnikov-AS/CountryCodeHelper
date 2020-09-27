package my.CountryCodeHelper.service;

import java.util.Date;


public abstract class DataDownloadService {
    private final Long ONE_DAY_IN_MILLIS = 86400000L;
    protected final String COUNTRY_IO_URL = "http://country.io/";

    protected abstract Date checkLastUpdate();
    public abstract void updateDataIfNeed();

    protected boolean isNeedToRefreshDataInDB(){
        return System.currentTimeMillis() - checkLastUpdate().getTime() >= ONE_DAY_IN_MILLIS;
    }

}
