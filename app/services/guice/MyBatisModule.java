package services.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import play.db.Database;
import services.sms.FreeclimbSMSApiService;
import services.sms.ISMSApiService;
import services.sms.TelnexSMSApiService;
import services.sms.TwilioSMSApiService;
//import services.sms.YtelSMSApiService;

import javax.sql.DataSource;

public class MyBatisModule extends org.mybatis.guice.MyBatisModule {

    @Override
    protected void initialize() {
        environmentId("production");

        bindDataSourceProviderType(PlayDataSourceProvider.class);
        bindTransactionFactoryType(JdbcTransactionFactory.class);

        addMapperClasses("services.database.mapper");
        addSimpleAliases("services.database.model");

//        bind(ISMSApiService.class).to(YtelSMSApiService.class);
        //bind(ISMSApiService.class).to(TwilioSMSApiService.class);
        bind(ISMSApiService.class).to(FreeclimbSMSApiService.class);
    }

    @Singleton
    public static class PlayDataSourceProvider implements Provider<DataSource> {
        private final Database db;

        @Inject
        public PlayDataSourceProvider(final Database db) {
            this.db = db;
        }

        @Override
        public DataSource get() {
            DataSource dataSource = db.getDataSource();
            if (dataSource instanceof HikariDataSource) {
                HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
                hikariDataSource.setMaximumPoolSize(80);
            }

            return dataSource;
        }
    }
}
