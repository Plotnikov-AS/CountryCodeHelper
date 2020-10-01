package my.CountryCodeHelper.service.data.download;

import my.CountryCodeHelper.external.ErrorCode;
import my.CountryCodeHelper.external.ExtResponse;
import my.CountryCodeHelper.external.country.CountryIOExec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class PhoneCodesDownloadServiceTest {

    @Mock
    CountryIOExec exec;

    @InjectMocks
    PhoneCodesDownloadService phoneCodesDownloadService;

    @BeforeEach
    void setUp() {
        setMockOutput();
    }

    void setMockOutput() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testNullResponse() {
        when(exec.execute()).thenReturn(null);
        assertNull(phoneCodesDownloadService.downloadData());
    }

    @Test
    void testEmptyResponseDataResponse() {
        ExtResponse emptyRecievedDataResponse = new ExtResponse();
        emptyRecievedDataResponse.setReceivedData(null);
        emptyRecievedDataResponse.setErrorCode(ErrorCode.ERROR_CODE_OK);
        when(exec.execute()).thenReturn(emptyRecievedDataResponse);
        ExtResponse response = phoneCodesDownloadService.downloadData();
        assertNull(response.getReceivedData());
        assertEquals(ErrorCode.ERROR_CODE_OK, response.getErrorCode());
    }

    @Test
    void testNormalResponse() {
        ExtResponse normalResponse = new ExtResponse();
        normalResponse.setErrorCode(ErrorCode.ERROR_CODE_OK);
        normalResponse.setReceivedData(new ByteArrayInputStream("some".getBytes()));
        when(exec.execute()).thenReturn(normalResponse);
        ExtResponse response = phoneCodesDownloadService.downloadData();
        assertNotNull(response.getReceivedData());
        assertEquals(ErrorCode.ERROR_CODE_OK, response.getErrorCode());
    }

}