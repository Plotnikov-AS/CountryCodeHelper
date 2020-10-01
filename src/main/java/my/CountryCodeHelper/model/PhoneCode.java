package my.CountryCodeHelper.model;

import javax.persistence.*;

@Entity
@Table(name = "table_phone_code")
public class PhoneCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "country_code")
    private String countryCode;
    @Column(name = "phone_code")
    private String phoneCode;
    @OneToOne(mappedBy = "phoneCode")
    private Country country;

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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "PhoneCode: {Country code:" + countryCode + "; Phone code:" + phoneCode + ";}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        return ((PhoneCode) obj).getCountryCode().equalsIgnoreCase(this.countryCode)
                && ((PhoneCode) obj).getPhoneCode().equalsIgnoreCase(this.phoneCode);
    }
}
