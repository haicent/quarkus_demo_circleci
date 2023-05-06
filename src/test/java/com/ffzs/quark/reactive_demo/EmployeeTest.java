package com.ffzs.quark.reactive_demo;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

/**
 * @Author: ffzs
 * @Date: 2020/10/27 下午6:20
 */

@QuarkusTest
public class EmployeeTest {

    @Test
    public void testApi (){

        // 测试getAll
        given().when().get("/employee/")
                .then()
                .statusCode(200)
                .body(
                        containsString("ffzs"),
                        containsString("fanfanzhisu"),
                        containsString("vincent")
                );

        // 测试getOne
        given().when().get("/employee/1")
                .then()
                .statusCode(200)
                .body(containsString("ffzs"));

        // 测试delete
        given()
                .when()
                .delete("/employee/1")
                .then()
                .statusCode(204);

        given().when().get("/employee/")
                .then()
                .statusCode(200)
                .body(
                        not(containsString("ffzs"))
                );



        // 测试create
        given()
                .when()
                .body("{\"name\" : \"cat\", \"age\" : \"10\", \"email\" : \"cat@163.com\"}")
                .contentType("application/json")
                .post("/employee/")
                .then()
                .statusCode(201);

        given().when().get("/employee/")
                .then()
                .statusCode(200)
                .body(
                        containsString("cat")
                );

    }
}
