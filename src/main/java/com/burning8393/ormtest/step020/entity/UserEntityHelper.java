package com.burning8393.ormtest.step020.entity;


import java.lang.reflect.Field;
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
    public UserEntity create(ResultSet rs) throws Exception {
        if (null == rs) {
            return null;
        }
        // 有了反射，
        // 这下就不怕实体类的修改了...
        // 实体类随便改，
        // 我们还能进一步优化， 将这个 UserEntityHelper 改的更通用，
        // 跳到XxxEntity_Helper 类看看
        //
        // 创建新的实体对象
        UserEntity ue = new UserEntity();

        // 获取类的字段数组
        Field[] fArray = ue.getClass().getFields();

        for (Field field : fArray) {
            // 获取字段上注解
            Column annoColumn = field.getAnnotation(Column.class);

            if (annoColumn == null) {
                // 如果注解为空，
                // 则直接跳过。。。
                continue;
            }

            // 获取列名称
            String colName = annoColumn.name();
            // 从数据库中获取列值
            Object colVal = rs.getObject(colName);

            if (colVal == null) {
                // 如果列值为空，
                // 则直接跳过
                continue;
            }

            // 设置字段值
            field.set(ue, colVal);
        }

        return ue;
    }
}
