package game.tools.db.mysql;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDAO
{
	private final static String sql = "SELECT * FROM sys_role u WHERE u.id=10010000015012";

    public void query(Connection conn) {
        try {
            Statement st = conn.createStatement();
            ResultSet result = st.executeQuery(sql);
            result.close();
            st.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
