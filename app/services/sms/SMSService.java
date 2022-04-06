package services.sms;

import com.google.inject.Inject;
import common.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.dao.CampaignDAO;
import services.database.dao.SettingsDAO;
import services.database.dao.UserDAO;
import services.database.model.*;

import java.util.*;

import static controllers.ListsController.CODE_LENGTH;

public class SMSService {

    final Logger logger = LoggerFactory.getLogger("access");

    private CampaignDAO campaignDAO;
    private UserDAO userDAO;
    private SettingsDAO settingsDAO;

    private ISMSApiService smsApiService;
    private TelnexSMSApiService telnexSMSApiService;

    private long DAY_SENT_LIMIT = 150;

    @Inject
    public SMSService(CampaignDAO campaignDAO, UserDAO userDAO,
                      SettingsDAO settingsDAO, ISMSApiService smsApiService,
                      TelnexSMSApiService telnexSMSApiService) {
        this.campaignDAO = campaignDAO;
        this.userDAO = userDAO;
        this.settingsDAO = settingsDAO;
        this.smsApiService = smsApiService;
        this.telnexSMSApiService = telnexSMSApiService;
    }

    public void startCampaign(Campaign campaign) {
        Campaign dbCampaign = campaignDAO.getCampaignById(campaign.getId());
        if (dbCampaign.getStatus() == 0) {
            new Thread(() -> startCampaignBackground(campaign)).start();
        }
    }

    private void startCampaignBackground(Campaign campaign) {


        Map<Integer, Set<Long>> dncPhonesMap =
                campaignDAO.generateDNCPhonesMap(campaignDAO.getDNCListByName(DNCList.MAIN));

        Map<Integer, Set<Long>> dncMasterPhonesMap = new HashMap();
        if (campaign.isFilterDNC()) {
            dncMasterPhonesMap = campaignDAO.generateMasterDNCPhonesMap();
        }

        User user = userDAO.getUserById(campaign.getUserId());

        Float price = settingsDAO.getOutboundMessagePrice(campaign.getUserId(), user.getResellerId());
        Float basePrice = settingsDAO.getOutboundMessagePrice(user.getResellerId(), User.DEFAULT_RESELLER_ID);


        List<Long> heldPhones;
        int countHeldPhones = 0;
        int countTotalSent = 1;
        List<Long> allListsPhones = new ArrayList<>();
        List<CampaignList> allLists = campaignDAO.getListsByCampaignId(campaign.getId());
        for (CampaignList list : allLists) {
            allListsPhones.addAll(campaignDAO.getAllNotSentPhoneFromList(list.getId()));
        }
        Collections.shuffle(allListsPhones);
        heldPhones = new ArrayList<>(allListsPhones.subList(0, (allListsPhones.size() / 2)));
        logger.info("Starting camping : " + campaign.getId());
        logger.info("Total sms to be send : " + allListsPhones.size());

        while (true) {
            List<CampaignList> lists = campaignDAO.getListsByCampaignId(campaign.getId());
            List<Phone> phones = campaignDAO.getPhonesByCampaignId(campaign.getId());

            if (user.isDisabled()) {
                campaignDAO.updateCampaignErrorStatusById(campaign.getId(), "Your account has been disabled, contact support.");
                campaignDAO.updateCampaignStatusById(campaign.getId(), 0);
                return;
            }

            if (lists.size() == 0) {
                campaignDAO.updateCampaignErrorStatusById(campaign.getId(), "No list is attached");
                campaignDAO.updateCampaignStatusById(campaign.getId(), 0);
                return;
            }

            if (phones.size() == 0) {
                campaignDAO.updateCampaignErrorStatusById(campaign.getId(), "Caller IDs were not selected");
                campaignDAO.updateCampaignStatusById(campaign.getId(), 0);
                return;
            }

            for (Phone fromPhone : phones) {
                boolean allListsEmpty = true;

                if (!checkPhoneDaySentLimit(fromPhone)) {
                    if (!checkAllPhonesDaySentLimit(phones)) {
                        campaignDAO.updateCampaignErrorStatusById(campaign.getId(), "Caller IDs exceeded days sent count limit");
                        campaignDAO.updateCampaignStatusById(campaign.getId(), 0);
                        return;
                    }

                    continue;
                }

                for (CampaignList list : lists) {
                    Campaign dbCampaign = campaignDAO.getCampaignById(campaign.getId());

                    if (dbCampaign.getStatus() == 0) {
                        return;
                    }

                    user = userDAO.getUserById(campaign.getUserId());
                    if (user != null && user.getBalance() < price) {
                        campaignDAO.updateCampaignErrorStatusById(campaign.getId(), "Your balance is too low");
                        campaignDAO.updateCampaignStatusById(campaign.getId(), 0);

                        return;
                    }

                    CampaignListItem toPhone = campaignDAO.getNotSentPhoneFromListAndUpdateStatus(list.getId());

                    if (toPhone != null) {

                        allListsEmpty = false;

                        Long toPhoneValue = Utils.formatPhone(toPhone.getPhone());
                        int code = Integer.parseInt(toPhoneValue.toString().substring(0, CODE_LENGTH));
                        if ((dncPhonesMap.get(code) != null && dncPhonesMap.get(code).contains(toPhoneValue)) ||
                                (dncMasterPhonesMap.get(code) != null && dncMasterPhonesMap.get(code).contains(toPhoneValue))) {
                            campaignDAO.updateListItemSentStatusById(toPhone.getId(), true);
                            campaignDAO.incrementListDNCCountById(list.getId());
                            campaignDAO.incrementCampaignDNCCountById(campaign.getId());
                            campaignDAO.decrementCampaignLeadsCountById(campaign);

                            continue;
                        }

                        Long sentPhone = campaignDAO.getAndInsertSentPhone(toPhoneValue, getLastSentTime(user));
                        if (sentPhone != null || checkCarrierIgnore(user, toPhoneValue)) {
                            campaignDAO.updateListItemSentStatusById(toPhone.getId(), true);
                            campaignDAO.incrementListIgnoredCountById(list.getId());
                            campaignDAO.incrementCampaignIgnoredCountById(campaign.getId());
                            campaignDAO.decrementCampaignLeadsCountById(campaign);

                            continue;
                        }
                        String errorMessage = null;

                        if (heldPhones.contains(toPhone.getPhone()) && countHeldPhones / (double) countTotalSent <= 0.25) {
                            countHeldPhones++;
                        } else {
                            if(campaign.getCampaigntype().equals("SMS")){
                                errorMessage = smsApiService.sendSMS(
                                        toPhone.getPhone(), fromPhone.getPhone(),
                                        campaign.getFormattedMessage(toPhone.getData(), toPhone.getData2()),
                                        campaign.getUserId());
                            }else if(campaign.getCampaigntype().equals("VOICE")){
                                errorMessage = smsApiService.sendVoice(
                                        toPhone.getPhone(), fromPhone.getPhone(),
                                        campaign.getFormattedMessage(toPhone.getData(), toPhone.getData2()),
                                        campaign.getUserId());
                                System.out.println("sending Voice campaign");
                            }else{
                                errorMessage = null;
                                System.out.println("sending SMS and Voice campaign");

                            }

                        }

                        if (errorMessage == null) {
                            countTotalSent++;
                            campaignDAO.incrementCampaignSentCountById(campaign);
                            campaignDAO.incrementListSentCountById(list);

                            fromPhone.setLastSentDate(System.currentTimeMillis());
                            campaignDAO.incrementPhoneSentCountById(fromPhone);
                            campaignDAO.incrementPhoneDaySentCountById(fromPhone);

                            Chat chat = campaignDAO.updateChatMessage(null, campaign,
                                    toPhone.getPhone(), fromPhone.getPhone(),
                                    campaign.getFormattedMessage(toPhone.getData(), toPhone.getData2()),
                                    campaign.getUserId(),
                                    false, System.currentTimeMillis(),
                                    false, false);
                            updateChatCarrier(chat, user, toPhoneValue);

                            if (price != null) {
                                userDAO.changeUserBalanceAndUpdateResellerBalance(Transaction.OUTBOUND_MESSAGE, campaign.getUserId(), -price, -basePrice);
                                userDAO.updateCountTransaction(Transaction.OUTBOUND_MESSAGE, campaign.getUserId(), -price, -basePrice, "count: ");
                            }
                        } else {
                            campaignDAO.incrementCampaignErrorsCountById(campaign);
                            campaignDAO.incrementListErrorsById(list.getId());
                            //campaignDAO.updateCampaignErrorStatusById(campaign.getId(), errorMessage);
                            campaignDAO.insertCampaignError(
                                    new CampaignError(campaign.getId(), toPhoneValue, errorMessage, System.currentTimeMillis()));
                        }
                        campaignDAO.decrementCampaignLeadsCountById(campaign);
                    }

                }

                if (allListsEmpty) {
                    Date date = new Date();
                    logger.info(System.currentTimeMillis() + " -- " + date.toString() + "---> hsms " + countHeldPhones + " countTotalSent: " + countTotalSent);
                    logger.info("nfim rate " + countHeldPhones / (double) allListsPhones.size() + " camping:" + campaign.getId());
                    campaignDAO.updateCampaignStatusById(campaign.getId(), 0);
                    return;
                }
            }
        }
    }

    private void updateChatCarrier(Chat chat, User user, Long toPhoneValue) {
        if (chat.getCarrier() == null) {
            Setting setting = settingsDAO.getSettingByKey("chat.carrier.lookup_" + user.getId(), user.getId());
            if (setting != null && "1".equalsIgnoreCase(setting.getSval())) {
                String carrier = smsApiService.getCarrierInfo(toPhoneValue);
                if (carrier != null && carrier.length() > 0) {
                    Float lookupPrice = settingsDAO.getLookupPrice(user.getId(), user.getResellerId());
                    Float lookupBasePrice = settingsDAO.getLookupPrice(user.getResellerId(), User.DEFAULT_RESELLER_ID);

                    userDAO.changeUserBalanceAndUpdateResellerBalance(Transaction.CARRIER_LOOKUP, user.getId(), -lookupPrice, -lookupBasePrice);
                    userDAO.updateCountTransaction(Transaction.CARRIER_LOOKUP, user.getId(), -lookupPrice, -lookupBasePrice, "count: ");

                    userDAO.updateChatCarrierById(chat.getId(), chat.getUserId(), carrier);
                }
            }
        }
    }

    private boolean checkCarrierIgnore(User user, Long toPhoneValue) {
        Set<String> ignoreCarriers = new HashSet();

        List<String> carrierSettings = new LinkedList();
        carrierSettings.add("carrier.ignore.verizon_" + user.getId());
        carrierSettings.add("carrier.ignore.att_" + user.getId());
        carrierSettings.add("carrier.ignore.t-mobile_" + user.getId());
        carrierSettings.add("carrier.ignore.sprint_" + user.getId());

        for (String carrierSetting : carrierSettings) {
            Setting setting = settingsDAO.getSettingOrDefaultByKey(
                    carrierSetting, user.getId());

            if (setting != null && "1".equalsIgnoreCase(setting.getSval())) {
                ignoreCarriers.add(carrierSetting.split("_")[0].split("\\.")[2]);
            }
        }

        if (ignoreCarriers.size() > 0) {
            String carrier = smsApiService.getCarrierInfo(toPhoneValue);

            Float lookupPrice = settingsDAO.getLookupPrice(user.getId(), user.getResellerId());
            Float lookupBasePrice = settingsDAO.getLookupPrice(user.getResellerId(), User.DEFAULT_RESELLER_ID);

            userDAO.changeUserBalanceAndUpdateResellerBalance(Transaction.CARRIER_LOOKUP, user.getId(), -lookupPrice, -lookupBasePrice);
            userDAO.updateCountTransaction(Transaction.CARRIER_LOOKUP, user.getId(), -lookupPrice, -lookupBasePrice, "count: ");

            for (String ignoreCarrier : ignoreCarriers) {
                if (carrier.toLowerCase().contains(ignoreCarrier)) {
                    return true;
                }
            }
        }

        return false;
    }

    private long getLastSentTime(User user) {
        long time = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000;
        Setting setting = settingsDAO.getSettingOrDefaultByKey(
                "master.ignore_" + user.getId(), user.getId());

        if (setting != null && "1".equalsIgnoreCase(setting.getSkey())) {
            time = 0L;
        }

        return time;
    }

    private boolean checkAllPhonesDaySentLimit(List<Phone> phones) {
        for (Phone phone : phones) {
            if (checkPhoneDaySentLimit(phone)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkPhoneDaySentLimit(Phone fromPhone) {
        Phone dbPhone = campaignDAO.getPhoneById(fromPhone.getId());
        return dbPhone.getDaySentCount() < DAY_SENT_LIMIT || dbPhone.isTollFree();
    }

    public String sendTestSMS(Long toPhone, String message,
                              String data, String data2,
                              Long fromPhone,
                              User user,
                              Campaign campaign) {
        String errorMessage = smsApiService.sendSMS(
                toPhone, fromPhone, message != null ? message : campaign.getFormattedMessage(data, data2), user.getId());
        if (errorMessage == null && campaign != null) {
            campaignDAO.updateChatMessage(null, campaign,
                    toPhone, fromPhone,
                    campaign.getFormattedMessage(data, data2), user.getId(),
                    false, System.currentTimeMillis(), true, true);
        }

        return errorMessage;
    }

}
