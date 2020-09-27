package my.CountryCodeHelper.external;

public class ExtRequest {
    public static class Builder {
        private String extSysUrl;

        public Builder(){
        }

        public Builder setExtSysUrl(String extSysUrl) {
            this.extSysUrl = extSysUrl;
            return this;
        }


        public ExtRequest build() {
            ExtRequest request = new ExtRequest();
            request.extSysUrl = this.extSysUrl;
            return request;
        }
    }

    private String extSysUrl;

    private ExtRequest() {
    }


    public String getExtSysUrl() {
        return extSysUrl;
    }


}
