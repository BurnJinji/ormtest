package com.burning8393.ormtest.step030.entity;

import java.lang.reflect.Field;
import java.sql.ResultSet;

public class XxxEntityHelper {
    /**
     * 将数据集转换为实体对象
     *
     * @param rs 数据集
     * @return
     * @throws Exception
     */
    public <TEntity> TEntity create(Class<?> entityClazz, ResultSet rs) throws Exception {
        if (null == rs) {
            return null;
        }
        //
        // 更通用的助手类，
        // 甭管实体类是哪个，也甭管实体类有多少属性，
        // 全都可以，
        // 但是性能太差
        //
        // 创建新的实体对象
        Object entityObj = entityClazz.newInstance();

        // 获取类的字段数组
        Field[] fArray = entityClazz.getFields();

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
            field.set(entityObj, colVal);
        }

        return (TEntity) entityObj;
    }
}
