package my.CountryCodeHelper.repo.repo;

import my.CountryCodeHelper.model.PhoneCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface PhoneCodeRepo extends JpaRepository<PhoneCode, Long> {
    PhoneCode getByCountryCode(String countryCode);

    Set<PhoneCode> getByCountryCodeIn(Set<String> countryCodes);
}
