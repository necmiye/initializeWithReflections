package com.reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationContext implements IApplicationContext {

    Map<Class<?>, Object> applicationScope;
    ApplicationContext() {
        applicationScope = new HashMap<>();
        start();
    }

    public void start(){
        Set<Class<?>> classes = new org.reflections.Reflections("com.reflections").getTypesAnnotatedWith(Bean.class);
        for (Class<?> classz : classes) {
            try {
                Object classInstance = classz.getDeclaredConstructor().newInstance();
                checkCircuitDependency(classz);
                applicationScope.put(classz, classInstance);

            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }

        }

        applicationScope.forEach((key,value)->{
            try {
                inject(key,value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

    }

    public  void inject( Class<?> classz, Object classInstance)
        throws  IllegalAccessException {
        Set<Field> fields = findFields(classz);
        for (Field field : fields) {
            Object fieldInstance = applicationScope.get(field.getType());
            field.set(classInstance, fieldInstance);
            inject(fieldInstance.getClass(), fieldInstance);
        }
    }

    private static Set<Field> findFields(Class<?> classz) {
        Set<Field> set = new HashSet<>();
        while (classz != null) {
            for (Field field : classz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    field.setAccessible(true);
                    set.add(field);
                }
            }
            classz = classz.getSuperclass();
        }
        return set;
    }

    @Override
    public void displayAllBean() {
        Set<Class<?>> classes = new org.reflections.Reflections("com.reflections").getTypesAnnotatedWith(Bean.class);

        System.out.println("\n-Bean List-");
        for (Class<?> c : classes) {
            System.out.println("Name: " + c.getSimpleName() + ",  Type:" + c.getName() );
        }
        System.out.println("\n");
    }


    public void checkCircuitDependency(Class<?> cls) {
        List<Field> fields = Arrays.stream(cls.getDeclaredFields()).filter(field -> Objects.nonNull(field.getAnnotation(Inject.class))).collect(Collectors.toList());
        for (Field field : fields) {
            recursiveCheck(field, cls);
        }
    }

    public void recursiveCheck(Field field,Class<?> cls) {
        List<Field> fields = Arrays.stream(field.getType().getDeclaredFields()).filter(f -> Objects.nonNull(f.getAnnotation(Inject.class))).collect(Collectors.toList());

        if (!fields.isEmpty()) {
            for (Field f : fields) {
                if (f.getType().getName().equals(cls.getName())) {
                    System.err.println("circuit dependeny");
                    System.err.println(fields);
                    System.exit(0);
                }
                recursiveCheck(f,cls);
                System.out.println(f.getType().getName());
            }

        }
    }


    @Override
    public Object getBeanByName(Class<?> bean) {
        return applicationScope.get(bean);
        }


}
