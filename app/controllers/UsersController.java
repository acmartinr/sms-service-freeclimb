package controllers;

import com.google.inject.Inject;
import common.Utils;
import model.*;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.dao.CampaignDAO;
import services.database.dao.SettingsDAO;
import services.database.dao.UserDAO;
import services.database.model.*;
import services.sms.ISMSApiService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class UsersController extends Controller {
    final Logger logger = LoggerFactory.getLogger("access");

    private UserDAO userDAO;
    private CampaignDAO campaignDAO;
    private SettingsDAO settingsDAO;

    private ISMSApiService smsApiService;

    private String tempDirectoryPath;
    private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    @Inject
    public UsersController(UserDAO userDAO,
                           CampaignDAO campaignDAO,
                           SettingsDAO settingsDAO,
                           ISMSApiService smsApiService) {
        this.userDAO = userDAO;
        this.settingsDAO = settingsDAO;
        this.campaignDAO = campaignDAO;
        this.smsApiService = smsApiService;
    }

    @With(LoggingAction.class)
    public Result updateUserTimeZone(Http.Request request) {
        User user = Json.fromJson(request.body().asJson(), User.class);
        User currentUser = Utils.getCurrentUser(request, userDAO);

        if (currentUser != null && user.getId() == currentUser.getId()) {
            userDAO.updateUserTimeZone(user);
        }

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result users(Http.Request request) {
        CommonRequest commonRequest = Json.fromJson(request.body().asJson(), CommonRequest.class);
        User currentUser = Utils.getCurrentUser(request, userDAO);
        if (currentUser != null && currentUser.getRole() == User.RESELLER) {
            commonRequest.setResellerId(currentUser.getId());
        }

        List<User> users = userDAO.getUsersByRequest(commonRequest);
        for (User user : users) {
            user.setPassword(null);
        }

        return ok(Json.toJson(CommonResponse.OK(new UsersListsResponse(
                users,
                userDAO.getUsersCountByRequest(commonRequest)
        ))));
    }

    @With(LoggingAction.class)
    public Result resellers(Http.Request request) {
        User currentUser = Utils.getCurrentUser(request, userDAO);
        if (currentUser != null && currentUser.getRole() != User.ADMIN) {
            return ok(Json.toJson(CommonResponse.OK(new LinkedList<>())));
        }

        return ok(Json.toJson(CommonResponse.OK(userDAO.getAllResellers())));
    }

    @With(LoggingAction.class)
    public Result payments(Http.Request request) {
        CommonRequest commonRequest = Json.fromJson(request.body().asJson(), CommonRequest.class);
        User currentUser = Utils.getCurrentUser(request, userDAO);
        if (currentUser != null && currentUser.getRole() == User.RESELLER) {
            commonRequest.setResellerId(currentUser.getId());
        }

        List<Payment> payments = userDAO.gePaymentsByRequest(commonRequest);

        return ok(Json.toJson(CommonResponse.OK(new PaymentsListsResponse(
                payments,
                userDAO.getPaymentsCountByRequest(commonRequest)
        ))));
    }

    @With(LoggingAction.class)
    public Result updateUser(Http.Request request) {
        User user = Json.fromJson(request.body().asJson(), User.class);
        userDAO.updateUser(user);

        if (user.getPassword() != null && user.getPassword().length() > 0) {
            userDAO.updateUserPasswordById(user.getId(), user.getPassword());
        }

        if (user.getRole() == User.RESELLER) {
            userDAO.updateUserAsReseller(user.getId());
            settingsDAO.updateResellerSettings(user.getId());
        } else {
            userDAO.updateResellerNumberById(user.getId(), 0);
        }

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result userBalance(Http.Request request, Long id) {
        User user = Utils.getCurrentUser(request, userDAO);
        if (user != null && user.getAdminUserId() > 0) {
            user = userDAO.getUserById(user.getAdminUserId());
        }

        if (user == null) {
            return forbidden();
        }

        return ok(Json.toJson(CommonResponse.OK(user.getBalance()))).withNewSession();
    }

    @With(LoggingAction.class)
    public Result userManageMoneyDetails(Http.Request request, Long id) {
        User user = Utils.getCurrentUser(request, userDAO);
        if (user != null && user.getAdminUserId() > 0) {
            user = userDAO.getUserById(user.getAdminUserId());
        }

        if (user == null) {
            return forbidden();
        }

        return ok(Json.toJson(CommonResponse.OK(user.isAllowManageMoney())));
    }

    @With(LoggingAction.class)
    public Result userMessagesAndBalance(Http.Request request, Long id) {
        User user = Utils.getCurrentUser(request, userDAO);
        if (user != null && user.getAdminUserId() > 0) {
            user = userDAO.getUserById(user.getAdminUserId());
        }

        if (user == null) {
            return forbidden();
        }

        List<AdminMessage> messages = userDAO.getUserAdminMessages(user.getId());
        return ok(Json.toJson(CommonResponse.OK(
                new UserInfoResponse(messages, user.getBalance())))).withNewSession();
    }

    @With(LoggingAction.class)
    public Result userMessages(Long id) {
        List<AdminMessage> messages = userDAO.getUserAdminMessages(id);
        return ok(Json.toJson(CommonResponse.OK(messages)));
    }

    @With(LoggingAction.class)
    public Result deleteMessage(Long id) {
        userDAO.deleteUserMessageById(id);
        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result sendMessageToUser(Http.Request request) {
        AdminMessage message = Json.fromJson(request.body().asJson(), AdminMessage.class);
        message.setDate(System.currentTimeMillis());



        userDAO.insertAdminMessage(message);

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result resetReadMessages(Http.Request request) {
        User user = Utils.getCurrentUser(request, userDAO);
        if (user != null && user.getAdminUserId() > 0) {
            user = userDAO.getUserById(user.getAdminUserId());
        }

        if (user == null) {
            return forbidden();
        }

        userDAO.resetReadMessagesByUserId(user.getId());

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result userUISettings(Long id) {
        User user = userDAO.getUserById(id);

        List<Setting> settings = new LinkedList();
        if (user != null) {
            settings.add(settingsDAO.getSettingOrDefaultByKey("auto.reply.enabled_" + id, user.getResellerId()));
            settings.add(settingsDAO.getSettingOrDefaultByKey("agents.login.enabled_" + id, user.getResellerId()));
            settings.add(settingsDAO.getSettingOrDefaultByKey("master.ignore.enabled_" + id, user.getResellerId()));
            settings.add(settingsDAO.getSettingOrDefaultByKey("carrier.ignore.enabled_" + id, user.getResellerId()));
            settings.add(settingsDAO.getSettingOrDefaultByKey("phones.bulk.forward.enabled_" + id, user.getResellerId()));
            settings.add(new Setting("paymentAvailable", isPaymentAvailable(user) ? "1" : "0"));
            settings.add(settingsDAO.getSettingOrDefaultByKey("chat.carrier.lookup.enabled_" + id, user.getResellerId()));
            settings.add(new Setting("allowSubUsersPayments", user.isAllowSubUsersPayments() ? "1" : "0"));
            settings.add(settingsDAO.getSettingOrDefaultByKey("download.lists.enabled_" + id, user.getResellerId()));
            settings.add(new Setting("allowTransactionsView", isAllowedTransactionsView(user) ? "1" : "0"));
            settings.add(new Setting("allowSubUsersTransactionsView", user.isAllowSubUsersTransactionsView() ? "1" : "0"));
            settings.add(settingsDAO.getSettingOrDefaultByKey("consumerdnc.upload.filter.ignore_" + id, user.getResellerId()));
        }

        return ok(Json.toJson(CommonResponse.OK(settings)));
    }

    private boolean isPaymentAvailable(User user) {
        if (user.getResellerId() > 0) {
            User reseller = userDAO.getUserById(user.getResellerId());
            if (reseller != null) {
                return user.isAllowPayments() && reseller.isAllowSubUsersPayments();
            }
        }

        return user.isAllowPayments();
    }

    private boolean isAllowedTransactionsView(User user) {
        if (user.getResellerId() > 0) {
            User reseller = userDAO.getUserById(user.getResellerId());
            if (reseller != null) {
                return user.isAllowTransactionsView() && reseller.isAllowSubUsersTransactionsView();
            }
        }

        return user.isAllowTransactionsView();
    }

    @With(LoggingAction.class)
    public Result userSettings(Long id) {

        User user = userDAO.getUserById(id);
        List<Setting> settings = new LinkedList();
        String[] userSettings = new String[]{
                "price.phone_" + id,
                "price.sms.inbound_" + id,
                "price.sms.outbound_" + id,
                //"auto.reply.enabled_" + id,
                "chat.carrier.lookup.enabled_" + id,
                "agents.login.enabled_" + id,
                "master.ignore.enabled_" + id,
                "carrier.ignore.enabled_" + id,
                "phones.bulk.forward.enabled_" + id,
                "download.lists.enabled_" + id,
                "surcharge.factor_" + id,
                "consumerdnc.upload.filter.ignore_" + id};

        for (String userSetting : userSettings) {
            Setting setting = settingsDAO.getSettingByKey(userSetting, user.getResellerId());
            if (setting == null) {
                setting = new Setting(
                        userSetting,
                        settingsDAO.getSettingByKey(
                                userSetting.split("_")[0],
                                user.getResellerId()).getSval());
            }
            settings.add(setting);
        }

        return ok(Json.toJson(CommonResponse.OK(settings)));
    }

    @With(LoggingAction.class)
    public Result updateAutoReply(Http.Request request) {
        AutoReply autoReply = Json.fromJson(request.body().asJson(), AutoReply.class);
        if (autoReply.getId() > 0) {
            userDAO.updateAutoReply(autoReply);
        } else {
            userDAO.insertAutoReply(autoReply);
        }

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result autoReplies(Http.Request request) {
        CommonRequest commonRequest = Json.fromJson(request.body().asJson(), CommonRequest.class);
        List<AutoReply> autoReplies = userDAO.getAutoRepliesByRequest(commonRequest);

        return ok(Json.toJson(CommonResponse.OK(new AutoRepliesResponse(
                autoReplies,
                userDAO.getAutoRepliesCountByRequest(commonRequest)
        ))));
    }

    @With(LoggingAction.class)
    public Result removeAutoReply(Long id) {
        userDAO.removeAutoReplyById(id);
        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result addFund(Http.Request request) {
        AddFundRequest addFundRequest = Json.fromJson(request.body().asJson(), AddFundRequest.class);

        userDAO.changeUserBalance(Transaction.ADD_FUND_MANUALLY, addFundRequest.getUserId(), addFundRequest.getValue());
        userDAO.insertTransaction(
                new Transaction(
                        addFundRequest.getUserId(), addFundRequest.getValue(),
                        Transaction.ADD_FUND_MANUALLY, System.currentTimeMillis(),
                        "added by admin"));

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result transactions(Http.Request request) {
        CommonRequest commonRequest = Json.fromJson(request.body().asJson(), CommonRequest.class);

        return ok(Json.toJson(CommonResponse.OK(new TransactionsListsResponse(
                userDAO.getTransactionsByRequest(commonRequest),
                userDAO.getTransactionsCountByRequest(commonRequest)
        ))));
    }

    @With(LoggingAction.class)
    public Result allTransactions(Http.Request request) {
        CommonRequest commonRequest = Json.fromJson(request.body().asJson(), CommonRequest.class);
        User currentUser = Utils.getCurrentUser(request, userDAO);

        assert currentUser != null;
        updateStartDate(commonRequest, currentUser);
        updateEndDate(commonRequest, currentUser);

        return ok(Json.toJson(CommonResponse.OK(new TransactionsListsResponse(
                userDAO.getAllTransactionsByRequest(commonRequest),
                userDAO.getAllTransactionsCountByRequest(commonRequest)
        ))));
    }

    @With(LoggingAction.class)
    public Result dailyStatistics(Http.Request request) {
        CommonRequest commonRequest = Json.fromJson(request.body().asJson(), CommonRequest.class);
// Fixing delay time
        long date = Instant.now().truncatedTo(ChronoUnit.DAYS)
//                .plus(Duration.ofHours(12))
                .toEpochMilli();

        int inboundCount = calculateCount(userDAO.getLastTransactionByDateUserAndType(date, commonRequest.getUserId(), Transaction.INBOUND_MESSAGE));
        int outboundCount = calculateCount(userDAO.getLastTransactionByDateUserAndType(date, commonRequest.getUserId(), Transaction.OUTBOUND_MESSAGE));

        Map<String, String> result = new LinkedHashMap();
        result.put("Inbound message(s) count", Integer.toString(inboundCount));
        result.put("Outbound message(s) count", Integer.toString(outboundCount));

        if (outboundCount > 0) {
            result.put("In/Out Ratio", ((inboundCount / (float) outboundCount) * 100) + "%");
        } else {
            result.put("In/Out Ratio", "not available yet");
        }

        return ok(Json.toJson(CommonResponse.OK(result)));
    }

    private int calculateCount(Transaction transaction) {
        int count = 0;

        if (transaction != null) {
            String details = transaction.getDetails();
            if (details.contains(":")) {
                try {
                    count = count + Integer.parseInt(details.split(":")[1].trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return count;
    }

    @With(LoggingAction.class)
    public Result transactionsStatistics(Http.Request request) {
        CommonRequest commonRequest = Json.fromJson(request.body().asJson(), CommonRequest.class);
        User currentUser = Utils.getCurrentUser(request, userDAO);

        updateStartDate(commonRequest, currentUser);
        updateEndDate(commonRequest, currentUser);

        logger.info("Getting statistics from " + commonRequest.getStartDate() + " to " + commonRequest.getEndDate());

        Map<Integer, TransactionStatistics> typeToStatistics = new HashMap();

        Float phonePrice = settingsDAO.getPhonePrice(User.DEFAULT_RESELLER_ID, User.DEFAULT_RESELLER_ID);
        Float inboundPrice = settingsDAO.getInboundMessagePrice(User.DEFAULT_RESELLER_ID, User.DEFAULT_RESELLER_ID);
        Float outboundPrice = settingsDAO.getOutboundMessagePrice(User.DEFAULT_RESELLER_ID, User.DEFAULT_RESELLER_ID);
        Float carrierLookupPrice = settingsDAO.getLookupPrice(User.DEFAULT_RESELLER_ID, User.DEFAULT_RESELLER_ID);

        List<Transaction> transactions = userDAO.getTransactionsStatisticsForDates(commonRequest.getStartDate(), commonRequest.getEndDate());
        for (Transaction transaction : transactions) {
            int count = 1;
            String details = transaction.getDetails();
            if (details.contains(":")) {
                try {
                    count = Integer.parseInt(details.split(":")[1].trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (!typeToStatistics.containsKey(transaction.getType())) {
                typeToStatistics.put(transaction.getType(), new TransactionStatistics(0, 0, transaction.getType()));
            }

            TransactionStatistics transactionStatistics = typeToStatistics.get(transaction.getType());

            switch (transaction.getType()) {
                case Transaction.PHONE_BUY:
                case Transaction.PHONE_RENEW:
                    transactionStatistics.update(count, count * Math.abs(phonePrice));
                    break;
                case Transaction.INBOUND_MESSAGE:
                    transactionStatistics.update(count, count * Math.abs(inboundPrice));
                    break;
                case Transaction.OUTBOUND_MESSAGE:
                    transactionStatistics.update(count, count * Math.abs(outboundPrice));
                    break;
                case Transaction.CARRIER_SURCHARGE:
                    transactionStatistics.update(count, Math.abs(transaction.getAmount()));
                    break;
                case Transaction.CARRIER_LOOKUP:
                    transactionStatistics.update(count, count * Math.abs(carrierLookupPrice));
                    break;
            }
        }

        return ok(Json.toJson(CommonResponse.OK(typeToStatistics.values())));
    }

    private void updateEndDate(CommonRequest commonRequest, User user) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(user.getTimezoneName()));

        calendar.setTimeInMillis(commonRequest.getEndDate());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        commonRequest.setEndDate(calendar.getTimeInMillis());
    }

    private void updateStartDate(CommonRequest commonRequest, User user) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(user.getTimezoneName()));

        calendar.setTimeInMillis(commonRequest.getStartDate());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        commonRequest.setStartDate(calendar.getTimeInMillis());
    }

    @With(LoggingAction.class)
    public Result removeUser(Long userId) {
        List<Phone> phones = campaignDAO.getPhonesByUserId(userId);
        for (Phone phone : phones) {
            smsApiService.releasePhone(phone.getPhone());
        }

        userDAO.removeUserById(userId);

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result exportUsersToFile(Http.Request request) throws Exception {
        CommonRequest commonRequest = Json.fromJson(request.body().asJson(), CommonRequest.class);
        User currentUser = Utils.getCurrentUser(request, userDAO);
        if (currentUser.getRole() == User.RESELLER) {
            commonRequest.setResellerId(currentUser.getId());
        }

        List<User> users = userDAO.getUsersByRequest(commonRequest);
        String fileName = writeUsersToFile(users);

        return ok(Json.toJson(CommonResponse.OK(fileName)));
    }

    private String writeUsersToFile(List<User> users) throws Exception {
        File file = File.createTempFile("users", "csv");
        FileWriter fileWriter = new FileWriter(file);

        fileWriter.write("phone,business name,name,email,balance\n");
        for (User user : users) {
            fileWriter.write(
                    formatString(user.getUsername()) + "," +
                            formatString(user.getFullName()) + "," +
                            formatString(user.getPersonalName()) + "," +
                            formatString(user.getEmail()) + "," +
                            user.getBalance() + "\n");
        }

        fileWriter.flush();
        fileWriter.close();

        return file.getName();
    }

    private String formatString(String value) {
        return value == null ? "" : "\"" + value + "\"";
    }

    @With(LoggingAction.class)
    public Result downloadExportedUsersFile(Http.Request request, String path) throws Exception {
        if (tempDirectoryPath == null) {
            File file = File.createTempFile("temp", "temp");
            tempDirectoryPath = file.getParentFile().getAbsolutePath();
        }

        File file = new File(tempDirectoryPath, path);
        String fileName = "users_" + dateFormat.format(new Date()) + ".csv";

        return ok(new FileInputStream(file)).as("text/csv").
                withHeader("Content-disposition", "attachment; filename=" + fileName);
    }

    @With(LoggingAction.class)
    public Result notes(Http.Request request) {
        User currentUser = Utils.getCurrentUser(request, userDAO);
        return ok(Json.toJson(CommonResponse.OK(userDAO.getUserNotes(currentUser))));
    }

    @With(LoggingAction.class)
    public Result updateNote(Http.Request request) {
        Note note = Json.fromJson(request.body().asJson(), Note.class);
        if (note.getId() > 0) {
            userDAO.updateNote(note);
        } else {
            userDAO.insertNote(note);
        }

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result deleteNote(Long id) {
        userDAO.deleteNoteById(id);
        return ok(Json.toJson(CommonResponse.OK()));
    }
}
