package controllers;

import com.google.inject.Inject;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

public class MakeMyDataController extends Controller {

    private final WSClient ws;

    @Inject
    public MakeMyDataController(WSClient ws) {
        this.ws = ws;
    }

    public CompletionStage<Result> getRecordsCount(Http.Request request) {
        WSRequest mmdRequest = ws.url("https://www.makemydata.com/rest/textalldata/data/geographic");
        CompletionStage<WSResponse> eventualResponse = mmdRequest.post(request.body().asJson());
        return eventualResponse.thenApplyAsync(wsResponse -> ok(wsResponse.asJson()));
    }

    public CompletionStage<Result> purchaseRecords(Http.Request request) {
        WSRequest mmdRequest = ws.url("https://makemydata.com/rest/textalldata/lists/save/buy");
        CompletionStage<WSResponse> eventualResponse = mmdRequest.post(request.body().asJson());
        return eventualResponse.thenApplyAsync(wsResponse -> ok(wsResponse.asJson()));
    }

    public CompletionStage<Result> campaignsList(Http.Request request) {
        WSRequest mmdRequest = ws.url("https://makemydata.com/rest/textalldata/lists/purchased");
        CompletionStage<WSResponse> eventualResponse = mmdRequest.post(request.body().asJson());
        return eventualResponse.thenApplyAsync(wsResponse -> ok(wsResponse.asJson()));
    }

}