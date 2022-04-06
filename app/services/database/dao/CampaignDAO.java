package services.database.dao;

import com.google.inject.Inject;
import common.Utils;
import model.BuyPhonesRequest;
import model.CommonRequest;
import org.mybatis.guice.transactional.Transactional;
import services.database.mapper.CampaignsMapper;
import services.database.model.*;
import services.sms.model.SMSApiMessage;

import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static controllers.ListsController.CODE_LENGTH;

public class CampaignDAO {

    private CampaignsMapper mapper;

    private static Map<Integer, Set<Long>> staticDNCPhones = new ConcurrentHashMap();
    private static Map<Integer, Set<Long>> staticMasterDNCPhones = new ConcurrentHashMap();

    @Inject
    public CampaignDAO(CampaignsMapper mapper) {
        this.mapper = mapper;
    }


    public int getActiveCampaignsCountByUserId(long userId) {
        Integer value = mapper.getActiveCampaignsCountByUserId(userId);
        return value == null ? 0 : value;
    }

    public int getTotalCampaignsCountByUserId(long userId) {
        Integer value = mapper.getTotalCampaignsCountByUserId(userId);
        return value == null ? 0 : value;
    }

    public int getSMSSentCountByUserId(long userId) {
        Integer value = mapper.getSMSSentCountByUserId(userId);
        return value == null ? 0 : value;
    }

    public List<Sender> getSendersByRequest(CommonRequest commonRequest) {
        return mapper.getSendersByRequest(commonRequest);
    }

    public int getSendersCountByRequest(CommonRequest commonRequest) {
        Integer value = mapper.getSendersCountByRequest(commonRequest);
        return value == null ? 0 : value;
    }

    public Sender getSenderByName(Sender sender) {
        return mapper.getSenderByName(sender);
    }

    public Sender getSenderByPhone(Sender sender) {
        return mapper.getSenderByPhone(sender);
    }

    public void insertSender(Sender sender) {
        mapper.insertSender(sender);
    }

    public void updateSender(Sender sender) {
        mapper.updateSender(sender);
    }

    public void removeSenderById(Long senderId) {
        mapper.removeSenderById(senderId);
    }

    public List<SenderGroup> getSenderGroupsByRequest(CommonRequest commonRequest) {
        return mapper.getSenderGroupsByRequest(commonRequest);
    }

    public int getSenderGroupsCountByRequest(CommonRequest commonRequest) {
        Integer value = mapper.getSenderGroupsCountByRequest(commonRequest);
        return value == null ? 0 : value;
    }

    public SenderGroup getSenderGroupByName(SenderGroup senderGroup) {
        return mapper.getSenderGroupByName(senderGroup);
    }

    @Transactional
    public void insertSenderGroup(SenderGroup senderGroup) {
        mapper.insertSenderGroup(senderGroup);
        updateSendersForGroup(senderGroup);
    }

    @Transactional
    public void updateSenderGroup(SenderGroup senderGroup) {
        mapper.updateSenderGroup(senderGroup);
        updateSendersForGroup(senderGroup);
    }

    private void updateSendersForGroup(SenderGroup senderGroup) {
        mapper.removeSendersFromGroup(senderGroup);

        if (senderGroup.getSenders().size() > 0) {
            mapper.insertSendersIntoGroup(senderGroup);
        }
    }

    public void removeSenderGroupById(Long senderGroupId) {
        mapper.removeSenderGroupById(senderGroupId);
    }

    public List<Sender> getSendersForGroup(Long senderGroupId) {
        return mapper.getSendersForGroup(senderGroupId);
    }

    public void insertList(CampaignList list) {
        mapper.insertList(list);
    }

    public void insertListPhone(long listId, long phone, List<String> data) {
        String data1 = null;
        String data2 = null;

        if (data.size() > 0) {
            data1 = data.get(0);
        }

        if (data.size() > 1) {
            data2 = data.get(1);
        }

        mapper.insertListPhone(listId, Utils.formatPhone(phone), data1, data2);
    }

    public void insertDNCListPhone(DNCList list, Long phone) {
        Long formattedPhone = Utils.formatPhone(phone);
        mapper.insertDNCListPhone(list.getId(), formattedPhone);

        Map<Integer, Set<Long>> phonesMap = staticDNCPhones;
        if (!"DNC".equalsIgnoreCase(list.getName())) {
            phonesMap = staticMasterDNCPhones;
        }

        int code = Integer.parseInt(formattedPhone.toString().substring(0, CODE_LENGTH));
        if (!phonesMap.containsKey(code)) {
            phonesMap.put(code, new HashSet());
        }

        if (!phonesMap.get(code).contains(formattedPhone)) {
            phonesMap.get(code).add(formattedPhone);
        }
    }

    public void updateListCountById(long id, long count) {
        mapper.updateListCountById(id, count);
    }

    public List<CampaignList> getListsByRequest(CommonRequest commonRequest) {
        return mapper.getListsByRequest(commonRequest);
    }

    public int getListsCountByRequest(CommonRequest commonRequest) {
        Integer value = mapper.getListsCountByRequest(commonRequest);
        return value == null ? 0 : value;
    }

    public void removeListById(Long listId) {
        mapper.removeListById(listId);
    }

    public CampaignList getListById(Long listId) {
        return mapper.getListById(listId);
    }

    public void copyPhonesToFiles(Long listId, FileWriter writer) throws Exception {
        long offset = 0;
        long batch = 10000;

        Map<Integer, Set<Long>> dncPhonesMap =
                generateDNCPhonesMap(getDNCListByName(DNCList.MAIN));

        List<Long> phones = mapper.getPhonesByListIdAndLimit(listId, offset, batch);
        while (phones.size() > 0) {
            for (Long phone : phones) {
                int code = Integer.parseInt(phone.toString().substring(0, CODE_LENGTH));
                if (dncPhonesMap.get(code) == null || !dncPhonesMap.get(code).contains(phone)) {
                    writer.write(phone.toString());
                    writer.write("\n");
                }
            }

            offset = offset + batch;
            phones = mapper.getPhonesByListIdAndLimit(listId, offset, batch);
        }

        writer.flush();
        writer.close();
    }

    public void copyDNCPhonesToFiles(Long listId, FileWriter writer) throws Exception {
        synchronized (CampaignDAO.class) {
            long offset = 0;
            long batch = 10000;

            List<Long> phones = mapper.getDNCPhonesByListIdAndLimit(listId, offset, batch);
            while (phones.size() > 0) {
                for (Long phone : phones) {
                    writer.write(phone.toString());
                    writer.write("\n");
                }

                offset = offset + batch;
                phones = mapper.getDNCPhonesByListIdAndLimit(listId, offset, batch);
            }

            writer.flush();
            writer.close();
        }
    }

    public void removeCampaignById(Long campaignId) {
        mapper.removeCampaignById(campaignId);
    }

    public Campaign getCampaignByName(String name, long userId) {
        return mapper.getCampaignByName(name, userId);
    }

    public void insertCampaign(Campaign campaign) {
        mapper.insertCampaign(campaign);
    }

    public void updateCampaign(Campaign campaign) {
        mapper.updateCampaign(campaign);
    }

    public List<Campaign> getCampaignsByRequest(CommonRequest commonRequest) {
        return mapper.getCampaignsByRequest(commonRequest);
    }

    public int getCampaignsCountByRequest(CommonRequest commonRequest) {
        Integer value = mapper.getCampaignsCountByRequest(commonRequest);
        return value == null ? 0 : value;
    }

    public List<CampaignList> getListsByCampaignId(Long campaignId) {
        return mapper.getListsByCampaignId(campaignId);
    }

    public List<Phone> getPhonesByCampaignId(Long campaignId) {
        return mapper.getPhonesByCampaignId(campaignId);
    }

    @Transactional
    public void updateListsForCampaign(Campaign campaign) {
        mapper.removeListsForCampaign(campaign);

        if (campaign.getLists().size() > 0) {
            mapper.insertListsForCampaign(campaign);
        }

        Long leadsCount = mapper.getCampaignLeadsCount(campaign);
        campaign.setLeadsCount(leadsCount == null || leadsCount < 0 ? 0 : leadsCount);

        mapper.updateCampaignLeadsCount(campaign);
    }

    public void updateCampaignErrorStatusById(long campaignId, String error) {
        mapper.updateCampaignErrorStatusById(campaignId, error);
    }

    public void updateCampaignStatusById(long campaignId, int status) {
        mapper.updateCampaignStatusById(campaignId, status);
    }

    public Sender getSenderById(int senderId) {
        return mapper.getSenderById(senderId);
    }

    public List<Sender> getSendersByGroupId(int groupId) {
        return mapper.getSendersByGroupId(groupId);
    }

    public CampaignListItem getNotSentPhoneFromList(long listId) {
        return mapper.getNotSentPhoneFromList(listId);
    }

    public void updateListItemSentStatusById(long itemId, boolean sent) {
        mapper.updateListItemSentStatusById(itemId, sent);
    }

    public void incrementCampaignSentCountById(Campaign campaign) {
        mapper.incrementCampaignSentCountById(campaign);
    }

    public void incrementListSentCountById(CampaignList list) {
        mapper.incrementListSentCountById(list);
    }

    public List<Phone> getPhonesByRequest(CommonRequest commonRequest) {
        return mapper.getPhonesByRequest(commonRequest);
    }

    public int getPhonesCountByRequest(CommonRequest commonRequest) {
        Integer value = mapper.getPhonesCountByRequest(commonRequest);
        return value == null ? 0 : value;
    }

    public void insertPhones(BuyPhonesRequest buyPhonesRequest) {
        mapper.insertPhones(buyPhonesRequest);
    }

    public Phone getPhoneById(Long phoneId) {
        return mapper.getPhoneById(phoneId);
    }

    public void removePhoneById(Long phoneId) {
        mapper.removePhoneById(phoneId);
    }

    public void removeSendersByPhoneAndUserId(Long phone, long userId) {
        mapper.removeSendersByPhoneAndUserId(phone, userId);
    }

    public List<Phone> getFreePhonesByUserId(long userId) {
        return mapper.getFreePhonesByUserId(userId);
    }

    public Campaign getCampaignById(long id) {
        return mapper.getCampaignById(id);
    }

    @Transactional
    public Chat updateChatMessage(Chat chat, Campaign campaign,
                                  long toPhone, long fromPhone,
                                  String message, long userId,
                                  boolean inbound, long date,
                                  boolean manual, boolean updateChatLastDate) {
        if (chat == null) {
            chat = mapper.getChatWithReceiverPhoneAndUserId(Utils.formatPhone(toPhone), userId);
            if (chat == null) {
                chat = new Chat(userId, campaign != null ? campaign.getId() : null, Utils.formatPhone(toPhone), fromPhone);
                mapper.insertChat(chat);
            }
        }

        mapper.insertChatMessage(userId,
                new ChatMessage(chat.getId(), message, inbound, date, true, null, fromPhone, manual));

        chat.setLastMessage(message);
        if (updateChatLastDate) {
            chat.setLastDate(date);
        }

        chat.setRead(true);

        mapper.updateChatLastMessageAndDate(chat);

        return chat;
    }

    public List<Chat> getAllChats() {
        return mapper.getAllChats();
    }

    public void incrementSenderSentCountById(long id) {
        mapper.incrementSenderSentCountById(id);
    }

    public boolean updateExternalChatMessage(Chat chat, SMSApiMessage message) {
        if (mapper.getChatMessageByExternalId(chat.getUserId(), message.getMessageSid()) == null) {
            mapper.insertChatMessage(
                    chat.getUserId(),
                    new ChatMessage(
                            chat.getId(), message.getBody(),
                            true, message.getDate(),
                            false, message.getMessageSid(),
                            chat.getPhoneTo(), false));

            chat.setLastMessage(message.getBody());
            chat.setLastDate(message.getDate());
            chat.setRead(false);

            mapper.updateChatLastMessageAndDate(chat);

            return true;
        }

        return false;
    }

    public List<Chat> getVisibleChatsByUserId(long userId) {
        return mapper.getVisibleChatsByUserId(userId);
    }

    public List<ChatMessage> getChatsMessagesByChatId(Long id, long userId) {
        return mapper.getChatsMessagesByChatId(id, userId);
    }

    public Chat getChatById(long chatId, long userId) {
        return mapper.getChatById(chatId, userId);
    }

    public void updateChatReadById(Long id, long userId) {
        mapper.updateChatReadById(id, userId);
    }

    public void updateMessagesRedByChatId(Long id, long userId) {
        mapper.updateMessagesRedByChatId(id, userId);
    }

    @Transactional
    public void updateCampaignPhones(Campaign campaign) {
        mapper.removePhonesForCampaign(campaign);

        if (campaign.isAllPhones()) {
            campaign.setSelectedPhones(getPhonesByUserId((long) campaign.getUserId()));
        }

        if (campaign.getSelectedPhones().size() > 0) {
            mapper.insertPhonesForCampaign(campaign);
        }
    }

    public void updateHasInboundForChat(long chatId, long userId, boolean value) {
        mapper.updateHasInboundForChat(chatId, userId, value);
    }

    public List<Chat> getChatsByToNumber(long phone, List<User> users) {
        List<Chat> chats = new LinkedList();
        for (User user : users) {
            chats.addAll(mapper.getChatsByToNumber(phone, user.getId()));
        }

        return chats;
    }

    public List<DNCList> getDNCLists() {
        return mapper.getDNCLists();
    }

    public DNCList getDNCListById(Long listId) {
        return mapper.getDNCListById(listId);
    }

    public Map<Integer, Set<Long>> generateDNCPhonesMap(DNCList list) {
        synchronized (CampaignDAO.class) {
            if (staticDNCPhones.size() > 0) {
                return staticDNCPhones;
            }

            generateDNCPhonesMapFromList(list, staticDNCPhones);
        }

        return staticDNCPhones;
    }

    public Map<Integer, Set<Long>> generateMasterDNCPhonesMap() {
        DNCList list = getDNCListByName(DNCList.MASTER);
        synchronized (CampaignDAO.class) {
            if (staticMasterDNCPhones.size() > 0) {
                return staticMasterDNCPhones;
            }

            generateDNCPhonesMapFromList(list, staticMasterDNCPhones);
        }

        return staticMasterDNCPhones;
    }

    private Map<Integer, Set<Long>> generateDNCPhonesMapFromList(DNCList list, Map<Integer, Set<Long>> phonesMap) {
        long offset = 0;
        long batch = 100000;

        List<Long> phones = mapper.getDNCPhonesByListIdAndLimit(list.getId(), offset, batch);

        while (phones.size() > 0) {
            for (Long phone : phones) {
                if (phone.toString().length() > CODE_LENGTH) {
                    int code = Integer.parseInt(phone.toString().substring(0, CODE_LENGTH));

                    if (!phonesMap.containsKey(code)) {
                        phonesMap.put(code, new HashSet());
                    }

                    if (!phonesMap.get(code).contains(phone)) {
                        phonesMap.get(code).add(phone);
                    }
                }
            }

            offset = offset + batch;
            phones = mapper.getDNCPhonesByListIdAndLimit(list.getId(), offset, batch);

            System.out.println("Generating DNC phones list: " + list.getName() + "... Offset: " + offset);
        }
        updateDNCListCountAndDateById(list.getId());

        return phonesMap;
    }

    public void updateDNCListCountAndDateById(long id) {
        mapper.updateDNCListCountItselfAndDateById(id, System.currentTimeMillis());
    }

    public void updateDNCListCountAndDateById(long id, long count) {
        mapper.updateDNCListCountAndDateById(id, count, System.currentTimeMillis());
    }

    public void incrementCampaignDNCCountById(long id) {
        mapper.incrementCampaignDNCCountById(id);
    }

    public void insertCampaignError(CampaignError campaignError) {
        mapper.insertCampaignError(campaignError);
    }

    public List<CampaignError> getCampaignErrorsByCampaignId(long id) {
        return mapper.getCampaignErrorsByCampaignId(id);
    }

    public void incrementCampaignErrorsCountById(Campaign campaign) {
        mapper.incrementCampaignErrorsCountById(campaign);
    }

    public void removeChatById(Long chatId, long userId) {
        mapper.removeChatById(chatId, userId);
    }

    public List<Chat> getVisibleChatsByRequest(CommonRequest commonRequest) {
        return mapper.getVisibleChatsByRequest(commonRequest);
    }

    public Integer getVisibleChatsCountByRequest(CommonRequest commonRequest) {
        Integer value = mapper.getVisibleChatsCountByRequest(commonRequest);
        return value == null ? 0 : value;
    }

    public Long getDNCPhoneByListId(long listId, Long phone) {
        return mapper.getDNCPhoneByListId(listId, phone);
    }

    public Phone getPhoneByUserIdAndPhone(long userId, long phoneTo) {
        return mapper.getPhoneByUserIdAndPhone(userId, phoneTo);
    }

    public int getPhonesCountByUserId(long userId) {
        Integer value = mapper.getPhonesCountByUserId(userId);
        return value == null ? 0 : value;
    }

    public void decrementCampaignLeadsCountById(Campaign campaign) {
        mapper.decrementCampaignLeadsCountById(campaign);
    }

    public void incrementPhoneSentCountById(Phone phone) {
        mapper.incrementPhoneSentCountById(phone);
    }

    public List<Phone> getPhonesByUserId(Long userId) {
        return mapper.getPhonesByUserId(userId);
    }

    public Phone getPhoneByNumber(long phone) {
        return mapper.getPhoneByNumber(phone);
    }

    @Transactional
    public Long getAndInsertSentPhone(Long phone, long date) {
        Long sentPhone;

        synchronized (CampaignDAO.class) {
            sentPhone = mapper.getSentPhone(phone, date);
            if (sentPhone == null) {
                insertSentPhone(phone, System.currentTimeMillis());
            }
        }

        return sentPhone;
    }

    public void incrementCampaignIgnoredCountById(long id) {
        mapper.incrementCampaignIgnoredCountById(id);
    }

    public void insertSentPhone(Long phone, long date) {
        mapper.insertSentPhone(phone, date);
    }

    public void updatePhone(Phone phone) {
        mapper.updatePhone(phone);
    }

    public void incrementListDNCCountById(long id) {
        mapper.incrementListDNCCountById(id);
    }

    public void incrementListIgnoredCountById(long id) {
        mapper.incrementListIgnoredCountById(id);
    }

    public void incrementListErrorsById(long id) {
        mapper.incrementListErrorsById(id);
    }

    public void banChat(Chat chat) {
        Long phone = chat.getPhoneTo();


        DNCList dncList = getDNCListByName(DNCList.MAIN);
        if (dncList != null) {
            Long dbPhone = getDNCPhoneByListId(dncList.getId(), phone);
            if (dbPhone == null) {
                insertDNCListPhone(dncList, phone);
                updateDNCListCountAndDateById(dncList.getId());
            }
        }

        removeChatById(chat.getId(), chat.getUserId());
    }

    public DNCList getDNCListByName(String name) {
        return mapper.getDNCListByName(name);
    }

    public List<Phone> getAllPhones() {
        return mapper.getAllPhones();
    }

    public void updatePhoneChargedDateById(long id, long date) {
        mapper.updatePhoneChargedDateById(id, date);
    }

    public void updateCampaignAgentCredentials(Campaign campaign) {
        mapper.updateCampaignAgentCredentials(campaign);
    }

    public Campaign getCampaignByAgentUsername(String agentUsername) {
        return mapper.getCampaignByAgentUsername(agentUsername);
    }

    public ChatMessage getLastIncomeMessageByChatId(long id, long userId) {
        return mapper.getLastIncomeMessageByChatId(id, userId);
    }

    public List<Chat> getVisibleChatsWithLastInboundMessagesByUserId(int id) {
        List<Chat> chats = mapper.getVisibleChatsWithLastInboundMessagesByUserId(id);

        Set<Long> ids = new HashSet();
        Iterator<Chat> it = chats.iterator();

        while (it.hasNext()) {
            Chat chat = it.next();

            if (ids.contains(chat.getId())) {
                it.remove();
            } else {
                ids.add(chat.getId());
            }
        }

        return chats;
    }

    public void incrementPhoneInboundCountById(long id) {
        mapper.incrementPhoneInboundCountById(id);
    }

    public void incrementPhoneDaySentCountById(Phone fromPhone) {
        mapper.incrementPhoneDaySentCountById(fromPhone);
    }

    public void resetDaySentCountForCallerIds() {
        mapper.resetDaySentCountForCallerIds();
        mapper.resetCallerIdsLimitError();
    }

    @Transactional
    public CampaignListItem getNotSentPhoneFromListAndUpdateStatus(long listId) {
        CampaignListItem phone;

        synchronized (CampaignDAO.class) {
            phone = getNotSentPhoneFromList(listId);
            if (phone != null) {
                updateListItemSentStatusById(phone.getId(), true);
            }
        }

        return phone;
    }

    public ChatMessage getLastChatMessageByChatId(long chatId, long userId) {
        return mapper.getLastChatMessageByChatId(chatId, userId);
    }

    public ChatMessage getLastOutcomeMessageByChatId(long id, long userId) {
        return mapper.getLastOutcomeMessageByChatId(id, userId);
    }

    public List<Campaign> getAllCampaigns() {
        return mapper.getAllCampaigns();
    }

    public void updateLeadsCount(Campaign campaign) {
        Long leadsCount = mapper.getCampaignLeadsCount(campaign);
        if (leadsCount != null) {
            leadsCount = leadsCount - campaign.getDncCount() - campaign.getSentCount() - campaign.getErrorsCount() - campaign.getIgnoredCount();
        }

        campaign.setLeadsCount(leadsCount == null || leadsCount < 0 ? 0 : leadsCount);
        mapper.updateCampaignLeadsCount(campaign);
    }

    public void updatePhonesForwardingByUserId(Phone phone) {
        mapper.updatePhonesForwardingByUserId(phone);
    }

    public List<Campaign> getStartedCampaigns() {
        return mapper.getStartedCampaigns();
    }

    public Long getCampaignLeadsCount(Campaign campaign) {
        Long result = mapper.getCampaignLeadsCount(campaign);
        return (result == null || result < 0) ? 0 : result;
    }

    public void incrementListReceivedCountById(Chat chat, long phone) {
        List<CampaignList> lists = getListsByCampaignId(chat.getCampaignId());
        for (CampaignList list : lists) {
            CampaignListItem sentPhone = getSentCampaignListItemByListIdAndPhone(phone, list.getId());
            if (sentPhone != null) {
                incrementCampaignReceivedCountById(list.getId());
            }
        }
    }

    private CampaignListItem getSentCampaignListItemByListIdAndPhone(long phone, long id) {
        return mapper.getSentCampaignListItemByListIdAndPhone(phone, id);
    }

    public List<Long> getAllNotSentPhoneFromList(long listId) {
        return mapper.getAllNotSentPhoneFromList(listId);
    }

    private void incrementCampaignReceivedCountById(long id) {
        mapper.incrementCampaignReceivedCountById(id);
    }

    public void insertDNCListPhones(DNCList list, List<Long> phones) {
        mapper.insertDNCListPhones(list, phones);
    }

    public boolean checkUserChatTables(User user) {
        try {
            mapper.checkUserChatsTable(user.getId());
            return true;
        } catch (Exception e) { /*e.printStackTrace();*/ }

        return false;
    }

    public void createUserChatsTable(User user) {
        mapper.createUserChatsTable(user);
    }

    public void createUserChatMessagesTable(User user) {
        mapper.createUserChatMessagesTable(user);
    }

    public void migrateUserChatsData(User user) {
        createUserChatsTable(user);
        createUserChatMessagesTable(user);

        List<Chat> chats = getAllUserChats(user);
        for (Chat chat : chats) {
            long previousChatId = chat.getId();

            mapper.copyChat(chat);
            mapper.copyUserChatMessages(user, chat, previousChatId);
        }
    }

    private List<Chat> getAllUserChats(User user) {
        return mapper.getAllUserChats(user);
    }

    public List<Phone> getPhonesBySentCountWithoutIncomeMessages(Long count) {
        return mapper.getPhonesBySentCountWithoutIncomeMessages(count);
    }
}
