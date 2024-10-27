package sqlGuide;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Exception;

public class sqlGuide {

	private final static String TABLE = "world";
	private final static String COLUMNS = "id,name,municipality,iso_region,iso_country,continent,latitude,longitude,altitude";

	static class Place extends HashMap<String,String> {}
	static class Places extends ArrayList<Place> {}

	public static void main(String[] args) {
		try {
			String match = "dave";
			Integer limit = 100;
			Integer found = Database.found(match);
			Places places = Database.places(match, limit);
			System.out.println("");
			Report.printPlaces(places);
			System.out.printf("%d found, %d returned\n", found, places.size());
		} catch (Exception e) {
			System.err.println("Exception: " + e.getMessage());
		}
	}

	static class Database {
		static Integer found(String match) throws Exception {
			String sql = Select.found(match);
			try (
				// connect to the database and query
				Connection conn = DriverManager.getConnection(Credential.url(), Credential.USER, Credential.PASSWORD);
				Statement query = conn.createStatement();
				ResultSet results = query.executeQuery(sql)
			) {
				return count(results);
			} catch (Exception e) {
				throw e;
			}
		}

		private static Integer count(ResultSet results) throws Exception {
			if (results.next()) {
				return results.getInt("count");
			}
			throw new Exception("No count results in found query.");
		}


		static Places places(String match, Integer limit) throws Exception {
			String sql      = Select.match(match, limit);
			String url      = Credential.url();
			String user     = Credential.USER;
			String password = Credential.PASSWORD;
			try (
				// connect to the database and query
				Connection conn    = DriverManager.getConnection(url, user, password);
				Statement  query   = conn.createStatement();
				ResultSet  results = query.executeQuery(sql)
			) {
				return convertQueryResultsToPlaces(results, COLUMNS);
			} catch (Exception e) {
				throw e;
			}
		}


		private static Places convertQueryResultsToPlaces(ResultSet results, String columns) throws Exception {
			int count = 0;
			String[] cols = columns.split(",");
			Places places = new Places();
			while (results.next()) {
				Place place = new Place();
				for (String col: cols) {
					place.put(col, results.getString(col));
				}
				place.put("index", String.format("%d",++count));
				places.add(place);
			}
			return places;
		}


	}
	static class Credential {
		// shared user with read-only access
		final static String USER = "cs314-db";
		final static String PASSWORD = "eiK5liet1uej";
		// connection information when using port forwarding from localhost
		final static String URL = "jdbc:mariadb://faure.cs.colostate.edu/cs314";
		static String url() {
			return URL;
		}
	}

	static class Select {
		static String match(String match, int limit) {
			return statement(match, "DISTINCT " + COLUMNS, "LIMIT " + limit);
		}

		static String found(String match) {
			return statement(match, "COUNT(*) AS count ", "");
		}

		static String statement(String match, String data, String limit) {
			return "SELECT "
				+ data
				+ " FROM " + TABLE
				+ " WHERE name LIKE \"%" + match + "%\" "
				+ limit
				+ " ;";
		}
	}


	static class Report {
		static void printPlaces(Places places) {
			for (Place place : places) {
				System.out.println(place);
			}
		}
	}

}