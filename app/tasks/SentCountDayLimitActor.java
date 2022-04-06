package tasks;

import akka.actor.AbstractActor;
import com.google.inject.Inject;
import services.database.dao.CampaignDAO;
import services.sms.ISMSApiService;

class SentCountDayLimitActor extends AbstractActor {

    private CampaignDAO campaignDAO;

    @Inject
    public SentCountDayLimitActor(CampaignDAO campaignDAO) {
        this.campaignDAO = campaignDAO;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().matchAny(o -> campaignDAO.resetDaySentCountForCallerIds()).build();
    }
}