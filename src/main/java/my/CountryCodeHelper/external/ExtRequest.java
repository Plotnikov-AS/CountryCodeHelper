package my.CountryCodeHelper.external;

public class ExtRequest {
    public static class Builder {
        private ExtSystem extSystem;
        private ExtMethods method;

        public Builder() {
        }

        public Builder setExtSystem(ExtSystem extSystem) {
            this.extSystem = extSystem;
            return this;
        }

        public Builder setMethod(ExtMethods method) {
            this.method = method;
            return this;
        }

        public ExtRequest build() {
            ExtRequest request = new ExtRequest();
            request.extSystem = this.extSystem;
            request.method = this.method;
            return request;
        }
    }

    private ExtSystem extSystem;
    private ExtMethods method;

    private ExtRequest() {
    }


    public ExtSystem getExtSystem() {
        return extSystem;
    }

    public ExtMethods getMethod() {
        return method;
    }
}
