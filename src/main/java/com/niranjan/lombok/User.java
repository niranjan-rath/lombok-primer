package com.niranjan.lombok;

import lombok.Builder;
import lombok.Data;

/**
 * Created by NIRRATH on 19-6-2017.
 */
@Data
@Builder
public class User {
    private String name;
    private String surname;
    private int age;
}
