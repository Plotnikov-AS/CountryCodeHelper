package my.CountryCodeHelper.controller;

import my.CountryCodeHelper.service.data.download.DataDownloadService;
import my.CountryCodeHelper.service.data.refresh.Refresher;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class RefreshControllerTest {
    @InjectMocks
    RefreshController controller;

    @Test
    void testRefreshRunning() {
        try (MockedStatic<Refresher> mockedRefresher = mockStatic(Refresher.class)) {
            mockedRefresher.when(Refresher::isCountriesRefreshRunning).thenReturn(true);
            assertTrue(Refresher.isCountriesRefreshRunning());
            mockedRefresher.verify(Refresher::isCountriesRefreshRunning);
            assertEquals(new ResponseEntity<>("Refresh already running", HttpStatus.NO_CONTENT), controller.refresh());
        }
        try (MockedStatic<Refresher> mockedRefresher = mockStatic(Refresher.class)) {
            mockedRefresher.when(Refresher::isPhonesRefreshRunning).thenReturn(true);
            assertTrue(Refresher.isPhonesRefreshRunning());
            mockedRefresher.verify(Refresher::isPhonesRefreshRunning);
            assertEquals(new ResponseEntity<>("Refresh already running", HttpStatus.NO_CONTENT), controller.refresh());
        }
    }

    @Test
    void testRefresh() {
        try (MockedStatic<Refresher> mockedRefresher = mockStatic(Refresher.class)) {
            mockedRefresher.when(() -> Refresher.refresh(any(DataDownloadService.class))).thenAnswer(invocationOnMock -> null);
            assertEquals(new ResponseEntity<>(HttpStatus.OK), controller.refresh());
        }
    }

    @Test
    void handleAllExceptions() {
        ResponseEntity<Exception> responseEntity = controller.handleAllExceptions(new RuntimeException());
        assertEquals(500, responseEntity.getStatusCode().value());
    }
}