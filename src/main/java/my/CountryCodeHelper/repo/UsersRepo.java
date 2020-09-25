package my.CountryCodeHelper.repo;

import my.CountryCodeHelper.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UsersRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
