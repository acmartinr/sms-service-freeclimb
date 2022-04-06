package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.Logger;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

public class LoggingAction extends Action.Simple {

    @Override
    public CompletionStage<Result> call(Http.Request req) {
        JsonNode jsonBody = req.body().asJson();
        if (jsonBody != null) {
            Logger.info("API: {} {} => {}",
                    req.uri(),
                    req.method(),
                    jsonBody.toString());
        }

        return delegate.call(req);
    }
}
