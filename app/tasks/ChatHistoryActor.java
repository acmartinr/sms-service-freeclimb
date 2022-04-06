package tasks;

import akka.actor.AbstractActor;
import com.google.inject.Inject;
import services.sms.ISMSApiService;

class ChatHistoryActor extends AbstractActor {

    private ISMSApiService smsApiService;

    @Inject
    public ChatHistoryActor(ISMSApiService smsApiService) {
        this.smsApiService = smsApiService;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().matchAny(o -> smsApiService.requestInboundMessages()).build();
    }
}