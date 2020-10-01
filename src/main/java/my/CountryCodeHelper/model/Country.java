package my.CountryCodeHelper.model;

import javax.persistence.*;

@Entity
@Table(name = "table_country")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "country_code")
    private String countryCode;
    @Column(name = "country_name")
    private String countryName;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "country2phone", referencedColumnName = "id")
    private PhoneCode phoneCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public PhoneCode getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(PhoneCode phoneCode) {
        this.phoneCode = phoneCode;
    }

    @Override
    public String toString() {
        return "Country: {Country code: " + countryCode + "; Country name: " + countryName +
                "; Phone code: " + phoneCode.getPhoneCode() + ";}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        return (((Country) obj).getCountryCode().equalsIgnoreCase(this.countryCode))
                && (((Country) obj).getCountryName().equalsIgnoreCase(this.countryName))
                && (((Country) obj).getPhoneCode().getPhoneCode().equalsIgnoreCase(this.phoneCode.getPhoneCode()));
    }
}
