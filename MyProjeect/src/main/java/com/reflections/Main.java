package com.reflections;

public class Main {
    public static void main(String[] args) {

        IApplicationContext applicationContext = new com.reflections.ApplicationContext();
        MyService myService = (MyService) applicationContext.getBeanByName(MyService.class);
        myService.test();
        applicationContext.displayAllBean();

    }
}
