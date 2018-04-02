package game.tools.db.mysql;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;
import org.apache.tomcat.jdbc.pool.DataSource;
//import org.logicalcobwebs.proxool.ProxoolDataSource;
//import com.alibaba.druid.pool.DruidDataSource;
//import com.mchange.v2.c3p0.ComboPooledDataSource;

public class TestMain {

    // 数据库驱动名称
    static String driver = "com.mysql.jdbc.Driver";

    // 数据库连接地址
    static String jdbcUrl = "jdbc:mysql://192.168.1.131:3306/jjdtk_logic_s2";

    // 数据库用户名
    static String user = "root";

    // 数据库密码
    static String passwd = "root";

    // 连接池初始化大小
    static int initialSize = 5;

    // 连接池最小空闲
    static int minPoolSize = 10;

    // 连接池最大连接数量
    static int maxPoolSize = 50;

    // 最小逐出时间，100秒
    static int maxIdleTime = 100000;

    // 连接失败重试次数
    static int retryAttempts = 10;

    // 当连接池连接耗尽时获取连接数
    static int acquireIncrement = 5;

//    // c3p0数据源
//    static ComboPooledDataSource c3p0DataSource = getC3p0DataSource();
//
//    // Druid数据源
//    static DruidDataSource druidDataSource = getDruidDataSource();
//
//    // Proxool数据源
//    static ProxoolDataSource proxoolDataSource = getProxoolDataSource();

    // Tomcat Jdbc Pool数据源
    static DataSource tomcatDataSource = getTomcatDataSource();

    /**
     * 测试方式： 每种数据源配置信息尽量相同，以求结果更加准确
     * 每种数据源做10次、100次、500次、1000次、2000次、4000次、8000次查询操作 每种查询重复100次，查看100次执行时间的波动图
     * @param args
     * @throws IOException
     * @throws SQLException
     */
    public static void main(String[] args) throws IOException, SQLException {

        TestDAO testDAO = new TestDAO();
        // 查询次数
        int count = 10;
        System.out.println("查询次数为：" + count);
        System.out.println();
//        System.out.println("==========================c3p0 测试开始==========================");
//        // 测试c3p0
//        for (int i = 0; i < 100; i++) {
//            queryC3p0(testDAO, c3p0DataSource, count);
//        }
//        System.out.println("==========================c3p0 测试结束==========================");
//        System.out.println();
//        System.out.println("==========================Proxool 测试开始==========================");
//        // 测试Proxool
//        for (int i = 0; i < 100; i++) {
//            queryProxxool(testDAO, proxoolDataSource, count);
//        }
//        System.out.println("==========================Proxool 测试结束==========================");
//        System.out.println();
//        System.out.println("==========================Druid 测试开始==========================");
//        // 测试Druid
//        for (int i = 0; i < 100; i++) {
//            queryDruid(testDAO, druidDataSource, count);
//        }
//        System.out.println("==========================Druid 测试结束==========================");
//        System.out.println();
        System.out.println("==========================Tomcat Jdbc Pool 测试开始==========================");
        // 测试Tomcat Jdbc Pool
        for (int i = 0; i < 100; i++) {
            queryTomcatJDBC(testDAO, tomcatDataSource, count);
        }
        System.out.println("==========================Tomcat Jdbc Pool 测试结束==========================");
    }

//    /**
//     * c3p0测试
//     * @param testDAO
//     * @param ds
//     * @param count
//     * @throws SQLException
//     */
//    public static void queryC3p0(TestDAO testDAO, ComboPooledDataSource ds, int count) throws SQLException {
//        // 查询10次以初始化连接池
//        for (int i = 0; i < 10; i++) {
//            testDAO.query(ds.getConnection());
//        }
//        // 开始时间
//        long startMillis = System.currentTimeMillis();
//        // 循环查询
//        for (int i = 0; i < count; i++) {
//            testDAO.query(ds.getConnection());
//        }
//        // 结束时间
//        long endMillis = System.currentTimeMillis();
//        // 输出结束时间
//        System.out.println(endMillis - startMillis);
//    }
//
//    /**
//     * Proxxool测试
//     * @param testDAO
//     * @param ds
//     * @param count
//     * @throws SQLException
//     */
//    public static void queryProxxool(TestDAO testDAO, ProxoolDataSource ds, int count) throws SQLException {
//        // 查询10次以初始化连接池
//        for (int i = 0; i < 10; i++) {
//            testDAO.query(ds.getConnection());
//        }
//        // 开始时间
//        long startMillis = System.currentTimeMillis();
//        // 循环查询
//        for (int i = 0; i < count; i++) {
//            testDAO.query(ds.getConnection());
//        }
//        // 结束时间
//        long endMillis = System.currentTimeMillis();
//        // 输出结束时间
//        System.out.println(endMillis - startMillis);
//    }
//
//    /**
//     * Druid测试
//     * @param testDAO
//     * @param ds
//     * @param count
//     * @throws SQLException
//     */
//    public static void queryDruid(TestDAO testDAO, DruidDataSource ds, int count) throws SQLException {
//        // 查询10次以初始化连接池
//        for (int i = 0; i < 10; i++) {
//            testDAO.query(ds.getConnection());
//        }
//        // 开始时间
//        long startMillis = System.currentTimeMillis();
//        // 循环查询
//        for (int i = 0; i < count; i++) {
//            testDAO.query(ds.getConnection());
//        }
//        // 结束时间
//        long endMillis = System.currentTimeMillis();
//        // 输出结束时间
//        System.out.println(endMillis - startMillis);
//    }
//
    /**
     * Tomcat Jdbc Pool测试
     * @param testDAO
     * @param ds
     * @param count
     * @throws SQLException
     */
    public static void queryTomcatJDBC(TestDAO testDAO, DataSource ds, int count) throws SQLException {
        // 查询10次以初始化连接池
        for (int i = 0; i < 10; i++) {
            testDAO.query(ds.getConnection());
        }
        // 开始时间
        long startMillis = System.currentTimeMillis();
        // 循环查询
        for (int i = 0; i < count; i++) {
            testDAO.query(ds.getConnection());
        }
        // 结束时间
        long endMillis = System.currentTimeMillis();
        // 输出结束时间
        System.out.println(endMillis - startMillis);
    }
//
//    /**
//     * 获取c3p0数据源
//     * @throws PropertyVetoException
//     */
//    public static ComboPooledDataSource getC3p0DataSource() {
//        // 设置参数
//        ComboPooledDataSource cpds = new ComboPooledDataSource();
//        try {
//            cpds.setDriverClass(driver);
//        } catch (PropertyVetoException e) {
//            e.printStackTrace();
//        }
//        cpds.setJdbcUrl(jdbcUrl);
//        cpds.setUser(user);
//        cpds.setPassword(passwd);
//        cpds.setInitialPoolSize(initialSize);
//        cpds.setMinPoolSize(minPoolSize);
//        cpds.setMaxPoolSize(maxPoolSize);
//        cpds.setMaxIdleTime(maxIdleTime);
//        cpds.setAcquireRetryAttempts(retryAttempts);
//        cpds.setAcquireIncrement(acquireIncrement);
//        cpds.setTestConnectionOnCheckin(false);
//        cpds.setTestConnectionOnCheckout(false);
//        return cpds;
//    }
//
//    /**
//     * 获取Druid数据源
//     * @return
//     */
//    public static DruidDataSource getDruidDataSource() {
//        DruidDataSource dds = new DruidDataSource();
//        dds.setUsername(user);
//        dds.setUrl(jdbcUrl);
//        dds.setPassword(passwd);
//        dds.setDriverClassName(driver);
//        dds.setInitialSize(initialSize);
//        dds.setMaxActive(maxPoolSize);
//        dds.setMaxWait(maxIdleTime);
//        dds.setTestWhileIdle(false);
//        dds.setTestOnReturn(false);
//        dds.setTestOnBorrow(false);
//        return dds;
//    }
//
//    /**
//     * 获取Proxool数据源
//     * @return
//     */
//    public static ProxoolDataSource getProxoolDataSource() {
//        ProxoolDataSource pds = new ProxoolDataSource();
//        pds.setAlias("mysql");
//        pds.setUser(user);
//        pds.setPassword(passwd);
//        pds.setDriverUrl(jdbcUrl);
//        pds.setDriver(driver);
//        pds.setMaximumActiveTime(maxIdleTime);
//        pds.setMaximumConnectionCount(maxPoolSize);
//        pds.setMinimumConnectionCount(initialSize);
//        pds.setPrototypeCount(minPoolSize);
//        pds.setTestBeforeUse(false);
//        pds.setTestAfterUse(false);
//        return pds;
//    }

    /**
     * 获取Apache tomcat jdbc pool数据源
     * @return
     */
    public static DataSource getTomcatDataSource() {
        DataSource ds = new DataSource();
        ds.setUrl(jdbcUrl);
        ds.setUsername(user);
        ds.setPassword(passwd);
        ds.setDriverClassName(driver);
        ds.setInitialSize(initialSize);
        ds.setMaxIdle(minPoolSize);
        ds.setMaxActive(maxPoolSize);
        ds.setTestWhileIdle(false);
        ds.setTestOnBorrow(false);
        ds.setTestOnConnect(false);
        ds.setTestOnReturn(false);
        return ds;
    }
}