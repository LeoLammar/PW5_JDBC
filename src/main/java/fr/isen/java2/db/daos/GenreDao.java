package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.util.Optional;


//import org.sqlite.SQLiteDataSource;

import fr.isen.java2.db.entities.Genre;


public class GenreDao {

	/**
	 * Cette méthode execute une requête SQL pour obtenir les données de la table genre.
	 * @return La liste contenant tous les genres. 
	 */
	public List<Genre> listGenres() {
		List<Genre> listGenres = new ArrayList<>();
		try(Connection connection = DataSourceFactory.getConnection()){
			try (Statement statement = connection.createStatement()) {
				try (ResultSet results = statement.executeQuery("SELECT * FROM genre")) {
					while (results.next()) {
						Genre genre = new Genre(results.getInt("idgenre"), results.getString("name")); 
						listGenres.add(genre);
					}
				}
			}
		} catch(SQLException e){
			throw new RuntimeException("Ereur d'accès à la base de données",e);
		}
		return listGenres;
	}

	/**
	 * Cette méthode utilise une requête SQL pour avoir un genre avec son nom.
	 * @param name Le nom du genre a rechercher.
	 * @return Un Optional contenant un genre s'il est trouvé ou sinon il est vide.
	 */
	public Optional<Genre> getGenre(String name) {
		try(Connection connection = DataSourceFactory.getConnection()){
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM genre WHERE name=?")) {
				statement.setString(1, name);
				try (ResultSet results = statement.executeQuery()) {
					while (results.next()) {
						Genre genre = new Genre(results.getInt("idgenre"), results.getString("name"));
						// Retourne un Optional qui contient le genre.
						return Optional.of(genre);
					}
				}
			}
		} catch(SQLException e){
			throw new RuntimeException("Ereur d'accès à la base de données",e);
		}
		// Retourne un Optional vide si le genre n'existe pas.
		return Optional.empty();
	}

	/**
	 * Cette méthode permet d'ajouter un nouveau genre dans la base de données avec un ID unique.
	 * @param name Le nouveau du nouveau genre inséré dans la base de données. 
	 */
	public void addGenre(String name) {
		try (Connection connection = DataSourceFactory.getConnection()) {
        	String sqlQuery = "INSERT INTO genre(name) VALUES(?)";
			try (PreparedStatement statement = connection.prepareStatement(
							sqlQuery)) {
				statement.setString(1, name);
				statement.executeUpdate();
			}
		}catch (SQLException e) {
			throw new RuntimeException("Ereur d'accès à la base de données", e); 
		}
	}
}