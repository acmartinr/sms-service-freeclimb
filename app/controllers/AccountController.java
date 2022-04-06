package controllers;

import com.google.inject.Inject;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import model.AddPaymentRequest;
import model.ChangePasswordRequest;
import model.CommonResponse;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import services.database.dao.UserDAO;
import services.database.model.Transaction;
import services.database.model.User;

import java.util.HashMap;
import java.util.Map;

import static play.mvc.Results.ok;

public class AccountController {

    private UserDAO userDAO;

    @Inject
    public AccountController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @With(LoggingAction.class)
    public Result changePassword(Http.Request request) {
        Result result;
        ChangePasswordRequest changePasswordRequest = Json.fromJson(request.body().asJson(), ChangePasswordRequest.class);

        User user = userDAO.findUserByUsernameOrEmail(changePasswordRequest.getUsername());
        if (user != null && user.getPassword().equals(changePasswordRequest.getOldPassword())) {
            userDAO.updateUserPasswordById(user.getId(), changePasswordRequest.getNewPassword());

            result = ok(Json.toJson(CommonResponse.OK()));
        } else {
            result = ok(Json.toJson(CommonResponse.ERROR()));
        }

        return result;
    }

    @With(LoggingAction.class)
    public Result addPayment(Http.Request request) {
        try {
            AddPaymentRequest addPaymentRequest = Json.fromJson(request.body().asJson(), AddPaymentRequest.class);
            User user = userDAO.findUserByUsernameOrEmail(addPaymentRequest.getUsername());

            if (!addPaymentRequest.isTest()) {
                Stripe.apiKey = user.getResellerId() == 214 ?
                        "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" :
                        "sk_live_xiZ2LceSbYjQsffHcp0Woqmn00X24ASBSQ";

                Map<String, Object> params = new HashMap();
                params.put("amount", (int) (addPaymentRequest.getAmount() * 100));
                params.put("currency", "usd");
                params.put("description", String.valueOf(user.getId()));
                params.put("source", addPaymentRequest.getToken());

                Charge.create(params);
            }

            userDAO.changeUserBalance(Transaction.ADD_FUND_MANUALLY, user.getId(), addPaymentRequest.getAmount());
            userDAO.insertTransaction(new Transaction(
                    user.getId(),
                    addPaymentRequest.getAmount(),
                    Transaction.ADD_FUND_MANUALLY,
                    System.currentTimeMillis(),
                    "added with Stripe"));

            return ok(Json.toJson(CommonResponse.OK()));
        } catch (Exception e) {
            e.printStackTrace();
            return ok(Json.toJson(CommonResponse.ERROR(e.getMessage())));
        }
    }

}
