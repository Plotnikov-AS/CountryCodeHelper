package my.CountryCodeHelper.external;

public class ExtRequest {
    public static class Builder {
        private String extSysUrl;
        private ExtMethod method;

        public Builder(){
        }

        public Builder setExtSysUrl(String extSysUrl) {
            this.extSysUrl = extSysUrl;
            return this;
        }

        public Builder setMethod(ExtMethod method) {
            this.method = method;
            return this;
        }

        public ExtRequest build() {
            ExtRequest request = new ExtRequest();
            request.extSysUrl = this.extSysUrl;
            request.method = this.method;
            return request;
        }
    }

    private String extSysUrl;
    private ExtMethod method;

    private ExtRequest() {
    }

    public ExtMethod getMethod() {
        return method;
    }

    public String getExtSysUrl() {
        return extSysUrl;
    }


}
