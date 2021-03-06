package com.producersmarket.auth.database;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.ispaces.dbcp.ConnectionManager;
import com.ispaces.dbcp.ConnectionPool;
import com.producersmarket.model.User;

public class UserDatabaseManager {

    private static final Logger logger = LogManager.getLogger();

    public static void populateUser(User user, ResultSet resultSet) throws SQLException, Exception {
        logger.debug("populateUser(user, resultSet)");
        user.setId                 (resultSet.getInt   (1));
        user.setName               (resultSet.getString(2));
        user.setHyphenatedName     (resultSet.getString(3));
        user.setEmail              (resultSet.getString(4));
        user.setEmailVerified      (resultSet.getBoolean(5));
        user.setActivationCode     (resultSet.getString(6));
    }

    public static void populateUser(User user, ResultSet resultSet, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("populateUser(user, resultSet, connectionManager)");
        populateUser(user, resultSet);
        selectGroupIdsByUserId(user, connectionManager);
    }

    public static void selectGroupIdsByUserId(User user, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectGroupIdsByUserId("+user+", connectionManager)");

        PreparedStatement preparedStatement = connectionManager.loadStatement("selectGroupIdsByUserId");
        preparedStatement.setInt(1, user.getId());
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()) {
            List<Integer> groupIdList = new ArrayList<Integer>();
            do {
                groupIdList.add(resultSet.getInt(1));
            } while(resultSet.next());
            logger.debug("groupIdList.size() = "+groupIdList.size());
            user.setGroupIdList(groupIdList);
        }
    }

    public static User selectUserByEmail(String email, Object connectionPoolObject) throws SQLException, Exception {
        logger.debug("selectUserByEmail("+email+", "+connectionPoolObject+")");
        return selectUserByEmail(email, (ConnectionPool) connectionPoolObject);
    }

    public static User selectUserByEmail(String email, ConnectionPool connectionPool) throws SQLException, Exception {
        logger.debug("selectUserByEmail("+email+", "+connectionPool+")");
        return selectUserByEmail(email, new ConnectionManager(connectionPool));
    }

    public static User selectUserByEmail(String email, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectUserByEmail("+email+", "+connectionManager+")");
        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement("selectUserByEmail");
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                User user = new User();
                populateUser(user, resultSet, connectionManager);
                return user;
            }
        } finally {
            connectionManager.commit();
        }
        return null;
    }

    public static User selectUserByActivationCode(String email, Object connectionPoolObject) throws SQLException, Exception {
        logger.debug("selectUserByActivationCode("+email+", "+connectionPoolObject+")");
        return selectUserByActivationCode(email, (ConnectionPool) connectionPoolObject);
    }

    public static User selectUserByActivationCode(String email, ConnectionPool connectionPool) throws SQLException, Exception {
        logger.debug("selectUserByActivationCode("+email+", "+connectionPool+")");
        return selectUserByActivationCode(email, new ConnectionManager(connectionPool));
    }

    public static User selectUserByActivationCode(String email, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectUserByActivationCode("+email+", "+connectionManager+")");
        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement("selectUserByActivationCode");
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                User user = new User();
                user.setId            (resultSet.getInt   (1));
                user.setName          (resultSet.getString(2));
                user.setEmail         (resultSet.getString(3));
                selectGroupIdsByUserId(user, connectionManager);
                return user;
            }
        } finally {
            connectionManager.commit();
        }
        return null;
    }

}
