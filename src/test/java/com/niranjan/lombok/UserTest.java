package com.niranjan.lombok;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by NIRRATH on 19-6-2017.
 */
public class UserTest {

    @Test
    public void createUser(){
        User user = User.builder().name("tom").age(23).surname("lombok").build();
        assertEquals(user.getName(), "tom");
        assertEquals(user.getAge(), 23);
        assertEquals(user.getSurname(), "lombok");
        System.out.println(user.toString());
    }

}