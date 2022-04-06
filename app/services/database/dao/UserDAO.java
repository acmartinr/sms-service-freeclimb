package services.database.dao;

import com.google.inject.Inject;
import common.Utils;
import model.CommonRequest;
import model.CommonResponse;
import play.libs.Json;
import services.database.mapper.UserMapper;
import services.database.model.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class UserDAO {

    private UserMapper mapper;
    private CampaignDAO campaignDAO;

    @Inject
    public UserDAO(UserMapper mapper,
                   CampaignDAO campaignDAO) {
        this.mapper = mapper;
        this.campaignDAO = campaignDAO;
    }

    public User findUserByUsernameOrEmail(String username) {
        User user = mapper.findUserByUsernameOrEmail(username);
        if (user == null) {
            Campaign campaign = campaignDAO.getCampaignByAgentUsername(username);
            if (campaign != null) {
                User adminUser = getUserById(campaign.getUserId());
                if (adminUser.isBlocked()) {
                    return null;
                }

                user = new User(
                        campaign.getAgentUsername(),
                        campaign.getAgentPassword(),
                        User.LIMITED,
                        adminUser.getBalance(),
                        adminUser.getId(),
                        adminUser.getResellerId());
            }
        }

        return user;
    }

    public List<User> getUsersByRequest(CommonRequest commonRequest) {
        return mapper.getUsersByRequest(commonRequest);
    }

    public int getUsersCountByRequest(CommonRequest commonRequest) {
        Integer value = mapper.getUsersCountByRequest(commonRequest);
        return value == null ? 0 : value;
    }

    public void updatePinCode(Long phone, int pinCode) {
        phone = Utils.formatPhone(phone);

        mapper.removePinCodesForPhones(phone);
        mapper.insertPinCode(phone, pinCode);
    }

    public Integer getPinCodeByPhone(long phone) {
        return mapper.getPinCodeByPhone(Utils.formatPhone(phone));
    }

    public void insertUser(User user) {
        mapper.insertUser(user);
    }

    public void changeUserBalance(int transactionType, long userId, float value) {
        insertDetailedTransaction(userId, value, transactionType);
        mapper.changeUserBalance(userId, value);
    }

    public void changeUserBalanceAndUpdateResellerBalance(int transactionType, long userId, float value, float baseValue) {
        changeUserBalanceAndUpdateResellerBalance(transactionType, userId, value, baseValue, null);
    }

    public void changeUserBalanceAndUpdateResellerBalance(int transactionType, long userId, float value, float baseValue, Transaction transaction) {
        mapper.changeUserBalance(userId, value);
        insertDetailedTransaction(userId, value, transactionType);

        if (transaction != null) {
            insertTransaction(transaction);
        }

        User user = getUserById(userId);
        User reseller = getUserById(user.getResellerId());
        if (reseller != null && reseller.getRole() != User.ADMIN && baseValue != value) {
            if (reseller.isAllowManageMoney()) {
                mapper.changeUserBalance(reseller.getId(), baseValue);
                insertDetailedTransaction(reseller.getId(), baseValue, transactionType + 10);

                if (transaction != null) {
                    transaction.setAmount(baseValue);
                    transaction.setUserId(reseller.getId());
                    transaction.setType(transaction.getType() + 10);
                    insertTransaction(transaction);
                }
            } else {
                mapper.changeUserBalance(reseller.getId(), baseValue - value);
                insertDetailedTransaction(reseller.getId(), baseValue - value, transactionType + 10);
            }
        }
    }

    private void insertDetailedTransaction(long userId, float value, int type) {
        Transaction transaction = new Transaction();
        transaction.setAmount(value);
        transaction.setUserId(userId);
        transaction.setType(type);
        transaction.setDate(System.currentTimeMillis());

        mapper.insertDetailedTransaction(transaction);
    }

    public User getUserById(long userId) {
        return mapper.getUserById(userId);
    }

    public void insertTransaction(Transaction transaction) {
        mapper.insertTransaction(transaction);
    }

    public List<Transaction> getTransactionsByRequest(CommonRequest commonRequest) {
        return mapper.getTransactionsByRequest(commonRequest);
    }

    public int getTransactionsCountByRequest(CommonRequest commonRequest) {
        Integer value = mapper.getTransactionsCountByRequest(commonRequest);
        return value == null ? 0 : value;
    }

    public List<Transaction> getAllTransactionsByRequest(CommonRequest commonRequest) {
        return mapper.getAllTransactionsByRequest(commonRequest);
    }

    public int getAllTransactionsCountByRequest(CommonRequest commonRequest) {
        Integer value = mapper.getAllTransactionsCountByRequest(commonRequest);
        return value == null ? 0 : value;
    }

    public void updateCountTransaction(int type, long userId, float amount, float baseAmount, String details) {
        updateCountTransaction(type, userId, amount, baseAmount, details, 1);
    }

    public void updateCountTransaction(int type, long userId, float amount, float baseAmount, String details, int delta) {
        User user = getUserById(userId);

        long date;
//        if (user.getTimezoneName() != null) {
        LocalDateTime dateTime = LocalDateTime.now();
        // it stores only one transaction type per day at the middle (12:00) of that day always using UTC timezone
        Instant instant = dateTime.atZone(ZoneId.of("UTC")).toInstant();
        date = instant.truncatedTo(ChronoUnit.DAYS)
                .plus(Duration.ofHours(12))
                .toEpochMilli();
//        } else {
//        date = Instant.now().truncatedTo(ChronoUnit.DAYS)
////                    .plus(Duration.ofHours(12))
//                .toEpochMilli();
//        }

        Transaction transaction = mapper.getTransactionByTypeUserIdAndDate(type, userId, date);
        if (transaction != null) {
            long count = Long.parseLong(transaction.getDetails().toLowerCase().split(details)[1]);
            count = count + delta;
            transaction.setDetails(details + count);

            transaction.setAmount(transaction.getAmount() + amount);

            mapper.updateTransactionAmountAndDetails(transaction);
        } else {
            transaction = new Transaction(
                    userId, amount, type,
                    date, details + delta);
            mapper.insertTransaction(transaction);
        }

        if (baseAmount != 0) {
            if (user != null && user.getResellerId() > 0) {
                User reseller = getUserById(user.getResellerId());
                if (reseller != null) {
                    updateCountTransaction(type + 10, reseller.getId(), baseAmount, 0, details, delta);
                }
            }
        }
    }

    public void removeUserById(Long userId) {
        mapper.removeUserById(userId);
    }

    public void updateUser(User user) {
        mapper.updateUser(user);
    }

    public void updateUserPasswordById(long id, String newPassword) {
        mapper.updateUserPasswordById(id, newPassword);
    }

    public void removeAutoReplyById(Long id) {
        mapper.removeAutoReplyById(id);
    }

    public List<AutoReply> getAutoRepliesByRequest(CommonRequest commonRequest) {
        return mapper.getAutoRepliesByRequest(commonRequest);
    }

    public int getAutoRepliesCountByRequest(CommonRequest commonRequest) {
        Integer count = mapper.getAutoRepliesCountByRequest(commonRequest);
        return count == null ? 0 : count;
    }

    public void updateAutoReply(AutoReply autoReply) {
        mapper.updateAutoReply(autoReply);
    }

    public void insertAutoReply(AutoReply autoReply) {
        autoReply.setDate(System.currentTimeMillis());
        mapper.insertAutoReply(autoReply);
    }

    public List<AutoReply> getAutoRepliesByUserId(long userId) {
        return mapper.getAutoRepliesByUserId(userId);
    }

    public List<Payment> gePaymentsByRequest(CommonRequest commonRequest) {
        return mapper.gePaymentsByRequest(commonRequest);
    }

    public int getPaymentsCountByRequest(CommonRequest commonRequest) {
        Integer count = mapper.getPaymentsCountByRequest(commonRequest);
        return count == null ? 0 : count;
    }

    public User getUserByResellerNumber(Integer resellerNumber) {
        return mapper.getUserByResellerNumber(resellerNumber);
    }

    public void updateResellerNumberById(long id, int value) {
        mapper.updateResellerNumberById(id, value);
    }

    public void updateUserAsReseller(long id) {
        User user = getUserById(id);
        if (user.getResellerNumber() == 0) {
            mapper.updateResellerNumberById(user.getId(), mapper.getMaxResellerNumber() + 1);
        }
    }

    public User findResellerByDomain(String requestHost) {
        return mapper.findResellerByDomain(requestHost);
    }

    public void updateLastCampaignDate(long userId, long date) {
        mapper.updateLastCampaignDate(userId, date);
    }

    public List<AdminMessage> getUserAdminMessages(long userId) {
        return mapper.getUserAdminMessages(userId);
    }

    public void insertAdminMessage(AdminMessage message) {
        mapper.insertAdminMessage(message);
    }

    public void resetReadMessagesByUserId(long userId) {
        mapper.resetReadMessagesByUserId(userId);
    }

    public List<Transaction> getTransactionsStatisticsForDates(long startDate, long endDate) {
        return mapper.getTransactionsStatisticsForDates(startDate, endDate);
    }

    public Transaction getLastTransactionByDateUserAndType(long date, long userId, int type) {
        return mapper.getLastTransactionByDateUserAndType(date, userId, type);
    }

    public void updateUserTimeZone(User user) {
        mapper.updateUserTimeZone(user);
    }

    public void updateChatCarrierById(long id, long userId, String carrier) {
        mapper.updateChatCarrierById(id, carrier, userId);
    }

    public List<User> getAllUsers() {
        return mapper.getAllUsers();
    }

    public List<User> getAllResellers() {
        return mapper.getAllResellers();
    }

    public List<Note> getUserNotes(User currentUser) {
        return mapper.getUserNotes(currentUser);
    }

    public void updateNote(Note note) {
        mapper.updateNote(note);
    }

    public void insertNote(Note note) {
        mapper.insertNote(note);
    }

    public void deleteNoteById(Long id) {
        mapper.deleteNoteById(id);
    }

    public void deleteUserMessageById(Long id) {
        mapper.deleteUserMessageById(id);
    }

    public List<AdminMessage> getUserAdminMessagesAboutRemovedCallerIdByRequest(CommonRequest commonRequest) {
        return mapper.getUserAdminMessagesAboutRemovedCallerIdByRequest(commonRequest);
    }

    public int getUserAdminMessagesAboutRemovedCallerIdCountByRequest(CommonRequest commonRequest) {
        Integer value = mapper.getUserAdminMessagesAboutRemovedCallerIdCountByRequest(commonRequest);
        return value == null ? 0 : value;
    }
}
