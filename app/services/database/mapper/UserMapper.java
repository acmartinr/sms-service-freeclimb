package services.database.mapper;

import model.CommonRequest;
import org.apache.ibatis.annotations.*;
import org.checkerframework.checker.guieffect.qual.SafeEffect;
import services.database.model.*;

import java.util.List;

public interface UserMapper {

    @Select("SELECT * FROM users WHERE blocked=false AND (LOWER(username)=LOWER(#{value}) OR LOWER(email)=LOWER(#{value}))")
    User findUserByUsernameOrEmail(@Param("value") String value);

    List<User> getUsersByRequest(CommonRequest commonRequest);

    Integer getUsersCountByRequest(CommonRequest commonRequest);

    @Delete("DELETE FROM pinCodes WHERE phone=#{phone}")
    void removePinCodesForPhones(@Param("phone") Long phone);

    @Insert("INSERT INTO pinCodes(phone, code) VALUES(#{phone}, #{code})")
    void insertPinCode(@Param("phone") Long phone,
                       @Param("code") int pinCode);

    @Select("SELECT code FROM pinCodes WHERE phone=#{phone}")
    Integer getPinCodeByPhone(@Param("phone") Long formatPhone);

    @Insert("INSERT INTO users(username,role,origin,email,password,date,balance,fullName,resellerId) " +
            "VALUES(#{username},#{role},#{origin},#{email},#{password},#{date},#{balance},#{fullName},#{resellerId})")
    void insertUser(User user);

    @Update("UPDATE users SET balance=balance + #{value} WHERE id=#{id}")
    void changeUserBalance(@Param("id") long userId,
                           @Param("value") float value);

    @Select("SELECT * FROM users WHERE id=#{userId}")
    User getUserById(@Param("userId") long userId);

    @Insert("INSERT INTO transactions(userId, type, amount, date, details) VALUES(#{userId}, #{type}, #{amount}, #{date}, #{details})")
    void insertTransaction(Transaction transaction);

    @Insert("INSERT INTO detailedTransactions(userId, type, amount, date) VALUES(#{userId}, #{type}, #{amount}, #{date})")
    void insertDetailedTransaction(Transaction transaction);

    @Select("SELECT * FROM transactions WHERE userId=#{userId} ORDER by date DESC, type LIMIT #{limit} OFFSET #{offset}")
    List<Transaction> getTransactionsByRequest(CommonRequest commonRequest);

    @Select("SELECT count(id) FROM transactions WHERE userId=#{userId}")
    Integer getTransactionsCountByRequest(CommonRequest commonRequest);

    List<Transaction> getAllTransactionsByRequest(CommonRequest commonRequest);

    Integer getAllTransactionsCountByRequest(CommonRequest commonRequest);

    @Select("SELECT * FROM transactions WHERE type=#{type} AND userId=#{userId} AND date=#{date} limit 1")
    Transaction getTransactionByTypeUserIdAndDate(@Param("type") int inboundMessage,
                                                  @Param("userId") long userId,
                                                  @Param("date") long date);

    @Update("UPDATE transactions SET amount=#{amount}, details=#{details} WHERE id=#{id}")
    void updateTransactionAmountAndDetails(Transaction transaction);

    @Delete("DELETE FROM users WHERE id = #{userId}")
    void removeUserById(@Param("userId") Long userId);

    @Update("UPDATE users SET fullName=#{fullName}, personalName=#{personalName}, role=#{role}, domain=#{domain}, " +
            "allowManageMoney=#{allowManageMoney}, allowSubUsersPayments=#{allowSubUsersPayments}, " +
            "allowTransactionsView=#{allowTransactionsView}, allowSubUsersTransactionsView=#{allowSubUsersTransactionsView}, " +
            "allowPayments=#{allowPayments}, blocked=#{blocked}, disabled=#{disabled}, resellerId=#{resellerId} " +
            "WHERE id=#{id}")
    void updateUser(User user);

    @Update("UPDATE users SET password=#{password} WHERE id=#{id}")
    void updateUserPasswordById(@Param("id") long id,
                                @Param("password") String newPassword);


    void updateUserPassword(String password);

    @Delete("DELETE FROM autoReplies WHERE id=#{id}")
    void removeAutoReplyById(@Param("id") Long id);

    @Select("SELECT * FROM autoReplies WHERE userId=#{userId} ORDER by date DESC LIMIT #{limit} OFFSET #{offset}")
    List<AutoReply> getAutoRepliesByRequest(CommonRequest commonRequest);

    @Select("SELECT count(id) FROM autoReplies WHERE userId=#{userId}")
    Integer getAutoRepliesCountByRequest(CommonRequest commonRequest);

    @Update("UPDATE autoReplies SET keywords=#{keywords}, message=#{message} WHERE id=#{id}")
    void updateAutoReply(AutoReply autoReply);

    @Insert("INSERT INTO autoReplies(userId,date,keywords,message) VALUES (#{userId},#{date},#{keywords},#{message})")
    void insertAutoReply(AutoReply autoReply);

    @Select("SELECT * FROM autoReplies WHERE userId=#{userId}")
    List<AutoReply> getAutoRepliesByUserId(@Param("userId") long userId);

    List<Payment> gePaymentsByRequest(CommonRequest commonRequest);

    Integer getPaymentsCountByRequest(CommonRequest commonRequest);

    @Select("SELECT * FROM users WHERE resellerNumber=#{resellerNumber}")
    User getUserByResellerNumber(@Param("resellerNumber") Integer resellerNumber);

    @Update("UPDATE users SET resellerNumber=#{value} WHERE id=#{id}")
    void updateResellerNumberById(@Param("id") long id,
                                  @Param("value") int value);

    @Select("SELECT MAX(resellerNumber) FROM users")
    int getMaxResellerNumber();

    @Select("SELECT * FROM users WHERE domain=lower(#{domain}) LIMIT 1")
    User findResellerByDomain(@Param("domain") String requestHost);

    @Update("UPDATE users SET lastCampaignDate=#{date} WHERE id=#{id}")
    void updateLastCampaignDate(@Param("id") long userId,
                                @Param("date") long date);

    @Select("SELECT * FROM adminMessages WHERE userId=#{userId} ORDER BY date DESC")
    List<AdminMessage> getUserAdminMessages(@Param("userId") long userId);

    @Insert("INSERT INTO adminMessages(userId, message, date) VALUES (#{userId}, #{message}, #{date})")
    void insertAdminMessage(AdminMessage message);

    @Update("UPDATE adminMessages SET read=TRUE WHERE userId=#{userId}")
    void resetReadMessagesByUserId(@Param("userId") long userId);

    @Select("SELECT * FROM transactions WHERE date >= #{startDate} AND date < #{endDate} AND amount < 0")
    List<Transaction> getTransactionsStatisticsForDates(@Param("startDate") long startDate,
                                                        @Param("endDate") long endDate);

    @Select("SELECT * FROM transactions WHERE date = #{date} AND userId = #{userId} AND type = #{type}")
    Transaction getLastTransactionByDateUserAndType(@Param("date") long date,
                                                    @Param("userId") long userId,
                                                    @Param("type") int type);

    @Update("UPDATE users SET timezoneName=#{timezoneName}, timezoneOffset=#{timezoneOffset} WHERE id=#{id}")
    void updateUserTimeZone(User user);

    @Update("UPDATE chats_${userId} SET carrier=#{carrier} WHERE id=#{id}")
    void updateChatCarrierById(@Param("id") long id,
                               @Param("carrier") String carrier,
                               @Param("userId") long userId);

    @Select("SELECT * FROM users")
    List<User> getAllUsers();

    @Select("SELECT id, fullName FROM users WHERE role=3")
    List<User> getAllResellers();

    @Select("SELECT * FROM notes WHERE userId=#{id} ORDER BY DATE desc")
    List<Note> getUserNotes(User currentUser);

    @Insert("INSERT INTO notes(userId, date, message) VALUES(#{userId}, #{date}, #{message})")
    void insertNote(Note note);

    @Update("UPDATE notes SET message=#{message} WHERE id=#{id}")
    void updateNote(Note note);

    @Delete("DELETE FROM notes WHERE id=#{id}")
    void deleteNoteById(@Param("id") Long id);

    @Delete("DELETE FROM adminMessages WHERE id=#{id}")
    void deleteUserMessageById(@Param("id") Long id);

    @Select("SELECT * FROM adminMessages WHERE message LIKE 'Your caller ID [%' ORDER by date DESC LIMIT #{limit} OFFSET #{offset}")
    List<AdminMessage> getUserAdminMessagesAboutRemovedCallerIdByRequest(CommonRequest commonRequest);

    @Select("SELECT count(id) FROM adminMessages WHERE message LIKE 'Your caller ID [%'")
    Integer getUserAdminMessagesAboutRemovedCallerIdCountByRequest(CommonRequest commonRequest);

}


