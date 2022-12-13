package com.github.wnameless.spring.boot.up.web;

public interface RestfulRouteProvider<ID> {

  RestfulRoute<ID> getRestfulRoute();

}
