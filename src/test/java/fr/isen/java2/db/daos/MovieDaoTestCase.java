package fr.isen.java2.db.daos;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;


public class MovieDaoTestCase {
	@BeforeEach
	public void initDb() throws Exception {
		Connection connection = DataSourceFactory.getConnection();
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , name VARCHAR(50) NOT NULL);");
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS movie (\r\n"
				+ "  idmovie INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + "  title VARCHAR(100) NOT NULL,\r\n"
				+ "  release_date DATETIME NULL,\r\n" + "  genre_id INT NOT NULL,\r\n" + "  duration INT NULL,\r\n"
				+ "  director VARCHAR(100) NOT NULL,\r\n" + "  summary MEDIUMTEXT NULL,\r\n"
				+ "  CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre));");
		stmt.executeUpdate("DELETE FROM movie");
		stmt.executeUpdate("DELETE FROM genre");
		stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='movie'");
		stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='genre'");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (1, 'Title 1', '2015-11-26 12:00:00.000', 1, 120, 'director 1', 'summary of the first movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (2, 'My Title 2', '2015-11-14 12:00:00.000', 2, 114, 'director 2', 'summary of the second movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (3, 'Third title', '2015-12-12 12:00:00.000', 2, 176, 'director 3', 'summary of the third movie')");
		stmt.close();
		connection.close();
	}
	
	/**
	 * Teste la méthode listMovies() qui récupère tous les films.
	 * Vérifie que la méthode retourne bien les 3 films de la base de données.
	 */
	 @Test
	 public void shouldListMovies() {
		MovieDao movieDao = new MovieDao();
		List<Movie> allMovies = movieDao.listMovies();
		assertThat(allMovies).hasSize(3);
	 }
	
	 /**
	 * Teste la méthode listMoviesByGenre() avec le genre "Drama".
	 * Vérifie que la méthode filtre correctement les films par genre et retourne bien les films du genre "Drama". 
	 */
	 @Test
	 public void shouldListMoviesByGenre() {
		MovieDao movieDao = new MovieDao();
		List<Movie> movies = movieDao.listMoviesByGenre("Drama"); 

		assertThat(movies).hasSize(1);
		assertThat(movies.get(0).getTitle()).isEqualTo("Title 1");
	 }
	
	 /**
	 * Teste la méthode addMovies() qui ajoute un nouveau film.
	 * Elle ajoute un film dans la base et vérifie le nombre total de film dans la base de données. 
	 */
	 @Test
	 public void shouldAddMovie() {
		MovieDao movieDao = new MovieDao();
		Genre genre = new Genre(1, "Drama");
		Movie movie = new Movie("Joker", LocalDate.of(2019, 10, 9), genre, 122, "Todd Phillips", "Dans les années 1980, à Gotham City, Arthur Fleck, un comédien de stand-up raté est agressé alors qu'il erre dans les rues grimé en clown.");
		Movie result = movieDao.addMovie(movie);
		assertThat(result).isNotNull();
		assertThat(result.getId()).isNotNull();
		assertThat(result.getTitle()).isEqualTo("Joker");
		List<Movie> allMovies = movieDao.listMovies();
		assertThat(allMovies).hasSize(4);
	 }
}