package my.CountryCodeHelper.model;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "table_phone_code")
public class PhoneCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "phone_code")
    private String phoneCode;
    @Column(name = "phone2country")
    private Long phone2country;
    @Column(name = "upd_time")
    private Date updTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public Long getPhone2country() {
        return phone2country;
    }

    public void setPhone2country(Long phone2country) {
        this.phone2country = phone2country;
    }

    public Date getUpdTime() {
        return updTime;
    }

    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}
