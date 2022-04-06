package controllers;

import com.google.inject.Inject;
import common.Utils;
import model.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import services.database.dao.CampaignDAO;
import services.database.dao.SettingsDAO;
import services.database.dao.UserDAO;
import services.database.model.Campaign;
import services.database.model.Setting;
import services.database.model.User;
import services.sms.ISMSApiService;

import java.util.*;

import static services.database.model.User.DEFAULT_RESELLER_ID;

public class AuthController extends Controller {

    private UserDAO userDAO;
    private ISMSApiService smsApiService;
    private SettingsDAO settingsDAO;
    private CampaignDAO campaignDAO;

    private Random random = new Random();

    @Inject
    public AuthController(UserDAO userDAO,
                          SettingsDAO settingsDAO,
                          ISMSApiService smsApiService,
                          CampaignDAO campaignDAO) {
        this.userDAO = userDAO;
        this.settingsDAO = settingsDAO;
        this.smsApiService = smsApiService;
        this.campaignDAO = campaignDAO;
    }

    @With(LoggingAction.class)
    public Result auth(Http.Request request) {
        AuthRequest authRequest = Json.fromJson(request.body().asJson(), AuthRequest.class);

        User user = userDAO.findUserByUsernameOrEmail(authRequest.getUsername());
        if (user == null || !user.getPassword().equalsIgnoreCase(authRequest.getPassword()) ||
                user.getOrigin() == User.ORIGIN_APP2) {
            return ok(Json.toJson(CommonResponse.ERROR()));
        }

        String sessionId = UUID.randomUUID().toString();
        user.setPassword(sessionId);

        Setting autoReplySetting = settingsDAO.getSettingOrDefaultByKey("auto.reply.enabled_" + user.getId(), user.getResellerId());
        user.setAutoReplyEnabled(autoReplySetting.getSval());

        Setting agentLoginSetting = settingsDAO.getSettingOrDefaultByKey("agents.login.enabled_" + user.getId(), user.getResellerId());
        user.setAgentLoginEnabled(agentLoginSetting.getSval());

        Utils.registerCurrentUser(sessionId, user);

        return ok(Json.toJson(CommonResponse.OK(user)));
    }

    @With(LoggingAction.class)
    public Result authWithPinCode(Http.Request request) {
        AuthRequest authRequest = Json.fromJson(request.body().asJson(), AuthRequest.class);

        User user = userDAO.findUserByUsernameOrEmail(authRequest.getUsername());
        Integer pinCode = 0;

        try {
            pinCode = userDAO.getPinCodeByPhone(Utils.formatPhone(Long.parseLong(authRequest.getUsername())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!pinCode.equals(authRequest.getPinCode())) {
            return ok(Json.toJson(CommonResponse.ERROR()));
        }

        if (user == null) {
            user = new User(
                    authRequest.getUsername(),
                    User.ORIGIN_TEXTALLDATA,
                    User.ORIGIN_APP2,
                    null,
                    "",
                    System.currentTimeMillis(),
                    0,
                    authRequest.getFullName(),
                    User.DEFAULT_RESELLER_ID);

            userDAO.insertUser(user);
            user = userDAO.findUserByUsernameOrEmail(authRequest.getUsername());

            campaignDAO.createUserChatsTable(user);
            campaignDAO.createUserChatMessagesTable(user);
        }

        String sessionId = UUID.randomUUID().toString();
        user.setPassword(sessionId);

        Utils.registerCurrentUser(sessionId, user);

        return ok(Json.toJson(CommonResponse.OK(user)));
    }

    @With(LoggingAction.class)
    public Result sendPinCodeForLogin(Http.Request request) {
        SendPinCodeRequest sendPinCodeRequest = Json.fromJson(request.body().asJson(), SendPinCodeRequest.class);

        Long systemPhone = settingsDAO.getSystemPhone(User.DEFAULT_RESELLER_ID);
        Integer pinCode = 1000 + random.nextInt(8999);

        userDAO.updatePinCode(Utils.formatPhone(sendPinCodeRequest.getParsedPhone()), pinCode);

        String errorMessage = smsApiService.sendSMS(sendPinCodeRequest.getParsedPhone(), systemPhone, pinCode.toString(), -1);
        if (errorMessage == null) {
            return ok(Json.toJson(CommonResponse.OK()));
        } else {
            return ok(Json.toJson(CommonResponse.ERROR(errorMessage)));
        }
    }

    @With(LoggingAction.class)
    public Result authAs(Http.Request request) {
        User user = Json.fromJson(request.body().asJson(), User.class);

        String sessionId = UUID.randomUUID().toString();
        user.setPassword(sessionId);

        Utils.registerCurrentUser(sessionId, user);

        User currentUser = Utils.getCurrentUser(request, userDAO);
        if (currentUser != null && (currentUser.getRole() == User.ADMIN || currentUser.getRole() == User.RESELLER)) {
            return ok(Json.toJson(CommonResponse.OK(user)));
        }

        return forbidden();
    }

    @With(LoggingAction.class)
    public Result sendPinCode(Http.Request request) {
        SendPinCodeRequest sendPinCodeRequest = Json.fromJson(request.body().asJson(), SendPinCodeRequest.class);

        User user = userDAO.findUserByUsernameOrEmail(sendPinCodeRequest.getParsedPhone().toString());
        if (user != null) {
            return ok(Json.toJson(CommonResponse.ERROR("phone")));
        }

        user = userDAO.findUserByUsernameOrEmail("1" + sendPinCodeRequest.getParsedPhone().toString());
        if (user != null) {
            return ok(Json.toJson(CommonResponse.ERROR("phone")));
        }

        Long systemPhone = settingsDAO.getSystemPhone(User.DEFAULT_RESELLER_ID);
        Integer pinCode = 1000 + random.nextInt(8999);

        userDAO.updatePinCode(sendPinCodeRequest.getParsedPhone(), pinCode);

        String errorMessage = smsApiService.sendSMS(sendPinCodeRequest.getParsedPhone(), systemPhone, pinCode.toString(), -1);
        if (errorMessage == null) {
            return ok(Json.toJson(CommonResponse.OK()));
        } else {
            return ok(Json.toJson(CommonResponse.ERROR(errorMessage)));
        }
    }

    @With(LoggingAction.class)
    public Result register(Http.Request request) {
        RegisterRequest registerRequest = Json.fromJson(request.body().asJson(), RegisterRequest.class);

        Integer pinCode = userDAO.getPinCodeByPhone(registerRequest.getParsedPhone());
        if (!registerRequest.getCode().equals(pinCode)) {
            return ok(Json.toJson(CommonResponse.ERROR("code")));
        }

        long resellerId = User.DEFAULT_RESELLER_ID;

        Integer resellerNumber = registerRequest.getResellerNumber();
        if (resellerNumber != null) {
            User user = userDAO.getUserByResellerNumber(resellerNumber);
            if (user != null) {
                resellerId = user.getId();
            }
        }

        User reseller = getResellerByHostName(request);
        if (reseller != null) {
            resellerId = reseller.getId();
        }

        User user = new User(
                registerRequest.getParsedPhone().toString(),
                1,
                User.ORIGIN_TEXTALLDATA,
                null,
                registerRequest.getPassword(),
                System.currentTimeMillis(),
                0,
                registerRequest.getFullName(),
                resellerId);

        userDAO.insertUser(user);
        user = userDAO.findUserByUsernameOrEmail(registerRequest.getParsedPhone().toString());

        campaignDAO.createUserChatsTable(user);
        campaignDAO.createUserChatMessagesTable(user);

        return ok(Json.toJson(CommonResponse.OK()));
    }


    @With(LoggingAction.class)
    private User getResellerByHostName(Http.Request request) {
        Optional<String> requestHostOptional = request.header("Origin");

        if (requestHostOptional.isPresent()) {
            String requestHost = requestHostOptional.get().
                    replace("http://", "").
                    replace("https://", "").
                    replace("www.", "");

            return userDAO.findResellerByDomain(requestHost);
        }

        return null;
    }

    @With(LoggingAction.class)
    public Result sendResetPasswordPinCode(Http.Request request) {
        SendPinCodeRequest sendPinCodeRequest = Json.fromJson(request.body().asJson(), SendPinCodeRequest.class);

        User user = userDAO.findUserByUsernameOrEmail(sendPinCodeRequest.getParsedPhone().toString());
        if (user == null) {
            user = userDAO.findUserByUsernameOrEmail("1" + sendPinCodeRequest.getParsedPhone().toString());
        }

        if (user == null) {
            return ok(Json.toJson(CommonResponse.ERROR("phone")));
        }

        Long systemPhone = settingsDAO.getSystemPhone(User.DEFAULT_RESELLER_ID);
        Integer pinCode = 100000 + random.nextInt(899999);

        userDAO.updatePinCode(sendPinCodeRequest.getParsedPhone(), pinCode);

        String errorMessage = smsApiService.sendSMS(sendPinCodeRequest.getParsedPhone(), systemPhone, pinCode.toString(), -1);
        if (errorMessage == null) {
            return ok(Json.toJson(CommonResponse.OK()));
        } else {
            return ok(Json.toJson(CommonResponse.ERROR(errorMessage)));
        }
    }

    @With(LoggingAction.class)
    public Result resetPassword(Http.Request request) {
        ResetPasswordRequest resetPasswordRequest = Json.fromJson(request.body().asJson(), ResetPasswordRequest.class);

        Integer pinCode = userDAO.getPinCodeByPhone(resetPasswordRequest.getParsedPhone());
        if (!resetPasswordRequest.getCode().equals(pinCode)) {
            return ok(Json.toJson(CommonResponse.ERROR("code")));
        }

        User user = userDAO.findUserByUsernameOrEmail(resetPasswordRequest.getParsedPhone().toString());
        if (user == null) {
            user = userDAO.findUserByUsernameOrEmail("1" + resetPasswordRequest.getParsedPhone().toString());
        }

        if (user == null) {
            return ok(Json.toJson(CommonResponse.ERROR("phone")));
        }

        userDAO.updateUserPasswordById(user.getId(), resetPasswordRequest.getPassword());
        userDAO.updatePinCode(resetPasswordRequest.getParsedPhone(), -1);

        return ok(Json.toJson(CommonResponse.OK()));
    }

}
