package controllers;

import com.google.inject.Inject;
import common.Utils;
import model.CommonResponse;
import model.KycInfo;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import services.database.dao.KycInfoDAO;
import services.database.dao.UserDAO;
import services.database.model.AdminMessage;
import services.database.model.User;

public class KycInfoController extends Controller {

    private final UserDAO userDAO;
    private final KycInfoDAO kycInfoDAO;


    @Inject
    public KycInfoController(UserDAO userDAO, KycInfoDAO kycInfoDAO) {
        this.userDAO = userDAO;
        this.kycInfoDAO = kycInfoDAO;
    }

    @With(LoggingAction.class)
    public Result getKycInfo(Http.Request request) {
        try {
            User user = Utils.getCurrentUser(request, userDAO);
            if (user != null && user.getAdminUserId() > 0) {
                user = userDAO.getUserById(user.getAdminUserId());
            }

            if (user == null) {
                return forbidden();
            }

            KycInfo kycInfo = kycInfoDAO.getKycInfoByUserId(user.getId());


            return ok(Json.toJson(CommonResponse.OK(kycInfo)));

        } catch (Exception e) {
            e.printStackTrace();
            return ok(Json.toJson(CommonResponse.ERROR(e.getMessage())));
        }
    }

    @With(LoggingAction.class)
    public Result updateOrInsertKycInfo(Http.Request request) {
        try {
            User user = Utils.getCurrentUser(request, userDAO);
            KycInfo kycInfo = Json.fromJson(request.body().asJson(), KycInfo.class);
            if (user != null && user.getAdminUserId() > 0) {
                user = userDAO.getUserById(user.getAdminUserId());
            }

            if (user == null) {
                return forbidden();
            }

            KycInfo currentKycInfo = kycInfoDAO.getKycInfoByUserId(user.getId());
            kycInfo.setUserId(user.getId());
            kycInfo.setLastUpdate(System.currentTimeMillis());
            boolean wasNotCompleted = currentKycInfo != null && !currentKycInfo.isCompleted();

            if (currentKycInfo != null) {
                currentKycInfo.updateKycInfo(kycInfo);
                kycInfoDAO.updateKycInfo(currentKycInfo);
                // TODO DELETE THIS WHEN 7 USERS WITH HALF FORM HAVE FILLED IT
                user.setDisabled(false);
                userDAO.updateUser(user);
            } else {
                kycInfoDAO.insertKycInfo(kycInfo);
                // ENABLING ACCOUNT IF THEY FILLED UP THE KYC INFO FOR THE FIRST TIME
                user.setDisabled(false);
                userDAO.updateUser(user);
            }

            if (wasNotCompleted && kycInfo.isCompleted()) {
                AdminMessage newUserCompleted = new AdminMessage();
                newUserCompleted.setUserId(User.DEFAULT_RESELLER_ID);
                newUserCompleted.setMessage("User " + user.getUsername() + " has completed KYC info.");
                newUserCompleted.setDate(System.currentTimeMillis());

                userDAO.insertAdminMessage(newUserCompleted);
            }

            return ok(Json.toJson(CommonResponse.OK(kycInfo)));

        } catch (Exception e) {
            e.printStackTrace();
            return ok(Json.toJson(CommonResponse.ERROR(e.getMessage())));
        }
    }

}
