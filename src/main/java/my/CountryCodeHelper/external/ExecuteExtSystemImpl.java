package my.CountryCodeHelper.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExecuteExtSystemImpl implements ExecuteExtSystem {
    private static Logger logger = LoggerFactory.getLogger(ExecuteExtSystemImpl.class);

    protected ExtRequest request;
    private ExtSystem extSystem;

    public ExecuteExtSystemImpl(ExtRequest request) {
        this.request = request;
        this.extSystem = request.getExtSystem();
    }

    protected abstract ExtResponse createResponse();

    public ExtRequest getRequest() {
        return request;
    }

    public ExtSystem getExtSystem() {
        return extSystem;
    }
}
