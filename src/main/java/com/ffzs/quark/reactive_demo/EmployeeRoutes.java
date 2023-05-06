package com.ffzs.quark.reactive_demo;

import io.quarkus.vertx.web.Body;
import io.quarkus.vertx.web.Param;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteBase;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.hibernate.reactive.mutiny.Mutiny;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.NoSuchElementException;

import static io.quarkus.vertx.web.Route.HandlerType.FAILURE;
import static io.vertx.core.http.HttpMethod.*;

/**
 * @Author: ffzs
 * @Date: 2020/10/27 下午4:40
 */

@RouteBase(path = "/employee", produces = "application/json")
public class EmployeeRoutes {

    private static final Logger LOGGER = Logger.getLogger(EmployeeRoutes.class.getName());

    @Inject
    Mutiny.Session session;

    @Route(methods = HttpMethod.GET, path = "/")
    public Uni<List<Employee>> getAll() {
        return session.createNamedQuery(Employee.FIND_ALL, Employee.class).getResultList();
    }

    @Route(methods = HttpMethod.GET, path = "/:id")
    public Uni<Employee> findById(@Param String id){
        return session.find(Employee.class, Long.valueOf(id));
    }

    @Route(methods = POST, path = "/")
    public Uni<Employee> create(@Body Employee employee, HttpServerResponse response) {
        if (employee == null || employee.getId() != null) {
            return Uni.createFrom().failure(new IllegalArgumentException("request中无数据或是包含id无法创建请考虑update"));
        }
        return session.persist(employee)
                .chain(session::flush)
                .onItem().transform(ignore -> {
                    response.setStatusCode(201);
                    return employee;
                });
    }

    @Route(methods = PUT, path = "/:id")
    public Uni<Employee> update(@Body Employee fruit, @Param String id) {
        if (fruit == null || fruit.getName() == null) {
            return Uni.createFrom().failure(new IllegalArgumentException("request信息中没有员工名"));
        }
        return session.find(Employee.class, Long.valueOf(id))
                // 如果id存在信息的话进行更新操作
                .onItem().ifNotNull().transformToUni(entity -> {
                    entity.setName(fruit.getName());
                    return session.flush()
                            .onItem().transform(ignore -> entity);
                })
                // 否则fail
                .onItem().ifNull().fail();
    }

    @Route(methods = DELETE, path = "/:id")
    public Uni<Employee> delete(@Param String id, HttpServerResponse response) {
        return session.find(Employee.class, Long.valueOf(id))
                // id 存在的话删除
                .onItem().ifNotNull().transformToUni(entity -> session.remove(entity)
                        .chain(session::flush)
                        .map(ignore -> {
                            response.setStatusCode(204).end();
                            return entity;
                        }))
                // 否则报错
                .onItem().ifNull().fail();
    }


    @Route(path = "/*", type = FAILURE)
    public void error(RoutingContext context) {
        Throwable t = context.failure();
        if (t != null) {
            LOGGER.error("请求处理失败", t);
            int status = context.statusCode();
            String chunk = "";
            if (t instanceof NoSuchElementException) {
                status = 404;
            } else if (t instanceof IllegalArgumentException) {
                status = 422;
                chunk = new JsonObject().put("code", status)
                        .put("exceptionType", t.getClass().getName()).put("error", t.getMessage()).encode();
            }
            context.response().setStatusCode(status).end(chunk);
        } else {
            // 非特殊处理情况使用默认处理
            context.next();
        }
    }
}
