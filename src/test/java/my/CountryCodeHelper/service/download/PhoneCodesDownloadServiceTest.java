package my.CountryCodeHelper.service.download;

import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.external.ErrorCode;
import my.CountryCodeHelper.external.ExtResponse;
import my.CountryCodeHelper.model.Country;
import my.CountryCodeHelper.model.PhoneCode;
import my.CountryCodeHelper.repo.proxy.PhoneCodeRepoProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
class PhoneCodesDownloadServiceTest {
    @Mock
    PhoneCodeRepoProxy phoneCodeRepo;

    @InjectMocks
    PhoneCodesDownloadService phoneCodesDownloadService;

    PhoneCode correctPhoneCode;
    Country correctCountry;

    ExtResponse failureResponse;
    ExtResponse badResponse;
    ExtResponse goodResponse;


    @BeforeEach
    void setUp() {
        String correctCountryCode = "BE";
        correctPhoneCode = new PhoneCode();
        correctPhoneCode.setId(1L);
        correctPhoneCode.setCountryCode(correctCountryCode);
        correctPhoneCode.setPhoneCode("32");
        correctPhoneCode.setCountry(correctCountry);

        correctCountry = new Country();
        correctCountry.setId(1L);
        correctCountry.setCountryName("Belgium");
        correctCountry.setCountryCode(correctCountryCode);
        correctCountry.setPhoneCode(correctPhoneCode);
        correctCountry.setUpdTime(new Date(System.currentTimeMillis()));


        failureResponse = new ExtResponse();
        badResponse = new ExtResponse();
        goodResponse = new ExtResponse();

        failureResponse.setErrorCode(ErrorCode.ERROR_CODE_FAILURE);

        goodResponse.setErrorCode(ErrorCode.ERROR_CODE_OK);
        goodResponse.setReceivedData(new ByteArrayInputStream("{\"BD\": \"880\", \"BE\": \"32\", \"BF\": \"226\"}".getBytes()));

        badResponse.setErrorCode(ErrorCode.ERROR_CODE_OK);
        badResponse.setReceivedData(new ByteArrayInputStream("some incorrect string".getBytes()));

        setMockOutput();
    }

    void setMockOutput() {
        when(phoneCodeRepo.getByCountryCode(anyString())).thenReturn(correctPhoneCode);
        doNothing().when(phoneCodeRepo).save(any());
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void updateData() throws DownloadingException {
        assertThrows(DownloadingException.class, () -> phoneCodesDownloadService.updateData(failureResponse));
        System.out.println("failure response passed");

        assertThrows(DownloadingException.class, () -> phoneCodesDownloadService.updateData(badResponse));
        System.out.println("incorrect string response passed");

        badResponse.setReceivedData(null);
        assertThrows(DownloadingException.class, () -> phoneCodesDownloadService.updateData(badResponse));
        System.out.println("receivedData = null response passed");

        badResponse.setReceivedData(new ByteArrayInputStream("".getBytes()));
        assertThrows(DownloadingException.class, () -> phoneCodesDownloadService.updateData(badResponse));
        System.out.println("Empty string response passed");

        phoneCodesDownloadService.updateData(goodResponse);

    }
}