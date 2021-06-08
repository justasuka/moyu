package top.cfl.cflwork.util;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ObjectKit {

    /**
     * 用于在java端构建树结构的数据使用，要求有字段id和parentId,和children字段
     * @param srcList
     * @param parentId
     * @param <T>
     * @return
     */
    public static <T> List<T> buildTree(List<T> srcList,String parentId){
        List<T> list = findTByParentId(srcList, parentId);
        try {
            for (T t : list) {
                Class<?> clazz = t.getClass();
                Method idMethod = clazz.getDeclaredMethod("getId");
                Method childrenMethod = clazz.getDeclaredMethod("setChildren",List.class);
                List<T> children = buildTree(srcList, idMethod.invoke(t).toString());
                if(children.size()!=0){
                    childrenMethod.invoke(t,children);
                }


            }
            return list;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }
    private static <T> List<T> findTByParentId(List<T> srcList,String parentId)  {
        List<T> result = new ArrayList<>();

        try {
            for (T t : srcList) {
                Class<?> clazz = t.getClass();
                Method parentIdMethod = clazz.getMethod("getParentId");
                if(parentIdMethod.invoke(t).equals(parentId)){
                    result.add(t);
                }
            }
            return result;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


}
