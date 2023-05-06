package com.ffzs.quark.reactive_demo;

import io.quarkus.vertx.web.Param;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteBase;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;

import java.net.InetAddress;
import java.net.UnknownHostException;
/**
 * @Author: ffzs
 * @Date: 2020/10/31 下午6:32
 */

@RouteBase(path = "/hello")
public class HelloWorldRoutes {

    @Route(methods = HttpMethod.GET, path = "/:id")
    public Uni<String> hello(@Param String id) throws UnknownHostException {
        return Uni.createFrom().item("[No." + id + "] Hello world: " + InetAddress.getLocalHost().toString());
    }
}
