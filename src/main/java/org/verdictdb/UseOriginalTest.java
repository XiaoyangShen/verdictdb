package org.verdictdb;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.junit.BeforeClass;
import org.junit.Test;
import org.verdictdb.commons.DatabaseConnectionHelpers;
import org.verdictdb.commons.VerdictOption;
import org.verdictdb.connection.DbmsConnection;
import org.verdictdb.connection.JdbcConnection;
import org.verdictdb.core.sqlobject.AbstractRelation;
import org.verdictdb.core.sqlobject.SelectQuery;
import org.verdictdb.exception.VerdictDBException;
import org.verdictdb.sqlreader.NonValidatingSQLParser;
import org.verdictdb.sqlreader.RelationStandardizer;
import org.verdictdb.sqlsyntax.MysqlSyntax;
import org.verdictdb.sqlwriter.SelectQueryToSql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class UseOriginalTest {

  static Connection conn;

  static DbmsConnection dbmsConnection;

  static Statement stmt;

  private static final String MYSQL_HOST;

  static {
    String env = System.getenv("BUILD_ENV");
    if (env != null && env.equals("GitLab")) {
      MYSQL_HOST = "mysql";
    } else {
      MYSQL_HOST = "localhost";
    }
  }

  private static final String MYSQL_DATABASE = "tpch1g";

  private static final String MYSQL_UESR = "root";

  private static final String MYSQL_PASSWORD = "";

  public static void main() throws VerdictDBException, IOException, SQLException {
    String mysqlConnectionString =
            String.format("jdbc:mysql://%s?autoReconnect=true&useSSL=false", MYSQL_HOST);
    conn = DatabaseConnectionHelpers.setupMySql(
            mysqlConnectionString, MYSQL_UESR, MYSQL_PASSWORD, MYSQL_DATABASE);
    dbmsConnection = JdbcConnection.create(conn);
    dbmsConnection.setDefaultSchema(MYSQL_DATABASE);
    stmt = conn.createStatement();
    VerdictContext verdictContext = new VerdictContext(dbmsConnection);
//    verdictContext.sql("DROP TABLE IF EXISTS lineitem_scramble");
//    verdictContext.sql("CREATE SCRAMBLE lineitem_scramble from lineitem");
    VerdictSingleResult result = verdictContext.sql("SELECT avg(l_extendedprice) FROM tpch1g.lineitem_scramble USE_ORIGINAL_AFTER 0.1");
    while (result.next()) {
    }
  }

}
