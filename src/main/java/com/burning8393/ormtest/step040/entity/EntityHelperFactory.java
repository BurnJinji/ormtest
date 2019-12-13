package com.burning8393.ormtest.step040.entity;

import javassist.*;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public final class EntityHelperFactory {
    /**
     * 助手字典
     */
    private static final Map<Class<?>, AbstractEntityHelper> entityHelperMap = new HashMap<>();

    /**
     * 私有化类默认构造器
     */
    private EntityHelperFactory() {

    }

    /**
     * 获取帮助类
     *
     * @param entityClazz 实体类
     * @return 实体助手
     * @throws Exception
     */
    public static AbstractEntityHelper getEntityHelper(Class<?> entityClazz) throws Exception {
        if (null == entityClazz) {
            // 如果参数对象为空，
            // 则直接退出！
            return null;
        }

        // 获取帮助对象
        AbstractEntityHelper entityHelper = entityHelperMap.get(entityClazz);

        if (entityHelper != null) {
            // 如果帮助对象不为空，
            // 则直接返回！
            return entityHelper;
        }

// 使用 Javassist 动态生成 Java 字节码
/////////////////////////////////////////////////////////////////////////////////
        // 获取类池
        ClassPool classPool = ClassPool.getDefault();
        classPool.appendSystemPath();

        //
        // 导入相关类，生成一下代码:
        // import java.sql.ResultSet;
        // import ...
        classPool.importPackage(ResultSet.class.getName());
        classPool.importPackage(entityClazz.getName());
        // 抽象的助手类
        classPool.insertClassPath(new ClassClassPath(AbstractEntityHelper.class));
        CtClass abstractEntityHelpClazz = classPool.getCtClass(AbstractEntityHelper.class.getName());
        // 助手实现类名称
        final String helperImplClazzName = entityClazz.getName() + "Helper";

        //
        // 创建助手类，会生成如下代码：
        // public class UserEntityHelper extends AbstractEntityHelper {...
        CtClass helperClazz = classPool.makeClass(helperImplClazzName, abstractEntityHelpClazz);

        //
        // 创建默认构造器， 会生成如下代码：
        // public UserEntityHelper() { }
        CtConstructor constructor = new CtConstructor(new CtClass[0], helperClazz);
        // 空函数体
        constructor.setBody("{}");
        // 添加默认构造器
        helperClazz.addConstructor(constructor);

        // 用于创建函数代码字符串
        final StringBuilder sb = new StringBuilder();
        // 添加函数头
        sb.append("public Object create(java.sql.ResultSet rs) throws Exception {\n");
        // 生成如下代码：
        // UserEntity obj = new UserEntity();
        sb.append(entityClazz.getName())
                .append(" obj = new ")
                .append(entityClazz.getName())
                .append("();\n");

// 通过反射的方式获取类的字段数组，
// 并生成代码
/////////////////////////////////////////////////////////////////////////////////
        // 获取类的字段数组并生成代码
        Field[] fArray = entityClazz.getFields();

        for (Field field : fArray) {
            Column annoColumn = field.getAnnotation(Column.class);

            if (annoColumn == null) {
                // 如果注解为空，
                // 则直接跳过
                continue;
            }

            // 获取列名称
            String colName = annoColumn.name();

            if (field.getType() == Integer.TYPE) {
                // 生成如下代码：
                // obj.userId = rs.getInt("user_id");
                sb.append("obj.")
                        .append(field.getName())
                        .append(" = rs.getInt(\"")
                        .append(colName)
                        .append("\");\n");
            } else if (field.getType().equals(String.class)) {
                // 生成如下代码 :
                // obj._userName = rs.getString("user_name");
                sb.append("obj.")
                        .append(field.getName())
                        .append(" = rs.getString(\"")
                        .append(colName)
                        .append("\");\n");
            } else {
                // 不支持的类型
            }
        }
        sb.append("return obj;\n");
        sb.append("}");

        String ss = sb.toString();
        System.out.println(ss);
        // 创建解析方法
        CtMethod cm = CtNewMethod.make(ss, helperClazz);
        // 添加方法
        helperClazz.addMethod(cm);
        // 获取JAVA类
        Class javaClazz = helperClazz.toClass();

// 创建帮助对象实例
//////////////////////////////////////////////////////////////////////////
        //
        // 创建帮助实例
        entityHelper = (AbstractEntityHelper) javaClazz.newInstance();
        // 添加到字典
        entityHelperMap.put(entityClazz, entityHelper);

        return entityHelper;
    }
}
