package my.CountryCodeHelper.external;

import my.CountryCodeHelper.exception.DownloadingException;

public interface ExecuteExtSystem {
    ExtResponse execute() throws DownloadingException;
}
