package my.CountryCodeHelper.repo.repo;

import my.CountryCodeHelper.model.PhoneCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PhoneCodeRepo extends JpaRepository<PhoneCode, Long> {

    @Query(value = "select * from table_phone_code where country_code = :countryCode", nativeQuery = true)
    PhoneCode getByCountryCode(@Param("countryCode") String countryCode);

    Set<PhoneCode> getByCountryCodeIn(Set<String> countryCodes);
}
