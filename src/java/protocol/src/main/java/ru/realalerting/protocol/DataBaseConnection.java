package ru.realalerting.protocol;

import io.vertx.core.Vertx;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnection;

import javax.xml.crypto.Data;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class DataBaseConnection {
    public static PgConnectOptions connectOptions;
    private static PoolOptions poolOptions;
    private static SqlClient client;

    public static SqlClient connect(int poolCount) {
        int port = DatabaseConfig.getDbPort();
        String host = DatabaseConfig.getDbHost();
        String dbName = DatabaseConfig.getDbName();
        String user = DatabaseConfig.getDbUsername();
        String password = DatabaseConfig.getDbPassword();

        connectOptions = new PgConnectOptions()
                .setPort(port)
                .setHost(host)
                .setDatabase(dbName)
                .setUser(user)
                .setPassword(password);
        poolOptions = new PoolOptions().setMaxWaitQueueSize(5);
        client = PgBuilder
                .client()
                .with(poolOptions)
                .connectingTo(connectOptions)
                .using(Vertx.vertx())
                .build();

        return client;
    }
}
