package tasks;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import services.database.dao.CampaignDAO;
import services.database.model.Campaign;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatHistoryTask {

    private CampaignDAO campaignDAO;
    private ActorRef chatHistoryActor;
    private ActorSystem actorSystem;
    private ExecutionContext executionContext;

    @Inject
    public ChatHistoryTask(@Named("chat-history-actor") ActorRef chatHistoryActor,
                           ActorSystem actorSystem,
                           ExecutionContext executionContext,
                           CampaignDAO campaignDAO) {
        this.chatHistoryActor = chatHistoryActor;
        this.actorSystem = actorSystem;
        this.executionContext = executionContext;
        this.campaignDAO = campaignDAO;

        this.initialize();
    }

    private void initialize() {
        /*List<Campaign> campaigns = campaignDAO.getAllCampaigns();
        for (Campaign campaign: campaigns) {
            campaignDAO.updateLeadsCount(campaign);
        }*/

        /*actorSystem.scheduler().schedule(
            Duration.create(0, TimeUnit.SECONDS),
            Duration.create(10, TimeUnit.SECONDS),
            chatHistoryActor,
            "Wake up, Neo...",
            executionContext,
            ActorRef.noSender());*/
    }

}
