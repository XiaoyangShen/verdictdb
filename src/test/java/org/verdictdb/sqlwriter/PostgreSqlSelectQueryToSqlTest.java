package org.verdictdb.sqlwriter;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.verdictdb.core.sqlobject.AbstractRelation;
import org.verdictdb.exception.VerdictDBException;
import org.verdictdb.sqlreader.NonValidatingSQLParser;
import org.verdictdb.sqlsyntax.PostgresqlSyntax;

public class PostgreSqlSelectQueryToSqlTest {

  static Connection conn;

  private static Statement stmt;

  private static final String POSTGRES_HOST;

  static {
    String env = System.getenv("BUILD_ENV");
    if (env != null && env.equals("GitLab")) {
      POSTGRES_HOST = "postgres";
    } else {
      POSTGRES_HOST = "localhost";
    }
  }

  private static final String POSTGRES_DATABASE = "test";

  private static final String POSTGRES_USER = "postgres";

  private static final String POSTGRES_PASSWORD = "";

  @BeforeClass
  public static void setupMySqlDatabase() throws SQLException {
    String postgresConnectionString =
        String.format("jdbc:postgresql://%s/%s", POSTGRES_HOST, POSTGRES_DATABASE);
    conn = DriverManager.getConnection(postgresConnectionString, POSTGRES_USER, POSTGRES_PASSWORD);

    stmt = conn.createStatement();
    List<List<Object>> contents = new ArrayList<>();
    contents.add(Arrays.<Object>asList(1, "Anju", "female", 15, 170.2, "USA", "2017-10-12 21:22:23"));
    contents.add(Arrays.<Object>asList(2, "Sonia", "female", 17, 156.5, "USA", "2017-10-12 21:22:23"));
    contents.add(Arrays.<Object>asList(3, "Asha", "male", 23, 168.1, "CHN", "2017-10-12 21:22:23"));
    contents.add(Arrays.<Object>asList(3, "Joe", "male", 14, 178.6, "USA", "2017-10-12 21:22:23"));
    contents.add(Arrays.<Object>asList(3, "JoJo", "male", 18, 190.7, "CHN", "2017-10-12 21:22:23"));
    contents.add(Arrays.<Object>asList(3, "Sam", "male", 18, 190.0, "USA", "2017-10-12 21:22:23"));
    contents.add(Arrays.<Object>asList(3, "Alice", "female", 18, 190.21, "CHN", "2017-10-12 21:22:23"));
    contents.add(Arrays.<Object>asList(3, "Bob", "male", 18, 190.3, "CHN", "2017-10-12 21:22:23"));
    stmt = conn.createStatement();
    stmt.execute("DROP TABLE IF EXISTS people");
    stmt.execute("CREATE TABLE people(id smallint, name varchar(255), gender varchar(8), age float, height float, nation varchar(8), birth timestamp)");
    for (List<Object> row : contents) {
      String id = row.get(0).toString();
      String name = row.get(1).toString();
      String gender = row.get(2).toString();
      String age = row.get(3).toString();
      String height = row.get(4).toString();
      String nation = row.get(5).toString();
      String birth = row.get(6).toString();
      stmt.execute(String.format("INSERT INTO people(id, name, gender, age, height, nation, birth) VALUES(%s, '%s', '%s', %s, %s, '%s', '%s')", id, name, gender, age, height, nation, birth));
    }
  }

  @AfterClass
  public static void tearDown() throws SQLException {
    stmt.execute("DROP TABLE IF EXISTS people");
  }

  @Test
  public void testConcat() throws VerdictDBException {
    String expected = "select 'Post' || 'greSQL' from \"myschema\".\"mytable\" as t";
    NonValidatingSQLParser sqlToRelation = new NonValidatingSQLParser();
    AbstractRelation sel = sqlToRelation.toRelation(expected);
    SelectQueryToSql relToSql = new SelectQueryToSql(new PostgresqlSyntax());
    String actual = relToSql.toSql(sel);
    assertEquals(expected, actual);
  }

  @Test
  public void testOverlay() throws VerdictDBException {
    String expected = "select overlay('Txxxxas' placing 'hom' from 2) from \"myschema\".\"mytable\" as t";
    NonValidatingSQLParser sqlToRelation = new NonValidatingSQLParser();
    AbstractRelation sel = sqlToRelation.toRelation(expected);
    SelectQueryToSql relToSql = new SelectQueryToSql(new PostgresqlSyntax());
    String actual = relToSql.toSql(sel);
    assertEquals(expected, actual);
  }

  @Test
  public void testSubstring() throws VerdictDBException {
    String expected = "select substring('Thomas' from '%#o_a#_' for '#') from \"myschema\".\"mytable\" as t";
    NonValidatingSQLParser sqlToRelation = new NonValidatingSQLParser();
    AbstractRelation sel = sqlToRelation.toRelation(expected);
    SelectQueryToSql relToSql = new SelectQueryToSql(new PostgresqlSyntax());
    String actual = relToSql.toSql(sel);
    assertEquals(expected, actual);
  }

  @Test
  public void testExtract() throws VerdictDBException {
    String expected = "select extract(hour from '2001-02-16 20:38:40') from \"myschema\".\"mytable\" as t";
    NonValidatingSQLParser sqlToRelation = new NonValidatingSQLParser();
    AbstractRelation sel = sqlToRelation.toRelation(expected);
    SelectQueryToSql relToSql = new SelectQueryToSql(new PostgresqlSyntax());
    String actual = relToSql.toSql(sel);
    assertEquals(expected, actual);
  }
}
