package tasks;

import akka.actor.AbstractActor;
import com.google.inject.Inject;
import common.Utils;
import play.libs.Json;
import services.database.dao.CampaignDAO;
import services.database.dao.SettingsDAO;
import services.database.dao.UserDAO;
import services.database.model.*;
import services.sms.ISMSApiService;

import java.util.*;

class CallerIDRemoveActor extends AbstractActor {

    private CampaignDAO campaignDAO;
    private UserDAO userDAO;
    private SettingsDAO settingsDAO;
    private ISMSApiService smsApiService;

    private Map<Long, Long> candidateForRemoving = new HashMap();

    @Inject
    public CallerIDRemoveActor(CampaignDAO campaignDAO,
                               UserDAO userDAO,
                               SettingsDAO settingsDAO,
                               ISMSApiService smsApiService) {
        this.campaignDAO = campaignDAO;
        this.userDAO = userDAO;
        this.settingsDAO = settingsDAO;
        this.smsApiService = smsApiService;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().matchAny(o -> removeBadCallerIds()).build();
    }

    private void removeBadCallerIds() {
        Setting unansweredMessageCountSetting = settingsDAO.getSettingByKey("unanswered.messages.count", User.DEFAULT_RESELLER_ID);
        Setting unansweredMessageDelaySetting = settingsDAO.getSettingByKey("unanswered.messages.time", User.DEFAULT_RESELLER_ID);

        Long count = unansweredMessageCountSetting.getSvalAsLong();
        Long delay = unansweredMessageDelaySetting.getSvalAsLong();

        System.out.println("Removing unused phones settings: count -> " + count + ", delay -> " + delay);

        if (count != null && delay != null) {
            List<Phone> phones = campaignDAO.getPhonesBySentCountWithoutIncomeMessages(count);
            System.out.println("Preparing unused phone for removing statistics: count -> " + phones.size());

            for (Phone phone : phones) {
                if (phone.getNote() != null &&
                        (phone.getNote().contains("System") || phone.getNote().contains("MMD"))) {
                    continue;
                }

                if (!candidateForRemoving.containsKey(phone.getId())) {
                    System.out.println("Preparing unused phone for removing: +" + phone.getPhone());
                    candidateForRemoving.put(phone.getId(), System.currentTimeMillis());
                }
            }

            Iterator<Map.Entry<Long, Long>> it = candidateForRemoving.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Long, Long> candidate = it.next();

                if (candidate.getValue() < System.currentTimeMillis() - delay * 60 * 1000) {
                    Phone phoneForRemove = campaignDAO.getPhoneById(candidate.getKey());
                    if (phoneForRemove.getInboundCount() > 0) {
                        it.remove();
                        continue;
                    }

                    System.out.println("Removing unused phone +" + phoneForRemove.getPhone());
                    boolean result = removeCallerId(phoneForRemove);

                    if (result) {
                        it.remove();

                        AdminMessage message = new AdminMessage();
                        message.setUserId(phoneForRemove.getUserId());
                        message.setMessage("Your caller ID [+" + Utils.formatPhone(phoneForRemove.getPhone()) + "] was removed due to bad in/out ratio statistics.");
                        message.setDate(System.currentTimeMillis());

                        userDAO.insertAdminMessage(message);
                    }
                }
            }
        }
    }

    private boolean removeCallerId(Phone phone) {
        boolean result = smsApiService.releasePhone(phone.getPhone());
        if (result) {
            campaignDAO.removePhoneById(phone.getId());
            campaignDAO.removeSendersByPhoneAndUserId(phone.getPhone(), phone.getUserId());
        }

        return result;
    }
}