package controllers;

import com.google.inject.Inject;
import model.*;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import services.database.dao.CampaignDAO;
import services.database.model.Sender;
import services.database.model.SenderGroup;

//import javax.xml.ws.Response;

public class SendersController extends Controller {

    private CampaignDAO campaignDAO;

    @Inject
    public SendersController(CampaignDAO campaignDAO) {
        this.campaignDAO = campaignDAO;
    }

    @With(LoggingAction.class)
    public Result senders(Http.Request request) {
        CommonRequest commonRequest = Json.fromJson(request.body().asJson(), CommonRequest.class);

        return ok(Json.toJson(CommonResponse.OK(new SendersResponse(
                campaignDAO.getSendersByRequest(commonRequest),
                campaignDAO.getSendersCountByRequest(commonRequest)
        ))));
    }

    @With(LoggingAction.class)
    public Result addSender(Http.Request request) {
        Sender sender = Json.fromJson(request.body().asJson(), Sender.class);

        Sender dbSender = campaignDAO.getSenderByName(sender);
        if (dbSender != null && dbSender.getId() != sender.getId()) {
            return ok(Json.toJson(CommonResponse.ERROR("name")));
        }

        dbSender = campaignDAO.getSenderByPhone(sender);
        if (dbSender != null && dbSender.getId() != sender.getId()) {
            return ok(Json.toJson(CommonResponse.ERROR("phone")));
        }

        if (sender.getId() == 0) {
            sender.setDate(System.currentTimeMillis());
            sender.setSentCount(0);

            campaignDAO.insertSender(sender);
        } else {
            campaignDAO.updateSender(sender);
        }

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result removeSender(Long senderId) {
        campaignDAO.removeSenderById(senderId);
        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result senderGroups(Http.Request request) {
        CommonRequest commonRequest = Json.fromJson(request.body().asJson(), CommonRequest.class);

        return ok(Json.toJson(CommonResponse.OK(new SenderGroupsResponse(
                campaignDAO.getSenderGroupsByRequest(commonRequest),
                campaignDAO.getSenderGroupsCountByRequest(commonRequest)
        ))));
    }

    @With(LoggingAction.class)
    public Result addSenderGroup(Http.Request request) {
        SenderGroup senderGroup = Json.fromJson(request.body().asJson(), SenderGroup.class);

        SenderGroup dbSenderGroup = campaignDAO.getSenderGroupByName(senderGroup);
        if (dbSenderGroup != null && dbSenderGroup.getId() != senderGroup.getId()) {
            return ok(Json.toJson(CommonResponse.ERROR("name")));
        }

        if (senderGroup.getId() == 0) {
            senderGroup.setDate(System.currentTimeMillis());

            campaignDAO.insertSenderGroup(senderGroup);
        } else {
            campaignDAO.updateSenderGroup(senderGroup);
        }

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result removeSenderGroup(Long senderGroupId) {
        campaignDAO.removeSenderGroupById(senderGroupId);
        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result getSendersForGroup(Long senderGroupId) {
        return ok(Json.toJson(CommonResponse.OK(campaignDAO.getSendersForGroup(senderGroupId))));
    }

}
