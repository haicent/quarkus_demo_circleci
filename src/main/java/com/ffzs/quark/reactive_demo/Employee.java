package com.ffzs.quark.reactive_demo;


import javax.persistence.*;

/**
 * @Author: ffzs
 * @Date: 2020/10/27 下午4:40
 */


@Entity
@Table(name = "employee")
@NamedQuery(name = Employee.FIND_ALL, query = "SELECT e FROM Employee e")
public class Employee {
    public static final String FIND_ALL = "Employee.findAll";

    @Id
    @SequenceGenerator(name = "fruitsSequence", sequenceName = "known_fruits_id_seq", allocationSize = 1, initialValue = 10)
    @GeneratedValue(generator = "fruitsSequence")
    private Long id;

    @Column(length = 40)
    private String name;
    private Integer age;
    @Column(length = 60, unique = true)
    private String email;

    public Employee() {}

    public Employee(Long id, String name, Integer age, String email) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
