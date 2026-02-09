package fr.isen.java2.db.daos;

//import javax.sql.DataSource;

// Package pour le bonus 1 
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

//import org.sqlite.SQLiteDataSource;

public class DataSourceFactory {

	private DataSourceFactory() {
		throw new IllegalStateException("This is a static class that should not be instantiated");
	}

	/**
	 * @return Connexion à la base de données SQLite
	 * 
	 */
	/* public static DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = new SQLiteDataSource();
			dataSource.setUrl("jdbc:sqlite:sqlite.db");
		}
		return dataSource;
	} */

	/** URL de connexion JDBC à la base de données SQLite. */
	private static final String URL = "jdbc:sqlite:sqlite.db";
	
	/**
	 * Crée une nouvelle connexion en utilisant DriverManager 
	 * @return connexion à la base de donnée
	 */
	public static Connection getConnection(){
		try{ 
			return DriverManager.getConnection(URL);
		} catch (SQLException e){
			throw new RuntimeException("Ereur de connexion", e);
		}
	}
}