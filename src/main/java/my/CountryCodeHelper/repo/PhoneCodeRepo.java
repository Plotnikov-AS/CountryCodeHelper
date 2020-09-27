package my.CountryCodeHelper.repo;

import my.CountryCodeHelper.model.PhoneCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneCodeRepo extends JpaRepository<PhoneCode, Long> {
    PhoneCode getByPhone2country(Long phone2country);

    @Query(value = "select * from table_phone_code where upd_time = (select MIN(upd_time) from table_phone_code)", nativeQuery = true)
    PhoneCode getLatestUpdated();
}
