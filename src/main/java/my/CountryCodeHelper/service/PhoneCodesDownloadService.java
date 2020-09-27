package my.CountryCodeHelper.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import my.CountryCodeHelper.exception.ObjectNotFoundException;
import my.CountryCodeHelper.external.ErrorCode;
import my.CountryCodeHelper.external.ExtRequest;
import my.CountryCodeHelper.external.ExtResponse;
import my.CountryCodeHelper.external.country.CountryIOExec;
import my.CountryCodeHelper.model.PhoneCode;
import my.CountryCodeHelper.repo.PhoneCodeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.util.Map;

@Service
public class PhoneCodesDownloadService extends DataDownloadService {
    private static final Logger logger = LoggerFactory.getLogger(PhoneCodesDownloadService.class);
    @Autowired
    private PhoneCodeRepo phoneCodeRepo;
    private final String EXT_SYSTEM_URL = "http://country.io/phone.json";

    @Override
    public void downloadData() {
        ExtRequest request = new ExtRequest.Builder()
                .setExtSysUrl(EXT_SYSTEM_URL)
                .build();
        CountryIOExec exec = new CountryIOExec(request);
        ExtResponse response = exec.execute();
        updateData(response);
    }

    @Override
    public void updateData(ExtResponse response) {
        if (response.getErrorCode().equals(ErrorCode.ERROR_CODE_OK)) {
            Gson gson = new Gson();
            Map<String, String> phones2countries = gson.fromJson(new InputStreamReader(response.getReceivedData()), new TypeToken<Map<String, String>>() {
            }.getType());
            phones2countries.forEach((countryCode, phoneCode) -> {
                try {
                    PhoneCode phone = phoneCodeRepo.getByCountryCode(countryCode);
                    if (phone == null)
                        throw new ObjectNotFoundException("Object with code " + countryCode + " not exist in table_phone_code");
                    phone.setPhoneCode(phoneCode);
                    phoneCodeRepo.save(phone);
                } catch (ObjectNotFoundException e) {
                    logger.debug(e.getMessage());
                }
            });

        }
    }
}
