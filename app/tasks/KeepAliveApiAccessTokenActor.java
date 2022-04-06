package tasks;

import akka.actor.AbstractActor;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.sms.YtelSMSApiService;

class KeepAliveApiAccessTokenActor extends AbstractActor {

    private YtelSMSApiService ytelSMSApiService;
    final Logger logger = LoggerFactory.getLogger("access");

    @Inject
    public KeepAliveApiAccessTokenActor(YtelSMSApiService ytelSMSApiService) {
        this.ytelSMSApiService = ytelSMSApiService;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().matchAny(o -> keepAliveBearerToken()).build();
    }

    public void keepAliveBearerToken() {
        if (!ytelSMSApiService.getApiRefreshToken().isEmpty()) {
            logger.info("Getting access token by refresh token...");
            ytelSMSApiService.getBearerTokenByRefreshToken();
        } else {
            logger.info("Getting access token by authentication...");
            ytelSMSApiService.getBearerTokenByAuthentication();
        }
        System.out.print("Done! ");
    }
}
