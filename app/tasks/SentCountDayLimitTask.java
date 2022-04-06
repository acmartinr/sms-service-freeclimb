package tasks;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.checkerframework.checker.units.qual.C;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import services.database.dao.CampaignDAO;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class SentCountDayLimitTask {

    private ActorRef sentCountDayLimitActor;
    private ActorSystem actorSystem;
    private ExecutionContext executionContext;

    @Inject
    public SentCountDayLimitTask(@Named("sent-count-day-limit-actor") ActorRef sentCountDayLimitActor,
                                 ActorSystem actorSystem,
                                 ExecutionContext executionContext) {
        this.sentCountDayLimitActor = sentCountDayLimitActor;
        this.actorSystem = actorSystem;
        this.executionContext = executionContext;

        this.initialize();
    }

    private void initialize() {
        actorSystem.scheduler().schedule(
                Duration.create(getStartDelay(), TimeUnit.MILLISECONDS),
                Duration.create(24, TimeUnit.HOURS),
                sentCountDayLimitActor,
                "Wake up, Neo...",
                executionContext,
                ActorRef.noSender());
    }

    private long getStartDelay() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        calendar.add(Calendar.DAY_OF_MONTH, 1);

        long delay = calendar.getTime().getTime() - System.currentTimeMillis();
        long day = 24l * 60 * 60 * 1000;
        while (delay > day) {
            delay = delay - day;
        }

        System.out.println("Initialize reset day sent count: " + System.currentTimeMillis());
        System.out.println("Reset day sent count delay: " + delay);

        return delay;
    }
}
