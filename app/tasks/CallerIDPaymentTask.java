package tasks;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class CallerIDPaymentTask {

    private ActorRef callerIDPaymentActor;
    private ActorSystem actorSystem;
    private ExecutionContext executionContext;

    @Inject
    public CallerIDPaymentTask(@Named("caller-id-payment-actor") ActorRef callerIDPaymentActor,
                               ActorSystem actorSystem,
                               ExecutionContext executionContext) {
        this.callerIDPaymentActor = callerIDPaymentActor;
        this.actorSystem = actorSystem;
        this.executionContext = executionContext;

        this.initialize();
    }

    private void initialize() {
        actorSystem.scheduler().schedule(
            Duration.create(0, TimeUnit.HOURS),
            Duration.create(1, TimeUnit.HOURS),
            callerIDPaymentActor,
            "Wake up, Neo...",
            executionContext,
            ActorRef.noSender());
    }

}
