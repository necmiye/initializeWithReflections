package com.reflections;

public interface IApplicationContext {

    void displayAllBean();

    Object getBeanByName(Class<?> bean);

}
