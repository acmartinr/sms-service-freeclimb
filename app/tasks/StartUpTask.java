package tasks;

import com.google.inject.Inject;
import services.database.dao.CampaignDAO;
import services.database.dao.UserDAO;
import services.database.model.Campaign;
import services.database.model.DNCList;
import services.database.model.User;
import services.sms.SMSService;

import java.util.List;

public class StartUpTask {

    private CampaignDAO campaignDAO;
    private UserDAO userDAO;
    private SMSService smsService;

    @Inject
    public StartUpTask(CampaignDAO campaignDAO,
                       UserDAO userDAO,
                       SMSService smsService) {
        this.campaignDAO = campaignDAO;
        this.smsService = smsService;
        this.userDAO = userDAO;

        this.initialize();
    }

    private void initialize() {
        campaignDAO.generateDNCPhonesMap(campaignDAO.getDNCListByName(DNCList.MAIN));

        new Thread(() ->
            campaignDAO.generateMasterDNCPhonesMap()
        ).start();

        List<User> users = userDAO.getAllUsers();
        for (User user: users) {
            if (!campaignDAO.checkUserChatTables(user)) {
                System.out.println("Migrating chats for user: " + user.getUsername());
                campaignDAO.migrateUserChatsData(user);
            }
        }

        List<Campaign> campaigns = campaignDAO.getStartedCampaigns();
        for (Campaign campaign: campaigns) {
            campaignDAO.updateCampaignStatusById(campaign.getId(), 0);
            smsService.startCampaign(campaign);
        }
    }

}
