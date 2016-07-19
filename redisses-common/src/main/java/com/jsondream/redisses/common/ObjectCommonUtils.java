package com.jsondream.redisses.common;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * <p>
 * 用来做对象转map
 * </p>
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 16/7/19
 */
public class ObjectCommonUtils {

    public static void main(String[] a) {

    }

    /**
     * 在最内侧补货异常，只会丢失错误的参数的代码，可以在补货异常的部分打印错误日志，方便开发人员调试，但是这种循环补货
     * 异常的方式对程序的执行效率影响较大
     *
     * @param o
     * @param fields
     * @return
     * @throws Exception
     * @version 1.0
     */
    public static Map<String, String> objResolveToMap(Object o, String... fields) {

        List<String> fieldList = new ArrayList<>();
        if (fields == null || fields.length == 0) {
            // 获得这个对象的所有属性
            fieldList = getAllFieldName(o.getClass());
        } else {
            fieldList.addAll(Arrays.asList(fields));
        }
        Map<String, String> map = new HashMap<>();

        // 判断这个类是否有属性,或者传递的属性值是否正确
        if (fieldList.isEmpty())
            return map;
        // 遍历属性并且取值
        for (String field : fieldList) {
            // 获取这个对象对应的属性值
            Object value = null;
            try {
                value = getMethodInvoke(o, field);
            } catch (Exception e) {
            }
            if (value == null)
                continue;
            map.put(field, String.valueOf(value));
        }

        return map;
    }

    /**
     * 执行这个对象的属性的get方法取值
     *
     * @param object
     * @param field
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> Object getMethodInvoke(T object, String field) throws Exception {
        PropertyDescriptor pd = new PropertyDescriptor(field, object.getClass());
        Method wM = pd.getReadMethod();//获得写方法
        return wM.invoke(object);
    }

    /**
     * 获得对象的所有属性名
     *
     * @param clazz
     * @return
     */
    public static List<String> getAllFieldName(Class<?> clazz) {
        List<String> allFieldName = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if ("serialVersionUID".equals(fieldName))
                continue;
            // TODO: fieldName非空校验
            allFieldName.add(fieldName);
        }
        return allFieldName;
    }

    /**
     * 外层补货异常,并打印日志,在错误的属性名之后的所有属性值都无法获取,即使有异常也可以正确执行,但是没有v1对效率影响大
     *
     * @param o
     * @param fields
     * @return
     * @throws Exception
     * @version 2.0
     */
    public static Map<String, String> objResolveToMapV2(Object o, String... fields) {

        List<String> fieldList = new ArrayList<>();
        if (fields == null || fields.length == 0) {
            // 获得这个对象的所有属性
            fieldList = getAllFieldName(o.getClass());
        } else {
            fieldList.addAll(Arrays.asList(fields));
        }
        Map<String, String> map = new HashMap<>();

        // 判断这个类是否有属性,或者传递的属性值是否正确
        if (fieldList.isEmpty())
            return map;
        // 遍历属性并且取值
        try {
            for (String field : fieldList) {
                // 获取这个对象对应的属性值
                Object value = null;

                value = getMethodInvoke(o, field);

                if (value == null)
                    continue;
                map.put(field, String.valueOf(value));
            }
        } catch (Exception e) {
        }
        return map;
    }

    /**
     * 抛出异常让开发人员检查，如果有异常这段代码将不能正确执行
     *
     * @param o
     * @param fields
     * @return
     * @throws Exception
     * @version 3.0
     */
    public static Map<String, String> objResolveToMapV3(Object o, String... fields)
        throws Exception {

        List<String> fieldList = new ArrayList<>();
        if (fields == null || fields.length == 0) {
            // 获得这个对象的所有属性
            fieldList = getAllFieldName(o.getClass());
        } else {
            fieldList.addAll(Arrays.asList(fields));
        }
        Map<String, String> map = new HashMap<>();

        // 判断这个类是否有属性,或者传递的属性值是否正确
        if (fieldList.isEmpty())
            return map;
        // 遍历属性并且取值
        for (String field : fieldList) {
            // 获取这个对象对应的属性值
            Object value = null;

            value = getMethodInvoke(o, field);

            if (value == null)
                continue;
            map.put(field, String.valueOf(value));
        }
        return map;
    }

    /**
     * 将一个 JavaBean 对象转化为一个  Map
     *
     * @param bean 要转化的JavaBean 对象
     * @return 转化出来的  Map 对象
     * @throws IntrospectionException    如果分析类属性失败
     * @throws IllegalAccessException    如果实例化 JavaBean 失败
     * @throws InvocationTargetException 如果调用属性的 setter 方法失败
     */
    public static Map<String, String> convertBean(Object bean)
        throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        Class type = bean.getClass();
        Map<String, String> returnMap = new HashMap<>();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(bean, new Object[0]);
                if (result != null) {
                    returnMap.put(propertyName, String.valueOf(result));
                } else {
                    returnMap.put(propertyName, "");
                }
            }
        }
        return returnMap;
    }

}
