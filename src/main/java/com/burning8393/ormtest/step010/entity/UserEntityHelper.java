package com.burning8393.ormtest.step010.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用户实体助手类
 */
public class UserEntityHelper {

    /**
     * 将数据集转换为实体对象
     *
     * @param rs 数据集
     * @return
     * @throws SQLException
     */
    public UserEntity create(ResultSet rs) throws SQLException {
        if (null == rs) {
            return null;
        }
        // 创建新的实体对象
        UserEntity ue = new UserEntity();

        ue.userId = rs.getInt("user_id");
        ue.userName = rs.getString("user_name");
        ue.password = rs.getString("password");
        //
        // 都是硬编码会不会很累?
        // 而且要是 UserEntity 和 t_user 加字段,
        // 还得改代码...
        // 为何不尝试一下反射?
        // 跳到 step020 看看吧!
        //
        return ue;
    }
}
