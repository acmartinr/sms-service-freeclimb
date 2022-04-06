package tasks;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class CallerIDRemoveTask {

    private ActorRef callerIDRemoveActor;
    private ActorSystem actorSystem;
    private ExecutionContext executionContext;

    @Inject
    public CallerIDRemoveTask(@Named("caller-id-remove-actor") ActorRef callerIDRemoveActor,
                              ActorSystem actorSystem,
                              ExecutionContext executionContext) {
        this.callerIDRemoveActor = callerIDRemoveActor;
        this.actorSystem = actorSystem;
        this.executionContext = executionContext;

        this.initialize();
    }

    private void initialize() {
        actorSystem.scheduler().schedule(
            Duration.create(1, TimeUnit.MINUTES),
            Duration.create(1, TimeUnit.MINUTES),
                callerIDRemoveActor,
            "Wake up, Neo...",
            executionContext,
            ActorRef.noSender());
    }

}
