package my.CountryCodeHelper.service;

import my.CountryCodeHelper.external.ExtResponse;

public abstract class DataDownloadService {

    public abstract void downloadData();

    public abstract void updateData(ExtResponse response);

}
