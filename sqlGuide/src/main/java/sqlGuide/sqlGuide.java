package sqlGuide;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class sqlGuide {

	private final static String TABLE = "world";
	private final static String COLUMN = "name";
	private final static String MATCH = "dave";
	private final static Integer limit = 100;
	private final static String COLUMNS = "id,name,municipality,iso_region,iso_country,latitude,longitude,altitude,type";

	static class Place extends HashMap<String, String> {

	}

	static class Places extends ArrayList<Place> {

	}

	public static void main(String[] args) {
		try {
			//near request
			Places near = Database.places(Select.near(limit));
			System.out.println("");
			Report.printPlaces(near);
			System.out.printf("\nnear, limit: %s, returned: %d\n", limit,
					near.size());

			// find request
			Integer found = Database.found(Select.found(MATCH));
			Places match = Database.places(Select.match(MATCH, limit));
			System.out.println("");
			Report.printPlaces(match);
			System.out.printf("\nmatch: %s, limit: %s, found: %d, returned: %d\n", MATCH, limit,
				  found, match.size());
		} catch (Exception e) {
			System.err.println("Exception: " + e.getMessage());
		}
	}


	static class Database {

		private static ResultSet performQuery (String sql) throws Exception {
			try (
				  // connect to the database and query
				  Connection conn = DriverManager.getConnection(Credential.URL, Credential.USER, Credential.PASSWORD);
				  Statement query = conn.createStatement();
			) {
				ResultSet results = query.executeQuery(sql);
				return results;
			} catch (Exception e) {
				System.out.println(String.format("ERROR IN performQuery() -> %s \n", e.toString()));
				e.printStackTrace();
				return null;
			}
		}

		static Integer found(String sql) throws Exception {
			ResultSet results = performQuery(sql);
			if (!results.next()) {
				throw new Exception("No count results in found query.");
			}
			return results.getInt("count");
		}


		static Places places(String sql) throws Exception {
			ResultSet results = performQuery(sql);
			String columns = COLUMNS;
			int count = 0;
			String[] cols = columns.split(",");
			Places places = new Places();
			while (results.next()) {
				Place place = new Place();
				for (String col : cols) {
					place.put(col, results.getString(col));
				}
				place.put("index", String.format("%d", ++count));
				places.add(place);
			}
			return places;
		}


	}

	static class Select {

		static String near(int limit) {
			// international date line wrapping at 180,-180
			String where =  " WHERE latitude BETWEEN 39.5 AND 40.5 AND longitude BETWEEN -105.5 AND -104.5 ";
			return statement(where, COLUMNS + " ", "LIMIT " + limit);
		}

		static String found(String match) {
			String where = " WHERE " + COLUMN + " LIKE \"%" + match + "%\" ";
			return statement(where, "COUNT(*) AS count ", "");
		}

		static String match(String match, int limit) {
			String where = " WHERE " + COLUMN + " LIKE \"%" + match + "%\" ";
			return statement(where, COLUMNS + " ", "LIMIT " + limit);
		}

		static String statement(String where, String data, String limit) {
			return "SELECT "
				  + data
				  + " FROM " + TABLE
				  + where
				  + limit
				  + " ;";
		}
	}

	static class Credential {

		 // shared user with read-only access
	final static String USER = "cs314-db";
	final static String PASSWORD = "eiK5liet1uej";
	// connection information when using port forwarding from localhost
	final static String URL = "jdbc:mariadb://faure.cs.colostate.edu/cs314";
	}

	static class Report {

		static void printPlaces(Places places) {
			for (Place place : places) {
				System.out.println(place);
			}
		}
	}

}