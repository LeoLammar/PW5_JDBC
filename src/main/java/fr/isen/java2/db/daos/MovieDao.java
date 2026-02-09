package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

public class MovieDao {

	/**
	 * Cette méthode utilise une jointure SQL pour avoir les données de la table movie.
	 * @return Une liste contenant tous les films de la base de données. 
	 */
	public List<Movie> listMovies() {
		List<Movie> listMovies = new ArrayList<>();
		try(Connection connection = DataSourceFactory.getConnection()){
			try (Statement statement = connection.createStatement()) {
				try (ResultSet results = statement.executeQuery("SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre")){
						while (results.next()) {
						// Création d'un objet Genre
						Genre genre = new Genre(results.getInt("idgenre"), results.getString("name"));
						// Création d'un objet movie 
						Movie movie = new Movie(
							results.getInt("idmovie"), 
							results.getString("title"), 
							results.getDate("release_date").toLocalDate(), 
							genre, 
							results.getInt("duration"), 
							results.getString("director"), 
							results.getString("summary")
						); 
						listMovies.add(movie);
					}
				}
			}
		} catch(SQLException e){
			throw new RuntimeException("Ereur d'accès à la base de données",e);
		}
		return listMovies;
		
	}

	/**
	 * Cette méthode utilise une jointure SQL pour avoir les données de la table movie avec le nom du genre de la table genre.
	 * @param genreName le nom du genre a filtrer.
	 * @return Une liste contenant tous les genres du genre genreName. 
	 */
	public List<Movie> listMoviesByGenre(String genreName) {
		List<Movie> listMovies = new ArrayList<>();
		try(Connection connection = DataSourceFactory.getConnection()){
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre WHERE genre.name = ?")) {
				statement.setString(1, genreName);
				try (ResultSet results = statement.executeQuery()) {
					while (results.next()) {
						Genre genre = new Genre(results.getInt("idgenre"), results.getString("name"));
						Movie movie = new Movie(
							results.getInt("idmovie"), 
							results.getString("title"), 
							results.getDate("release_date").toLocalDate(), 
							genre, 
							results.getInt("duration"), 
							results.getString("director"), 
							results.getString("summary")
						); 
						listMovies.add(movie);
					}
				}
			}
		} catch(SQLException e){
			throw new RuntimeException("Erreur d'accès à la base de données",e);
		}
		return listMovies;
	}

	/**
	 * Cette méthode utilise une requête SQL pour ajouter un film a la table movie de la base de données.
	 * @param Movie a inserer dans la base de données. 
	 * @return Le movie du paramètre avec l'ID généré par la base de données. 
	 */
	public Movie addMovie(Movie movie) {
		String title = movie.getTitle();
		String releaseDate = movie.getReleaseDate().toString() + " 00:00:00.000";
		int genreId = movie.getGenre().getId();
		int duration = movie.getDuration();
		String director = movie.getDirector();  
		String summary = movie.getSummary();
		try (Connection connection = DataSourceFactory.getConnection()) {
        	String sqlQuery = "INSERT INTO movie(title,release_date,genre_id,duration,director,summary) VALUES(?,?,?,?,?,?)";
			try (PreparedStatement statement = connection.prepareStatement(
							sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
				statement.setString(1, title);
				statement.setString(2, releaseDate);
				statement.setInt(3, genreId);
				statement.setInt(4, duration);
				statement.setString(5, director);
				statement.setString(6, summary);
				statement.executeUpdate();
				// Récupération de l'id généré par la base de données.
				try(ResultSet ids = statement.getGeneratedKeys()){
					if(ids.next()){
						int generatedId = ids.getInt(1);
						movie.setId(generatedId);
						return movie;
					}
				}
			}
		}catch (SQLException e) {
			throw new RuntimeException("Erreur d'accès à la base de données", e); 
		}
		return null;
	}
}