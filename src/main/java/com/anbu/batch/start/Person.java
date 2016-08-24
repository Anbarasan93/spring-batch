package com.anbu.batch.start;


import javax.persistence.*;
import java.math.BigInteger;

/**
 * Created by anbganapathy on 8/23/2016.
 */


@Entity
@Table(name = "PERSON")
public class Person {




    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Person() {}



    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;





    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }




}
