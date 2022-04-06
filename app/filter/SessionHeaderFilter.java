package filter;

import akka.stream.Materializer;
import com.google.inject.Inject;
import play.mvc.*;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class SessionHeaderFilter extends Filter {

    @Inject
    public SessionHeaderFilter(Materializer mat) {
        super(mat);
    }

    @Override
    public CompletionStage<Result> apply(Function<Http.RequestHeader, CompletionStage<Result>> next, Http.RequestHeader rh) {
        return next.apply(rh).thenApply(result -> {
            result.cookies();
            return result;
        });
    }
}
