package tasks;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class KeepAliveApiAccessTokenTask {

    private ActorRef keepAliveApiAccessTokenActor;
    private ActorSystem actorSystem;
    private ExecutionContext executionContext;

    @Inject
    public KeepAliveApiAccessTokenTask(@Named("keep-alive-access-token-actor") ActorRef keepAliveApiAccessTokenActor,
                                       ActorSystem actorSystem,
                                       ExecutionContext executionContext) {
        this.keepAliveApiAccessTokenActor = keepAliveApiAccessTokenActor;
        this.actorSystem = actorSystem;
        this.executionContext = executionContext;

        this.initialize();
    }

    private void initialize() {
        actorSystem.scheduler().schedule(
                Duration.create(0, TimeUnit.MINUTES),
                Duration.create(29, TimeUnit.MINUTES),
                keepAliveApiAccessTokenActor,
                "Wake up, Neo...",
                executionContext,
                ActorRef.noSender());
    }

}
