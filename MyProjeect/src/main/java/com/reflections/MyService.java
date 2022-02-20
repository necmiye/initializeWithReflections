package com.reflections;

@com.reflections.Bean
public  class MyService extends Object {

    @Inject
    private com.reflections.MyDao myDao;

    public void test() {
        System.out.println("MyService : test");
        myDao.test();
    }
}