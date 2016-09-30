package io.benwilcock;

import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by benwilcock on 04/08/2016.
 */

@Component
public class Status {

    @Value("${INSTANCE_GUID}")
    String id;

    @Autowired
    DataSource dataSource;
    @Autowired
    ConnectionFactory rabbitConnectionFactory;

    @Value("${INSTANCE_INDEX}")
    String instanceIndex;

    public Status() {
    }

    public String getId() {
        return id;
    }

    public String getIndex(){
        return instanceIndex;
    }

    public String getSql() {
        StringBuilder sb = new StringBuilder();

        if (dataSource == null) {
            sb.append("NULL");
        } else {
            try {
                Field urlField = ReflectionUtils.findField(dataSource.getClass(), "url");
                ReflectionUtils.makeAccessible(urlField);
                sb.append(urlField.get(dataSource));
                sb.append(":UP");
            } catch (Exception fe) {
                try {
                    Method urlMethod = ReflectionUtils.findMethod(dataSource.getClass(), "getUrl");
                    ReflectionUtils.makeAccessible(urlMethod);
                    sb.append(urlMethod.invoke(dataSource, (Object[]) null));
                    sb.append(":UP");
                } catch (Exception me) {
                    sb.append(":DOWN - ");
                    sb.append(me.getCause().getMessage());
                }
            }
        }
        return sb.toString();
    }

    public String getRabbit() {
        StringBuilder sb = new StringBuilder();

        if (rabbitConnectionFactory == null) {
            sb.append("NULL");
        } else {
            sb.append(rabbitConnectionFactory.getHost());
            sb.append(":");
            sb.append(rabbitConnectionFactory.getPort());

            try {
                rabbitConnectionFactory.createConnection().isOpen();
                sb.append(":UP");
            } catch (AmqpConnectException ce) {
                sb.append(":DOWN - ");
                sb.append(ce.getCause().getMessage());
            }
        }
        return sb.toString();
    }

    public String getMongo() {
        StringBuilder sb = new StringBuilder();
        sb.append("NOT REQUIRED");
        return sb.toString();
    }

    public boolean isMongo() {
        return false;
    }

    public boolean isSql() {
        return !(null == dataSource);
    }

    public boolean isRabbit() {
        return !(null == rabbitConnectionFactory);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SQL: [");
        sb.append(getSql());
        sb.append("] RABBIT: [");
        sb.append(getRabbit());
        sb.append("] MONGO: [");
        sb.append(getMongo());
        sb.append("]");
        return sb.toString();
    }
}