package my.CountryCodeHelper.repo;

import my.CountryCodeHelper.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CountryRepo extends JpaRepository<Country, Long> {
    Country findByCountryCode(String countryCode);

    Set<Country> findByCountryNameContaining(String countryName);
}
