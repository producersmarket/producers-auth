
package com.producersmarket.database;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.ispaces.database.connection.ConnectionManager;
//import com.producersmarket.model.User;

public class LoginDatabaseManager {

    private static final Logger logger = LogManager.getLogger();
    private static final String className = LoginDatabaseManager.class.getSimpleName();

    public static void updateUserLoggedIn(int userId, String sessionId) throws SQLException, Exception {
        logger.debug("updateUserLoggedIn("+userId+", '"+sessionId+"')");

        ConnectionManager connectionManager = new ConnectionManager(className);
        logger.debug("connectionManager = "+connectionManager);

        try {

            String sql = "UPDATE user SET session_id = ? WHERE id = ?";
            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            preparedStatement.setString(1, sessionId);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        } finally {
            try {
                connectionManager.commit();
            } catch(Exception exception) {
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                exception.printStackTrace(printWriter);
                logger.error(stringWriter.toString());
            }
        }
    }

    public static int selectUserIdByEmail(String email) throws SQLException, Exception {
        logger.debug("selectUserIdByEmail("+email+")");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            String sql = new StringBuilder()
                .append("SELECT id")
                .append(" FROM user")
                .append(" WHERE email = ?")
                .toString();
                
            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                return resultSet.getInt(1);
            }

        } finally {
            connectionManager.commit();
        }

        return -1;
    }

    public static String selectPasswordHashByEmail(String email) throws SQLException, Exception {
        logger.debug("selectPasswordHashByEmail('"+email+"')");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            String sql = "SELECT password_hash FROM user WHERE email = ?";
            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getString(1);
            }

        } finally {
            connectionManager.commit();
        }

        return null;
    }

/*
        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        } finally {
            try {
                connectionManager.commit();
            } catch(Exception e) {
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                e.printStackTrace(printWriter);
                logger.error(stringWriter.toString());
            }
        }
*/

}
