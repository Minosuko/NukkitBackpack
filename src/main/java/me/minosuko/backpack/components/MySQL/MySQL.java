package me.minosuko.backpack.MySQLDB;

import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.MainLogger;
import me.minosuko.backpack.Backpack;

import java.util.HashMap;
import java.util.Map;
import java.sql.*;

public class MySQL {
	private static Connection connection;
	private static final Config cfg = Backpack.cfg;
	private static final String host = cfg.getSection("mysql").getString("host");
	private static final String port = cfg.getSection("mysql").getString("port");
	private static final String database = cfg.getSection("mysql").getString("database");
	private static final String username = cfg.getSection("mysql").getString("username");
	private static final String password = cfg.getSection("mysql").getString("password");
	public static void connect() {
		if (isConnected()) {
			return;
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			return;
		}
		try {
			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
		} catch (SQLException e) {
			return;
		}
	}

	public static void disconnect() {
		if (!isConnected()) {
			return;
		}
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean isConnected() {
		if (connection == null) {
			return false;
		}
		return true;
	}

	public static void update(String qry) {
		if (!isConnected()) {
			return;
		}
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(qry);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ResultSet getResult(String qry) {
		if (!isConnected()) {
			return null;
		}
		try {
			Statement statement = connection.createStatement();
			return statement.executeQuery(qry);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}