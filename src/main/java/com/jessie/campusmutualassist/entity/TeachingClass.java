package com.jessie.campusmutualassist.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class TeachingClass implements Serializable {

    private String name;
    private String teacher;
    private String id;

}
