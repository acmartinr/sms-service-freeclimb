package tasks;

import com.google.inject.AbstractModule;
import play.libs.akka.AkkaGuiceSupport;

public class TasksModule extends AbstractModule implements AkkaGuiceSupport {

    @Override
    protected void configure() {
        bind(ChatHistoryTask.class).asEagerSingleton();
        bindActor(ChatHistoryActor.class, "chat-history-actor");

        bind(CallerIDPaymentTask.class).asEagerSingleton();
        bindActor(CallerIDPaymentActor.class, "caller-id-payment-actor");

        bind(SentCountDayLimitTask.class).asEagerSingleton();
        bindActor(SentCountDayLimitActor.class, "sent-count-day-limit-actor");

        bind(CallerIDRemoveTask.class).asEagerSingleton();
        bindActor(CallerIDRemoveActor.class, "caller-id-remove-actor");

//        bind(KeepAliveApiAccessTokenTask.class).asEagerSingleton();
//        bindActor(KeepAliveApiAccessTokenActor.class, "keep-alive-access-token-actor");

        bind(StartUpTask.class).asEagerSingleton();
    }
}
