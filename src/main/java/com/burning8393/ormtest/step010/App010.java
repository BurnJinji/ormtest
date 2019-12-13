package com.burning8393.ormtest.step010;

import com.burning8393.ormtest.step010.entity.UserEntity;
import com.burning8393.ormtest.step010.entity.UserEntityHelper;

import java.sql.*;

public class App010 {
    public static void main(String[] args) throws Exception {
        new App010().start();
    }

    private void start() {
        Connection conn = null;
        Statement stmt = null;
        long start = 0;
        long end = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

            String dbConnStr = "jdbc:mysql://192.168.56.99:3306/ormtest?user=root&password=burning8393";

            conn = DriverManager.getConnection(dbConnStr);

            stmt = conn.createStatement();

            // 创建 SQL 查询
            // ormtest 数据库中有个 t_user 数据表
            // t_user 数据表中包括三个字段 user_id, user_name，password
            // t_user 数据表有 20 万条数据
            String sql = "select * from t_user limit 200000";

            // 执行查询
            ResultSet rs = stmt.executeQuery(sql);

            UserEntityHelper helper = new UserEntityHelper();

            // 获取开始时间
            start = System.currentTimeMillis();

            while (rs.next()) {
                // 创建新的实体对象
                UserEntity userEntity = helper.create(rs);

                //
                // 关于上面这段代码，
                // 我们是否可以将其封装到一个助手类中
                // 这样做的好处是：
                // 当实体类发生修改时，只需要改助手类就可以了
                //
            }

            // 获取结束时间
            end = System.currentTimeMillis();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭数据库连接
            try {
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // 打印实例化花费时间
        System.out.println("实例化花费时间 = " + (end - start) + " ms");
    }
}
