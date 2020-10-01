package my.CountryCodeHelper.repo.repo;

import my.CountryCodeHelper.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CountryRepo extends JpaRepository<Country, Long> {
    Country getByCountryCode(String countryCode);

    @Query(value = "select * from table_country where UPPER(country_name) like CONCAT('%', UPPER(:countryName), '%')", nativeQuery = true)
    Set<Country> findByCountryNameContainingIgnoreCase(@Param("countryName") String countryName);

    Set<Country> getByCountryCodeIn(Set<String> countryCodes);
}
