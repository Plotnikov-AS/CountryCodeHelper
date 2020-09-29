package my.CountryCodeHelper.repo.repo;

import my.CountryCodeHelper.model.PhoneCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneCodeRepo extends JpaRepository<PhoneCode, Long> {
    PhoneCode getByCountryCode(String countryCode);
}
