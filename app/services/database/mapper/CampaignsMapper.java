package services.database.mapper;

import model.BuyPhonesRequest;
import model.CommonRequest;
import org.apache.ibatis.annotations.*;
import services.database.model.*;

import java.util.List;

public interface CampaignsMapper {

    @Select("SELECT * FROM campaigns WHERE userId = #{userId} AND startDate > #{startDate} AND endDate < #{endDate}")
    List<Campaign> getCampaignsByUserId(@Param("userId") long userId,
                                        @Param("startDate") long startDate,
                                        @Param("endDate") long endDate);

    @Select("SELECT COUNT(id) FROM campaigns WHERE userId=#{userId} AND status=1")
    Integer getActiveCampaignsCountByUserId(@Param("userId") long userId);

    @Select("SELECT COUNT(id) FROM campaigns WHERE userId=#{userId}")
    Integer getTotalCampaignsCountByUserId(@Param("userId") long userId);

    @Select("SELECT SUM(sentCount) FROM campaigns WHERE userId=#{userId}")
    Integer getSMSSentCountByUserId(@Param("userId") long userId);

    @Select("SELECT * FROM senders WHERE userId=#{userId} ORDER by date DESC LIMIT #{limit} OFFSET #{offset}")
    List<Sender> getSendersByRequest(CommonRequest commonRequest);

    @Select("SELECT count(id) FROM senders WHERE userId=#{userId}")
    Integer getSendersCountByRequest(CommonRequest commonRequest);

    @Select("SELECT * FROM senders WHERE userId=#{userId} AND lower(name)=lower(#{name})")
    Sender getSenderByName(Sender sender);

    @Select("SELECT * FROM senders WHERE phone=#{phone}")
    Sender getSenderByPhone(Sender sender);

    @Insert("INSERT INTO senders(userId, name, phone, date, sentCount) VALUES (#{userId}, #{name}, #{phone}, #{date}, #{sentCount})")
    void insertSender(Sender sender);

    @Update("UPDATE senders SET name=#{name}, phone=#{phone} WHERE id=#{id}")
    void updateSender(Sender sender);

    @Delete("DELETE FROM senders WHERE id=#{id}")
    void removeSenderById(@Param("id") Long senderId);

    List<SenderGroup> getSenderGroupsByRequest(CommonRequest commonRequest);

    @Select("SELECT count(id) FROM senderGroups WHERE userId=#{userId}")
    Integer getSenderGroupsCountByRequest(CommonRequest commonRequest);

    @Select("SELECT * FROM senderGroups WHERE userId=#{userId} AND lower(name)=lower(#{name})")
    SenderGroup getSenderGroupByName(SenderGroup senderGroup);

    @Delete("DELETE FROM senderGroups WHERE id=#{id}")
    void removeSenderGroupById(@Param("id") Long senderGroupId);

    void insertSenderGroup(SenderGroup senderGroup);

    @Update("UPDATE senderGroups SET name=#{name} WHERE id=#{id}")
    void updateSenderGroup(SenderGroup senderGroup);

    void removeSendersFromGroup(SenderGroup senderGroup);

    void insertSendersIntoGroup(SenderGroup senderGroup);

    @Select("SELECT * FROM senders WHERE id IN (SELECT senderId FROM senderGroupItems WHERE groupId=#{id})")
    List<Sender> getSendersForGroup(@Param("id") Long senderGroupId);

    void insertList(CampaignList list);

    @Insert("INSERT INTO listItems(listId, phone, data, data2) VALUES(#{listId}, #{phone}, #{data}, #{data2})")
    void insertListPhone(@Param("listId") long listId,
                         @Param("phone") long phone,
                         @Param("data") String data,
                         @Param("data2") String data2);

    @Update("UPDATE lists SET cnt=#{count} WHERE id=#{id}")
    void updateListCountById(@Param("id") long id,
                             @Param("count") long count);

    @Select("SELECT * FROM lists WHERE userId=#{userId} ORDER by date DESC LIMIT #{limit} OFFSET #{offset}")
    List<CampaignList> getListsByRequest(CommonRequest commonRequest);

    @Select("SELECT count(id) FROM lists WHERE userId=#{userId}")
    Integer getListsCountByRequest(CommonRequest commonRequest);

    @Delete("DELETE FROM lists WHERE id=#{listId}")
    void removeListById(@Param("listId") Long listId);

    @Select("SELECT * FROM lists WHERE id=#{id}")
    CampaignList getListById(@Param("id") Long listId);

    @Select("SELECT phone FROM listItems WHERE listId=#{listId} ORDER BY id LIMIT #{limit} OFFSET #{offset}")
    List<Long> getPhonesByListIdAndLimit(@Param("listId") Long listId,
                                         @Param("offset") long offset,
                                         @Param("limit") long batch);

    @Select("SELECT phone FROM dncListItems WHERE listId=#{listId} ORDER BY id LIMIT #{limit} OFFSET #{offset}")
    List<Long> getDNCPhonesByListIdAndLimit(@Param("listId") Long listId,
                                            @Param("offset") long offset,
                                            @Param("limit") long batch);

    @Delete("DELETE FROM campaigns WHERE id=#{id}")
    void removeCampaignById(@Param("id") Long campaignId);

    @Select("SELECT * FROM campaigns WHERE userId=#{userId} AND lower(name)=lower(#{name})")
    Campaign getCampaignByName(@Param("name") String name,
                               @Param("userId") long userId);

    void insertCampaign(Campaign campaign);

    void updateCampaign(Campaign campaign);

    List<Campaign> getCampaignsByRequest(CommonRequest commonRequest);

    Integer getCampaignsCountByRequest(CommonRequest commonRequest);

    @Select("SELECT listId as id FROM campaignsLists WHERE campaignId=#{campaignId} ORDER BY id DESC")
    List<CampaignList> getListsByCampaignId(@Param("campaignId") Long campaignId);

    @Select("SELECT phones.id, phones.phone, phones.tollFree FROM phones WHERE id IN (SELECT phoneId FROM campaignPhones WHERE campaignId=#{campaignId} ORDER BY campaignPhones.id)")
    List<Phone> getPhonesByCampaignId(@Param("campaignId") Long campaignId);

    @Delete("DELETE FROM campaignsLists WHERE campaignId=#{id}")
    void removeListsForCampaign(Campaign campaign);

    void insertListsForCampaign(Campaign campaign);

    @Select("SELECT (SUM(cnt) - SUM(sentCount) - SUM(ignored) - SUM(dnc) - SUM(errors)) FROM lists WHERE id IN (SELECT listId FROM campaignsLists WHERE campaignId=#{id})")
    Long getCampaignLeadsCount(Campaign campaign);

    @Update("UPDATE campaigns SET leadsCount=#{leadsCount} WHERE id=#{id}")
    void updateCampaignLeadsCount(Campaign campaign);

    @Update("UPDATE campaigns SET status=#{status} WHERE id=#{campaignId}")
    void updateCampaignStatusById(@Param("campaignId") long campaignId,
                                  @Param("status") int status);

    @Select("SELECT * FROM senders WHERE id=#{senderId}")
    Sender getSenderById(@Param("senderId") int senderId);

    @Select("SELECT * FROM senders WHERE id IN (SELECT senderId FROM senderGroupItems WHERE groupId=#{groupId})")
    List<Sender> getSendersByGroupId(@Param("groupId") int groupId);

    @Update("UPDATE campaigns SET errorStatus=#{error} WHERE id=#{id}")
    void updateCampaignErrorStatusById(@Param("id") long campaignId,
                                       @Param("error") String error);

    @Update("UPDATE listItems SET sent=#{sent} WHERE id=#{itemId}")
    void updateListItemSentStatusById(@Param("itemId") long itemId,
                                      @Param("sent") boolean sent);

    @Select("SELECT * FROM listItems WHERE listId=#{listId} AND sent=false LIMIT 1")
    CampaignListItem getNotSentPhoneFromList(@Param("listId") long listId);

    @Select("SELECT phone FROM listItems WHERE listId=#{listId} AND sent=false")
    List<Long> getAllNotSentPhoneFromList(@Param("listId") long listId);

    @Update("UPDATE campaigns SET sentCount=sentCount+1 WHERE id=#{id}")
    void incrementCampaignSentCountById(Campaign campaign);

    @Update("UPDATE lists SET sentCount=sentCount+1 WHERE id=#{id}")
    void incrementListSentCountById(CampaignList list);

    @Select("SELECT * FROM phones WHERE userId=#{userId} ORDER by date DESC, id DESC LIMIT #{limit} OFFSET #{offset}")
    List<Phone> getPhonesByRequest(CommonRequest commonRequest);

    @Select("SELECT count(id) FROM phones WHERE userId=#{userId}")
    Integer getPhonesCountByRequest(CommonRequest commonRequest);

    void insertPhones(BuyPhonesRequest buyPhonesRequest);

    @Select("SELECT * FROM phones WHERE id=#{id}")
    Phone getPhoneById(@Param("id") Long phoneId);

    @Delete("DELETE FROM phones WHERE id=#{id}")
    void removePhoneById(@Param("id") Long phoneId);

    @Delete("DELETE FROM senders WHERE userId=#{userId} AND phone=#{phone}")
    void removeSendersByPhoneAndUserId(@Param("phone") Long phone,
                                       @Param("userId") long userId);

    @Select("SELECT phones.id, phones.phone FROM phones LEFT JOIN senders ON phones.phone=senders.phone WHERE senders.id IS NULL")
    List<Phone> getFreePhonesByUserId(long userId);

    @Select("SELECT * FROM campaigns WHERE id=#{id}")
    Campaign getCampaignById(@Param("id") long id);

    @Select("SELECT * FROM chats_${userId} WHERE phoneTo=#{toPhone} LIMIT 1")
    Chat getChatWithReceiverPhoneAndUserId(@Param("toPhone") long toPhone,
                                           @Param("userId") long userId);

    void insertChat(Chat chat);

    void copyChat(Chat chat);

    @Insert("INSERT INTO chatMessages_${userId}(chatId, message, date, inbound, read, externalId, phoneFrom, manual) " +
            "VALUES (#{message.chatId}, #{message.message}, #{message.date}, #{message.inbound}, #{message.read}, " +
            "#{message.externalId}, #{message.phoneFrom}, #{message.manual})")
    void insertChatMessage(@Param("userId") long userId,
                           @Param("message") ChatMessage chatMessage);

    @Update("UPDATE chats_${userId} SET lastDate=#{lastDate}, lastMessage=#{lastMessage}, read=#{read} WHERE id = #{id}")
    void updateChatLastMessageAndDate(Chat chat);

    @Select("SELECT * FROM chats")
    List<Chat> getAllChats();

    @Update("UPDATE senders SET sentCount=sentCount+1 WHERE id=#{id}")
    void incrementSenderSentCountById(@Param("id") long id);

    @Select("SELECT * FROM chatMessages_${userId} WHERE externalId=#{externalId}")
    ChatMessage getChatMessageByExternalId(@Param("userId") long userId,
                                           @Param("externalId") String messageSid);

    @Select("SELECT * FROM chats_${userId} WHERE hasInbound=true ORDER BY lastDate DESC")
    List<Chat> getVisibleChatsByUserId(@Param("userId") long userId);

    @Select("SELECT * FROM chatMessages_${userId} WHERE chatId=#{chatId} ORDER BY date")
    List<ChatMessage> getChatsMessagesByChatId(@Param("chatId") Long id,
                                               @Param("userId") Long userId);

    @Select("SELECT * FROM chatMessages_${userId} WHERE chatId=#{chatId} ORDER BY date DESC LIMIT 1")
    ChatMessage getLastChatMessageByChatId(@Param("chatId") long id,
                                           @Param("userId") long userId);

    @Select("SELECT * FROM chats_${userId} WHERE id=#{chatId}")
    Chat getChatById(@Param("chatId") long chatId,
                     @Param("userId") long userId);

    @Update("UPDATE chats_${userId} SET read=true WHERE id=#{chatId}")
    void updateChatReadById(@Param("chatId") Long id,
                            @Param("userId") long userId);

    @Update("UPDATE chatMessages_${userId} SET read=true WHERE chatId=#{chatId}")
    void updateMessagesRedByChatId(@Param("chatId") Long id,
                                   @Param("userId") Long userId);

    @Delete("DELETE FROM campaignPhones WHERE campaignId=#{id}")
    void removePhonesForCampaign(Campaign campaign);

    void insertPhonesForCampaign(Campaign campaign);

    @Update("UPDATE chats_${userId} SET hasInbound=#{value} WHERE id=#{chatId}")
    void updateHasInboundForChat(@Param("chatId") long chatId,
                                 @Param("userId") long userId,
                                 @Param("value") boolean value);

    @Select("SELECT * FROM chats_${userId} WHERE phoneTo=#{phone}")
    List<Chat> getChatsByToNumber(@Param("phone") long phone,
                                  @Param("userId") long userId);

    @Select("SELECT * FROM dncLists WHERE userId=1 ORDER BY id")
    List<DNCList> getDNCLists();

    @Insert("INSERT INTO dncLists(userId,name,date,cnt) VALUES(1,#{name},#{date},#{cnt})")
    void insertDNCList(DNCList dnc);

    @Select("SELECT * FROM dncLists WHERE id=#{listId}")
    DNCList getDNCListById(Long listId);

    @Update("UPDATE dncLists SET cnt=#{count}, date=#{date} WHERE id=#{id}")
    void updateDNCListCountAndDateById(@Param("id") long id,
                                       @Param("count") long count,
                                       @Param("date") long currentTimeMillis);

    @Update("UPDATE dncLists SET cnt=q.cnt, date=#{date} FROM (SELECT count(listId) as cnt FROM dncListItems WHERE listId=#{id}) q " +
            "WHERE id=#{id}")
    void updateDNCListCountItselfAndDateById(@Param("id") long id,
                                             @Param("date") long currentTimeMillis);

    @Insert("INSERT INTO dncListItems(listId, phone) VALUES(#{listId}, #{phone})")
    void insertDNCListPhone(@Param("listId") long id,
                            @Param("phone") Long phone);

    @Update("UPDATE campaigns SET dncCount=dncCount+1 WHERE id=#{id}")
    void incrementCampaignDNCCountById(@Param("id") long id);

    @Insert("INSERT INTO campaignErrors(campaignId,phone,error,date) " +
            "VALUES(#{campaignId},#{phone},#{error},#{date})")
    void insertCampaignError(CampaignError campaignError);

    @Select("SELECT * FROM campaignErrors WHERE campaignId=#{campaignId} ORDER BY date DESC")
    List<CampaignError> getCampaignErrorsByCampaignId(@Param("campaignId") long id);

    @Update("UPDATE campaigns SET errorsCount=errorsCount + 1 WHERE id=#{id}")
    void incrementCampaignErrorsCountById(Campaign campaign);

    @Delete("DELETE FROM chats_${userId} WHERE id=#{chatId}")
    void removeChatById(@Param("chatId") Long chatId,
                        @Param("userId") long userId);

    List<Chat> getVisibleChatsByRequest(CommonRequest commonRequest);

    Integer getVisibleChatsCountByRequest(CommonRequest commonRequest);

    @Select("SELECT phone FROM dncListItems WHERE listId=#{listId} AND phone=#{phone}")
    Long getDNCPhoneByListId(@Param("listId") long listId,
                             @Param("phone") Long phone);

    @Select("SELECT * FROM phones WHERE userId=#{userId} AND phone=#{phone}")
    Phone getPhoneByUserIdAndPhone(@Param("userId") long userId,
                                   @Param("phone") long phoneTo);

    @Select("SELECT count(userId) FROM phones WHERE userId=#{userId}")
    Integer getPhonesCountByUserId(@Param("userId") long userId);

    @Update("UPDATE campaigns SET leadsCount=leadsCount-1 WHERE id=#{id}")
    void decrementCampaignLeadsCountById(Campaign campaign);

    @Update("UPDATE phones SET sentCount=sentCount+1 WHERE id=#{id}")
    void incrementPhoneSentCountById(Phone phone);

    @Select("SELECT * FROM phones WHERE userId = #{userId}")
    List<Phone> getPhonesByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM phones WHERE phone = #{phone} LIMIT 1")
    Phone getPhoneByNumber(@Param("phone") long phone);

    @Select("SELECT phone FROM sentPhones WHERE phone=#{phone} AND date > #{date} LIMIT 1")
    Long getSentPhone(@Param("phone") Long phone,
                      @Param("date") long date);

    @Update("UPDATE campaigns SET ignoredCount=ignoredCount+1 WHERE id=#{id}")
    void incrementCampaignIgnoredCountById(@Param("id") long id);

    @Insert("INSERT INTO sentPhones(phone, date) VALUES (#{phone}, #{date})")
    void insertSentPhone(@Param("phone") Long phone,
                         @Param("date") long date);

    @Update("UPDATE phones SET forwarding=#{forwarding}, note=#{note}, tollFree=#{tollFree} WHERE id=#{id}")
    void updatePhone(Phone phone);

    @Update("UPDATE lists SET dnc=dnc+1 WHERE id=#{id}")
    void incrementListDNCCountById(@Param("id") long id);

    @Update("UPDATE lists SET ignored=ignored+1 WHERE id=#{id}")
    void incrementListIgnoredCountById(@Param("id") long id);

    @Update("UPDATE lists SET errors=errors+1 WHERE id=#{id}")
    void incrementListErrorsById(@Param("id") long id);

    @Select("SELECT * FROM phones")
    List<Phone> getAllPhones();

    @Update("UPDATE phones SET chargedDate=#{date} WHERE id=#{id}")
    void updatePhoneChargedDateById(@Param("id") long id,
                                    @Param("date") long date);

    @Update("UPDATE campaigns SET agentUsername=#{agentUsername}, agentPassword=#{agentPassword} " +
            "WHERE id=#{id}")
    void updateCampaignAgentCredentials(Campaign campaign);

    @Select("SELECT * FROM campaigns WHERE agentUsername=#{agentUsername} LIMIT 1")
    Campaign getCampaignByAgentUsername(@Param("agentUsername") String agentUsername);

    @Select("SELECT * FROM chatMessages_${userId} WHERE chatId=#{chatId} AND inbound=true ORDER BY date DESC limit 1")
    ChatMessage getLastIncomeMessageByChatId(@Param("chatId") long id,
                                             @Param("userId") long userId);

    List<Chat> getVisibleChatsWithLastInboundMessagesByUserId(@Param("userId") int id);

    @Update("UPDATE phones SET inboundCount=inboundCount+1 WHERE id=#{id}")
    void incrementPhoneInboundCountById(@Param("id") long id);

    @Update("UPDATE phones SET daySentCount=daySentCount+1, lastSentDate=#{lastSentDate} WHERE id=#{id}")
    void incrementPhoneDaySentCountById(Phone fromPhone);

    @Update("UPDATE phones SET daySentCount=0")
    void resetDaySentCountForCallerIds();

    @Select("SELECT * FROM chatMessages_${userId} WHERE chatId=#{chatId} AND inbound=false ORDER BY date DESC limit 1")
    ChatMessage getLastOutcomeMessageByChatId(@Param("chatId") long chatId,
                                              @Param("userId") long userId);

    @Select("SELECT * FROM campaigns")
    List<Campaign> getAllCampaigns();

    @Update("UPDATE campaigns SET errorStatus='' WHERE errorStatus='Caller IDs exceeded days sent count limit'")
    void resetCallerIdsLimitError();

    @Update("UPDATE phones SET forwarding=#{forwarding} WHERE userId=#{userId}")
    void updatePhonesForwardingByUserId(Phone phone);

    @Select("SELECT * FROM campaigns WHERE status=1")
    List<Campaign> getStartedCampaigns();

    @Update("UPDATE lists SET receivedCount=receivedCount+1 WHERE id=#{id}")
    void incrementCampaignReceivedCountById(@Param("id") long id);

    @Select("SELECT * FROM listItems WHERE listId=#{listId} AND sent=true AND phone=#{phone} LIMIT 1")
    CampaignListItem getSentCampaignListItemByListIdAndPhone(@Param("phone") long phone,
                                                             @Param("listId") long id);

    @Select("SELECT * FROM dncLists WHERE name=#{name} limit 1")
    DNCList getDNCListByName(@Param("name") String name);

    void insertDNCListPhones(@Param("list") DNCList list,
                             @Param("phones") List<Long> phones);

    @Select("SELECT * FROM chats_${userId} LIMIT 1")
    List<Chat> checkUserChatsTable(@Param("userId") long id);

    void createUserChatsTable(User user);

    void createUserChatMessagesTable(User user);

    @Select("SELECT * FROM chats WHERE userId=#{user.id}")
    List<Chat> getAllUserChats(@Param("user") User user);

    @Insert("INSERT INTO chatMessages_${user.id}(chatId, message, date, inbound, read, externalId, phoneFrom, manual) " +
            "SELECT #{chat.id}, message, date, inbound, read, externalId, phoneFrom, manual FROM chatMessages " +
            "WHERE chatId=#{previousChatId}")
    void copyUserChatMessages(@Param("user") User user,
                              @Param("chat") Chat chat,
                              @Param("previousChatId") long previousChatId);

    @Select("SELECT * FROM phones WHERE sentCount >= #{count} AND inboundCount = 0")
    List<Phone> getPhonesBySentCountWithoutIncomeMessages(@Param("count") Long count);
}
