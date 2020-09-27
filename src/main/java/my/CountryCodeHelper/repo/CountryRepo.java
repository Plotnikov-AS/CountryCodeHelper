package my.CountryCodeHelper.repo;

import my.CountryCodeHelper.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CountryRepo extends JpaRepository<Country, Long> {
//    @Query(value = "select * from table_country where country_name like '%:countryName%'", nativeQuery = true)
    Set<Country> findByCountryNameContaining(String countryName);

    @Query(value = "select * from table_country where upd_time = (select MIN(upd_time) from table_country)", nativeQuery = true)
    Country getLatestUpdated();
}
