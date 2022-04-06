package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import common.Utils;
import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import play.mvc.*;
import services.database.dao.CampaignDAO;
import services.database.dao.SettingsDAO;
import services.database.dao.UserDAO;
import services.database.model.*;
import services.sms.ISMSApiService;
import services.sms.SMSService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CampaignsController extends Controller {
    final Logger logger = LoggerFactory.getLogger("access");

    private CampaignDAO campaignDAO;
    private SettingsDAO settingsDAO;
    private UserDAO userDAO;

    private SMSService smsService;
    private ISMSApiService smsApiService;

    /*private String[] dncWords = new String[] {
            "stop", "unsubscribe", "off", "remove", "dont text", "don't text", "do not text", "no SMS",
            "wrong", "fuck", "fck", "fuk", "shit", "bitch", "delete", "spam", "scam", "invalid", "nope",
            "not a working number", "Opt out", "suck", "stp", "cease", "desist", "block", "harass",
            "federal", "ftc", "fcc", "dnc", "slut", "kill", "Dislike", "lose", "do not contact",
            "do not text", "do not call", "dont contact", "dont text", "dont call", "don't contact",
            "don't text", "don't call", "I dont", "I don't", "I DO NOT", "not interested", "fuck",
            "attorney", "lawyer", "years old", "unsolicited", "never", "no idea", "im not", "im am not",
            "i'm not", "🛑", "cancel", "Do not send", "No interest", "no thank", "scam", "illegal",
            "Spam", "Do not message me", "out of business", "stfu", "🖕🏻", "Block", "Opt-out",
            "Optout", "Opt out", "no idea", "retired", "leave me alone", "fucking", "go away", "UNSUB",
            "blocked", "nigga", "WTF", "Loose my number", "my info", "nigger", "nigga", "die", "Ass",
            "closed", "none", "Fbi"};*/

    private String[] exactlyDNCPhrases = new String[]{"no"};
    private String[] dncWordsInFirstAnswer = new String[]{"no"};

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(100);

    private String tempDirectoryPath;
    private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private DateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    @Inject
    public CampaignsController(CampaignDAO campaignDAO,
                               SettingsDAO settingsDAO,
                               UserDAO userDAO,
                               SMSService smsService,
                               ISMSApiService smsApiService) {
        this.campaignDAO = campaignDAO;
        this.settingsDAO = settingsDAO;
        this.userDAO = userDAO;

        this.smsService = smsService;
        this.smsApiService = smsApiService;
    }

    @With(LoggingAction.class)
    public Result messageStatus(Http.Request request) {
       // response
        /*
        SmsSid: SM2xxxxxx
SmsStatus: sent
MessageStatus: sent
To: +1512zzzyyyy
MessageSid: SM2xxxxxx
AccountSid: ACxxxxxxx
From: +1512xxxyyyy
ApiVersion: 2010-04-01
         */
        try {
            String data = request.body().asJson().toString();
            System.out.println("#messageStatus: " + data);
            logger.info("#messageStatus",data);
            //get from and to from response
            //if(response.status == 'call ended')
            /*
            smsApiService.sendSms(
                    toPhone.getPhone(), fromPhone.getPhone(),
                    campaign.getFormattedMessage(toPhone.getData(), toPhone.getData2()),
                    campaign.getUserId());


             */
//            smsApiService.outboundMessageStatus(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result statistics(Http.Request request) {
        CommonRequest commonRequest = Json.fromJson(request.body().asJson(), CommonRequest.class);

        return ok(Json.toJson(CommonResponse.OK(new Statistics(
                campaignDAO.getActiveCampaignsCountByUserId(commonRequest.getUserId()),
                campaignDAO.getTotalCampaignsCountByUserId(commonRequest.getUserId()),
                campaignDAO.getSMSSentCountByUserId(commonRequest.getUserId())
        ))));
    }

    @With(LoggingAction.class)
    public Result campaigns(Http.Request request) {
        CommonRequest commonRequest = Json.fromJson(request.body().asJson(), CommonRequest.class);

        User user = Utils.getCurrentUser(request, userDAO);
        if (user != null && user.getRole() == User.LIMITED) {
            commonRequest.setAgentUsername(user.getUsername());
        }

        List<Campaign> campaigns = campaignDAO.getCampaignsByRequest(commonRequest);
        for (Campaign campaign : campaigns) {
            campaign.setAgentPassword(null);
            campaign.setLeadsCount(campaignDAO.getCampaignLeadsCount(campaign));
        }

        return ok(Json.toJson(CommonResponse.OK(new CampaignsResponse(
                campaigns,
                campaignDAO.getCampaignsCountByRequest(commonRequest)
        ))));
    }

    @With(LoggingAction.class)
    public Result updateAgentCredentials(Http.Request request) {
        Campaign campaign = Json.fromJson(request.body().asJson(), Campaign.class);

        if (campaign.getAgentUsername() != null && campaign.getAgentUsername().length() > 0) {
            Campaign dbCampaign = campaignDAO.getCampaignByAgentUsername(campaign.getAgentUsername());

            if (dbCampaign != null && dbCampaign.getUserId() != campaign.getUserId()) {
                return ok(Json.toJson(CommonResponse.ERROR()));
            }
        }

        campaignDAO.updateCampaignAgentCredentials(campaign);

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result addCampaign(Http.Request request) {
        Campaign campaign = Json.fromJson(request.body().asJson(), Campaign.class);

        Campaign dbCampaign = campaignDAO.getCampaignByName(campaign.getName(), campaign.getUserId());
        if (dbCampaign != null && campaign.getId() != dbCampaign.getId()) {
            return ok(Json.toJson(CommonResponse.ERROR("name")));
        }

        if (campaign.getId() == 0) {
            campaign.setDate(System.currentTimeMillis());
            campaign.setSentCount(0);
            campaign.setLeadsCount(0);
            campaign.setStatus(0);

            campaignDAO.insertCampaign(campaign);
            campaignDAO.updateCampaignPhones(campaign);
        } else {
            campaign.setStatus(0);
            campaignDAO.updateCampaign(campaign);
            campaignDAO.updateCampaignPhones(campaign);
        }

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result removeCampaign(Long campaignId) {
        campaignDAO.removeCampaignById(campaignId);
        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result listsForCampaign(Long campaignId) {
        return ok(Json.toJson(CommonResponse.OK(campaignDAO.getListsByCampaignId(campaignId))));
    }

    @With(LoggingAction.class)
    public Result getCampaignPhones(Long campaignId) {
        return ok(Json.toJson(CommonResponse.OK(campaignDAO.getPhonesByCampaignId(campaignId))));
    }

    @With(LoggingAction.class)
    public Result updateListsForCampaign(Http.Request request) {
        Campaign campaign = Json.fromJson(request.body().asJson(), Campaign.class);
        campaignDAO.updateListsForCampaign(campaign);

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result startCampaign(Http.Request request) {
        Campaign campaign = Json.fromJson(request.body().asJson(), Campaign.class);
        campaignDAO.updateCampaignErrorStatusById(campaign.getId(), null);
        smsService.startCampaign(campaign);
        campaignDAO.updateCampaignStatusById(campaign.getId(), 1);

        userDAO.updateLastCampaignDate(campaign.getUserId(), System.currentTimeMillis());

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result sendTestSMS(Http.Request request) {
        SendTestSMSRequest sendTestSMSRequest = Json.fromJson(request.body().asJson(), SendTestSMSRequest.class);

        User currentUser = Utils.getCurrentUser(request, userDAO);
        if (sendTestSMSRequest.getMessage() != null) {
            return sendTestMessage(currentUser, null, sendTestSMSRequest);
        } else {
            Campaign campaign = campaignDAO.getCampaignById(sendTestSMSRequest.getCampaignId());
            return sendTestMessage(currentUser, campaign, sendTestSMSRequest);
        }
    }

    @With(LoggingAction.class)
    private Result sendTestMessage(User currentUser, Campaign campaign, SendTestSMSRequest sendTestSMSRequest) {
        Float price = settingsDAO.getOutboundMessagePrice(
                currentUser.getId(),
                currentUser.getResellerId());
        Float basePrice = settingsDAO.getOutboundMessagePrice(currentUser.getResellerId(), User.DEFAULT_RESELLER_ID);

        User user = userDAO.getUserById(currentUser.getId());
        if (price != null && user.getBalance() < price) {
            return ok(Json.toJson(CommonResponse.ERROR("balance")));
        }

        String errorMessage = smsService.sendTestSMS(
                sendTestSMSRequest.getPhoneTo(),
                sendTestSMSRequest.getMessage(),
                sendTestSMSRequest.getData(),
                sendTestSMSRequest.getData2(),
                sendTestSMSRequest.getPhoneFrom(),
                currentUser,
                campaign);
        if (errorMessage != null) {
            return ok(Json.toJson(CommonResponse.ERROR(errorMessage)));
        }

        if (price != null) {
            userDAO.changeUserBalanceAndUpdateResellerBalance(Transaction.OUTBOUND_MESSAGE, currentUser.getId(), -price, -basePrice);
            userDAO.updateCountTransaction(Transaction.OUTBOUND_MESSAGE, currentUser.getId(), -price, -basePrice, "count: ");
        }

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result stopCampaign(Http.Request request) {
        Campaign campaign = Json.fromJson(request.body().asJson(), Campaign.class);
        campaignDAO.updateCampaignStatusById(campaign.getId(), 0);

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result removedPhones(Http.Request request) {
        CommonRequest commonRequest = Json.fromJson(request.body().asJson(), CommonRequest.class);

        List<AdminMessage> messages = userDAO.getUserAdminMessagesAboutRemovedCallerIdByRequest(commonRequest);
        int count = userDAO.getUserAdminMessagesAboutRemovedCallerIdCountByRequest(commonRequest);

        return ok(Json.toJson(CommonResponse.OK(new PhonesResponse(
                convertAdminMessageToPhones(messages),
                count
        ))));
    }

    @With(LoggingAction.class)
    public Result exportRemovedPhones(Http.Request request) throws Exception {
        File listFile = File.createTempFile("temp", "csv");
        FileWriter writer = new FileWriter(listFile);

        writer.write("phone,user,date\n");

        CommonRequest commonRequest = new CommonRequest();
        commonRequest.setPage(0);
        commonRequest.setLimit(10000);

        List<AdminMessage> messages = userDAO.getUserAdminMessagesAboutRemovedCallerIdByRequest(commonRequest);
        List<Phone> phones = convertAdminMessageToPhones(messages);

        for (Phone phone : phones) {
            writer.write("+" + Utils.formatPhone(phone.getPhone()));
            writer.write(",");
            writer.write(phone.getNote());
            writer.write(",");
            writer.write(dateTimeFormat.format(new Date(phone.getDate())));
            writer.write("\n");
        }

        writer.flush();
        writer.close();

        return ok(listFile).withHeader(
                "Content-disposition", "attachment; filename=" +
                        "removed_caller_ids.csv").as("text/csv");
    }

    private List<Phone> convertAdminMessageToPhones(List<AdminMessage> messages) {
        List<Phone> phones = new LinkedList();

        for (AdminMessage message : messages) {
            Phone phone = new Phone();
            phone.setId(message.getId());
            phone.setDate(message.getDate());

            User user = userDAO.getUserById(message.getUserId());
            if (user != null) {
                phone.setNote(user.getFullName());
            }

            String strPhones = message.getMessage().split("\\+")[1].split("]")[0];
            phone.setPhone(Long.parseLong(strPhones));

            phones.add(phone);
        }

        return phones;
    }

    @With(LoggingAction.class)
    public Result phones(Http.Request request) {
        CommonRequest commonRequest = Json.fromJson(request.body().asJson(), CommonRequest.class);

        User user = Utils.getCurrentUser(request, userDAO);
        if (user != null && user.getRole() == User.LIMITED) {
            Campaign campaign = campaignDAO.getCampaignByAgentUsername(user.getUsername());
            if (campaign != null) {
                List<Phone> phones = campaignDAO.getPhonesByCampaignId(campaign.getId());

                return ok(Json.toJson(CommonResponse.OK(new PhonesResponse(
                        phones,
                        phones.size()
                ))));
            }
        }

        return ok(Json.toJson(CommonResponse.OK(new PhonesResponse(
                campaignDAO.getPhonesByRequest(commonRequest),
                campaignDAO.getPhonesCountByRequest(commonRequest)
        ))));
    }

    @With(LoggingAction.class)
    public Result updatePhone(Http.Request request) {
        Phone phone = Json.fromJson(request.body().asJson(), Phone.class);
        if (phone.getForwarding() > 0) {
            phone.setForwarding(Utils.formatPhone(phone.getForwarding()));
        }

        campaignDAO.updatePhone(phone);

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result updatePhonesForwarding(Http.Request request) {
        Phone phone = Json.fromJson(request.body().asJson(), Phone.class);
        if (phone.getForwarding() > 0) {
            phone.setForwarding(Utils.formatPhone(phone.getForwarding()));
        }

        campaignDAO.updatePhonesForwardingByUserId(phone);

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result searchPhones(Http.Request request) {
        SearchPhonesRequest searchPhonesRequest = Json.fromJson(request.body().asJson(), SearchPhonesRequest.class);

        Long userId = Utils.getCurrentUserId(request, userDAO, campaignDAO);

        if (userId == null) {
            return forbidden();
        }

        List<Phone> currentUserPhones = campaignDAO.getPhonesByUserId(userId);
        List<String> strCurrentPhone = new ArrayList<>();
        if (currentUserPhones != null && !currentUserPhones.isEmpty()) {
            strCurrentPhone.addAll(currentUserPhones.stream().map(phone -> String.valueOf(phone.getPhone())).collect(Collectors.toList()));
        }

        return ok(Json.toJson(CommonResponse.OK(smsApiService.searchPhones(searchPhonesRequest.getAreaCodes(), strCurrentPhone))));
    }

    @With(LoggingAction.class)
    public Result buyPhones(Http.Request request) {
        BuyPhonesRequest buyPhonesRequest = Json.fromJson(request.body().asJson(), BuyPhonesRequest.class);

        User user = userDAO.getUserById(buyPhonesRequest.getUserId());

        List<Phone> currentPhones = campaignDAO.getAllPhones();

        // Filtering if the phone already exist in DB
        if (currentPhones != null && !currentPhones.isEmpty()) {
            List<String> strCurrentPhones = currentPhones.stream().map(phone -> String.valueOf(phone.getPhone())).collect(Collectors.toList());
            buyPhonesRequest.setPhones(buyPhonesRequest.getPhones().stream().filter(phone_ -> !strCurrentPhones.contains(phone_.substring(1))).collect(Collectors.toList()));
        }

        if (user.getRole() == User.REGULAR) {
            Integer limit = settingsDAO.getPhonesLimit(user.getResellerId());
            int phonesCount = campaignDAO.getPhonesCountByUserId(buyPhonesRequest.getUserId());

            if (limit != null && limit < phonesCount + buyPhonesRequest.getPhones().size()) {
                return ok(Json.toJson(CommonResponse.ERROR("limit")));
            }
        }

        Float phonePrice = settingsDAO.getPhonePrice(user.getId(), user.getResellerId());
        Float basePhonePrice = settingsDAO.getPhonePrice(user.getResellerId(), User.DEFAULT_RESELLER_ID);

        if (phonePrice != null && user.getBalance() < phonePrice * buyPhonesRequest.getPhones().size()) {
            return ok(Json.toJson(CommonResponse.ERROR("balance")));
        }

        List<String> boughtPhones = smsApiService.buyPhones(buyPhonesRequest.getPhones());
        if (buyPhonesRequest.getPhones().size() > 0) {
            buyPhonesRequest.setPhones(buyPhonesRequest.getPhones());
            campaignDAO.insertPhones(buyPhonesRequest);

            if (phonePrice != null) {
                float amount = -phonePrice * buyPhonesRequest.getPhones().size();
                float baseAmount = -basePhonePrice * buyPhonesRequest.getPhones().size();

                userDAO.updateCountTransaction(Transaction.PHONE_BUY, user.getId(), amount,
                        baseAmount, "phones count: ", buyPhonesRequest.getPhones().size());
                userDAO.changeUserBalanceAndUpdateResellerBalance(Transaction.PHONE_BUY, user.getId(), amount, baseAmount);
            }
        }

        return ok(Json.toJson(buyPhonesRequest.getPhones().size() > 0 ? CommonResponse.OK() : CommonResponse.ERROR()));
    }

    @With(LoggingAction.class)
    public Result removePhone(Long phoneId) {
        Phone phone = campaignDAO.getPhoneById(phoneId);
        if (phone != null) {
            boolean result = smsApiService.releasePhone(phone.getPhone());
            //if (result) {
            campaignDAO.removePhoneById(phoneId);
            campaignDAO.removeSendersByPhoneAndUserId(phone.getPhone(), phone.getUserId());
            //}
        }

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result freePhones(Http.Request request) {
        CommonRequest commonRequest = Json.fromJson(request.body().asJson(), CommonRequest.class);
        return ok(Json.toJson(CommonResponse.OK(campaignDAO.getFreePhonesByUserId(commonRequest.getUserId()))));
    }

    @With(LoggingAction.class)
    public Result chats(Http.Request request) {
        CommonRequest commonRequest = Json.fromJson(request.body().asJson(), CommonRequest.class);

        User user = Utils.getCurrentUser(request, userDAO);
        if (user == null) {
            return forbidden();
        }

        commonRequest.setUserId(user.getId());

        if (user.getRole() == User.LIMITED) {
            Campaign campaign = campaignDAO.getCampaignByAgentUsername(user.getUsername());
            if (campaign != null) {
                commonRequest.setCampaignId(campaign.getId());
                commonRequest.setUserId(campaign.getUserId());
            }
        }

        List<Chat> chats = campaignDAO.getVisibleChatsByRequest(commonRequest);
        Integer count = campaignDAO.getVisibleChatsCountByRequest(commonRequest);

        return ok(Json.toJson(CommonResponse.OK(new ChatsResponse(chats, count))));
    }

    @With(LoggingAction.class)
    public Result chatMessages(Http.Request request, Long id) {
        Long userId = Utils.getCurrentUserId(request, userDAO, campaignDAO);

        if (userId == null) {
            return forbidden();
        }

        campaignDAO.updateChatReadById(id, userId);
        campaignDAO.updateMessagesRedByChatId(id, userId);

        return ok(Json.toJson(CommonResponse.OK(campaignDAO.getChatsMessagesByChatId(id, userId))));
    }

    @With(LoggingAction.class)
    public Result sendMessage(Http.Request request) {
        Long userId = Utils.getCurrentUserId(request, userDAO, campaignDAO);

        if (userId == null) {
            return forbidden();
        }

        User user = userDAO.getUserById(userId);

        SendSmsRequest sendSmsRequest = Json.fromJson(request.body().asJson(), SendSmsRequest.class);
        Chat chat = campaignDAO.getChatById(sendSmsRequest.getChatId(), user.getId());

        Float price = settingsDAO.getOutboundMessagePrice(
                chat.getUserId(),
                Objects.requireNonNull(Utils.getCurrentUser(request, userDAO)).getResellerId());

        Float basePrice = settingsDAO.getOutboundMessagePrice(
                chat.getUserId(),
                User.DEFAULT_RESELLER_ID);

        if (price != null && user.getBalance() < price) {
            return ok(Json.toJson(CommonResponse.ERROR("balance")));
        }

        String errorMessage = smsApiService.sendSMS(chat.getPhoneTo(), sendSmsRequest.getPhoneFrom(), sendSmsRequest.getMessage(), chat.getUserId());
        if (errorMessage == null) {
            campaignDAO.updateChatMessage(chat, null,
                    chat.getPhoneTo(), sendSmsRequest.getPhoneFrom(),
                    sendSmsRequest.getMessage(), chat.getUserId(),
                    false, System.currentTimeMillis(), true, false);

            if (price != null) {
                userDAO.changeUserBalanceAndUpdateResellerBalance(Transaction.OUTBOUND_MESSAGE, chat.getUserId(), -price, -basePrice);
                userDAO.updateCountTransaction(Transaction.OUTBOUND_MESSAGE, chat.getUserId(), -price, -basePrice, "count: ");
            }
        }

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result parseAndSaveInboundMessage(Http.Request request) {
        JsonNode requestNode = request.body().asJson();
        logger.info(requestNode.toString());

        String messageSid = requestNode.get("sms_id").asText();
        String text = requestNode.get("body").asText();

//        SMSApiMessage message = new SMSApiMessage(text, System.currentTimeMillis(), messageSid);

        long phoneFrom = Long.parseLong(requestNode.get("from").asText().replace("+", ""));
        long phoneTo = Long.parseLong(requestNode.get("to").asText().replace("+", ""));

//        handleIncomeMessage(message, phoneFrom, phoneTo);

        return ok();
    }

    @With(LoggingAction.class)
    public Result saveInboundMessage(Http.Request request) {
        Map<String, String[]> data = request.body().asFormUrlEncoded();
        try {
            Chat message = smsApiService.parseInboundMessage(data);
            logger.info("From:" + message.getPhoneFrom() + "   to: " + message.getPhoneTo() + "   body:" + message.getLastMessage());
            handleIncomeMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return ok();
    }

    private void handleIncomeMessage(Chat incomeChatMessage) {
        List<Chat> chats = campaignDAO.getChatsByToNumber(incomeChatMessage.getPhoneFrom(), userDAO.getAllUsers());
        if (chats.size() == 0) {
            Phone phone = campaignDAO.getPhoneByNumber(incomeChatMessage.getPhoneTo());
            if (phone != null) {
                Chat chat = campaignDAO.updateChatMessage(null, null,
                        incomeChatMessage.getPhoneFrom(), incomeChatMessage.getPhoneTo(),
                        "** Related campaign not found **",
                        phone.getUserId(),
                        false, System.currentTimeMillis(),
                        false, false);
                chats.add(chat);
            }
        }

        for (Chat chat : chats) {
            ChatMessage lastMessage = campaignDAO.getLastChatMessageByChatId(chat.getId(), chat.getUserId());
            Phone phone = campaignDAO.getPhoneByUserIdAndPhone(chat.getUserId(), incomeChatMessage.getPhoneTo());

            ChatMessage lastOutcomeMessage = campaignDAO.getLastOutcomeMessageByChatId(chat.getId(), chat.getUserId());
            if (phone != null && lastOutcomeMessage != null && lastOutcomeMessage.getPhoneFrom() == incomeChatMessage.getPhoneTo()) {
                boolean updated = campaignDAO.updateExternalChatMessage(chat, incomeChatMessage.getLastSmsApiMessage());
                if (updated) {
                    campaignDAO.updateHasInboundForChat(chat.getId(), chat.getUserId(), true);
                    campaignDAO.incrementPhoneInboundCountById(phone.getId());

                    User user = userDAO.getUserById(chat.getUserId());

                    Float price = settingsDAO.getInboundMessagePrice(chat.getUserId(), user.getResellerId());
                    Float basePrice = settingsDAO.getInboundMessagePrice(user.getResellerId(), User.DEFAULT_RESELLER_ID);

                    if (price != null) {
                        userDAO.changeUserBalanceAndUpdateResellerBalance(Transaction.INBOUND_MESSAGE, chat.getUserId(), -price, -basePrice);
                        userDAO.updateCountTransaction(Transaction.INBOUND_MESSAGE, chat.getUserId(), -price, -basePrice, "count: ");
                    }

                    if (containsDNCWords(incomeChatMessage.getLastMessage())) {
                        campaignDAO.incrementListReceivedCountById(chat, incomeChatMessage.getPhoneFrom());
                        campaignDAO.banChat(chat);
                    } else if (lastMessage != null && !lastMessage.isManual() && !lastMessage.isInbound()) {
                        if (containsDNCWordsForFirstAnswer(incomeChatMessage.getLastMessage())) {
                            campaignDAO.incrementListReceivedCountById(chat, incomeChatMessage.getPhoneFrom());
                            campaignDAO.banChat(chat);
                        } else {
                            campaignDAO.incrementListReceivedCountById(chat, incomeChatMessage.getPhoneFrom());
                            handleIncomeMessagesRules(chat, incomeChatMessage.getLastMessage(), incomeChatMessage.getPhoneTo(), user);

                            List<AutoReply> autoReplies = userDAO.getAutoRepliesByUserId(chat.getUserId());
                            for (AutoReply autoReply : autoReplies) {
                                if (containsKeywords(incomeChatMessage.getLastMessage(), autoReply.getKeywords())) {
                                    scheduleAutoReplyMessage(chat, autoReply, incomeChatMessage.getPhoneTo());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleIncomeMessagesRules(Chat chat, String message, long phoneTo, User user) {
        if (chat.getUserId() == 12) { // Bridgeway marketing
            Float price = settingsDAO.getOutboundMessagePrice(chat.getUserId(), user.getResellerId());
            Float basePrice = settingsDAO.getOutboundMessagePrice(user.getResellerId(), User.DEFAULT_RESELLER_ID);

            if (price != null && user.getBalance() < price) {
                return;
            }

            String outboundMessage = message;
            outboundMessage =
                    outboundMessage + "; " +
//                            phoneTo + "; https://textalldata.com/login?redirect=chat&phoneFrom=" + phoneTo;
                            phoneTo + "; https://bizownercells.com/login?redirect=chat&phoneFrom=" + phoneTo;


            String errorMessage = smsApiService.sendSMS(17142906081L, 19495580935L, outboundMessage, chat.getUserId());
            if (errorMessage == null && price != null) {
                userDAO.changeUserBalanceAndUpdateResellerBalance(Transaction.OUTBOUND_MESSAGE, chat.getUserId(), -price, -basePrice);
                userDAO.updateCountTransaction(Transaction.OUTBOUND_MESSAGE, chat.getUserId(), -price, -basePrice, "count: ");
            }
        }
    }

    private void scheduleAutoReplyMessage(Chat chat, AutoReply autoReply, long phone) {
        scheduler.schedule(() -> {
            User user = userDAO.getUserById(chat.getUserId());

            Float price = settingsDAO.getOutboundMessagePrice(chat.getUserId(), user.getResellerId());
            Float basePrice = settingsDAO.getOutboundMessagePrice(user.getResellerId(), User.DEFAULT_RESELLER_ID);

            if (price != null && user.getBalance() < price) {
                return;
            }

            String errorMessage = smsApiService.sendSMS(chat.getPhoneTo(), phone, autoReply.getMessage(), chat.getUserId());
            if (errorMessage == null) {
                campaignDAO.updateChatMessage(chat, null,
                        chat.getPhoneTo(), phone,
                        autoReply.getMessage(), chat.getUserId(),
                        false, System.currentTimeMillis(),
                        true, false);

                if (price != null) {
                    userDAO.changeUserBalanceAndUpdateResellerBalance(Transaction.OUTBOUND_MESSAGE, chat.getUserId(), -price, -basePrice);
                    userDAO.updateCountTransaction(Transaction.OUTBOUND_MESSAGE, chat.getUserId(), -price, -basePrice, "count: ");
                }
            }
        }, 10, TimeUnit.SECONDS);
    }

    private boolean containsDNCWords(String body) {
        List<String> dncWords = settingsDAO.getDNCWords();
        for (String word : dncWords) {
            if (body.toLowerCase().matches(".*\\b" + word.toLowerCase() + "\\b.*")) {
                return true;
            }
        }

        for (String word : exactlyDNCPhrases) {
            if (body.equalsIgnoreCase(word)) {
                return true;
            }
        }

        return false;
    }

    private boolean containsDNCWordsForFirstAnswer(String body) {
        for (String word : dncWordsInFirstAnswer) {
            if (body.toLowerCase().matches(".*\\b" + word.toLowerCase() + "\\b.*") ||
                    body.toLowerCase().matches(word.toLowerCase() + "\\b.*") ||
                    body.toLowerCase().matches(".*\\b" + word.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    private boolean containsKeywords(String body, String keywords) {
        String[] keywordsArray = keywords.split(",");

        for (String word : keywordsArray) {
            word = word.trim().toLowerCase();

            if ("*".equals(word)) {
                return true;
            }

            //if (body.toLowerCase().matches(".*\\b" + word + "\\b.*")) {
            if (body.toLowerCase().contains(word)) {
                return true;
            }
        }

        return false;
    }

    @With(LoggingAction.class)
    public Result getCampaignErrors(long id) {
        return ok(Json.toJson(CommonResponse.OK(campaignDAO.getCampaignErrorsByCampaignId(id))));
    }

    @With(LoggingAction.class)
    public Result resetErrorStatus(Long id) {
        campaignDAO.updateCampaignErrorStatusById(id, null);
        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result removeChat(Http.Request request, Long chatId) {
        Long userId = Utils.getCurrentUserId(request, userDAO, campaignDAO);

        if (userId == null) {
            return forbidden();
        }

        campaignDAO.removeChatById(chatId, userId);
        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result exportUsersToFile(Http.Request request) throws Exception {
        User currentUser = Utils.getCurrentUser(request, userDAO);

        List<Chat> chats = campaignDAO.getVisibleChatsWithLastInboundMessagesByUserId((int) currentUser.getId());
        String fileName = writeChatsToFile(chats);

        return ok(Json.toJson(CommonResponse.OK(fileName)));
    }

    private String writeChatsToFile(List<Chat> chats) throws Exception {
        File file = File.createTempFile("chats", "csv");
        FileWriter fileWriter = new FileWriter(file);

        fileWriter.write("phone,callerID,incoming message,incoming message date\n");
        for (Chat chat : chats) {
            fileWriter.write(
                    chat.getPhoneTo() + "," +
                            chat.getPhoneFrom() + "," +
                            formatString(chat.getLastMessage()) + "," +
                            formatDate(chat.getLastDate()) + "\n");
        }

        fileWriter.flush();
        fileWriter.close();

        return file.getName();
    }

    private String formatString(String value) {
        return value == null ? "" : "\"" + value.replace("\"", "'") + "\"";
    }

    private String formatDate(long date) {
        return date == 0 ? "" : dateFormat.format(new Date(date));
    }

    @With(LoggingAction.class)
    public Result downloadExportedUsersFile(Http.Request request, String path) throws Exception {
        if (tempDirectoryPath == null) {
            File file = File.createTempFile("temp", "temp");
            tempDirectoryPath = file.getParentFile().getAbsolutePath();
        }

        File file = new File(tempDirectoryPath, path);
        String fileName = "chats_" + dateFormat.format(new Date()) + ".csv";

        return ok(new FileInputStream(file)).as("text/csv").
                withHeader("Content-disposition", "attachment; filename=" + fileName);

    }

    @With(LoggingAction.class)
    public Result getCarrierInfo(Long phone) {
        String carrier = smsApiService.getCarrierInfo(phone);
        return ok(Json.toJson(CommonResponse.OK(carrier)));
    }

}
