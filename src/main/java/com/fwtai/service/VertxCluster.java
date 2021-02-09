package com.fwtai.service;

import io.vertx.core.AbstractVerticle;

import java.util.UUID;

/**
 * Vert.x 测试集群Cluster模式
 * @作者 田应平
 * @版本 v1.0
 * @创建时间 2021-02-08 9:36
 * @QQ号码 444141300
 * @Email service@dwlai.com
 * @官网 http://www.fwtai.com
*/
public final class VertxCluster extends AbstractVerticle{

  final String verticleId = UUID.randomUUID().toString().replace("-","");

  @Override
  public void start(){
    vertx.eventBus().consumer("hello.vertx.addr",message->{
      message.reply("hello vert.x world");
    });
    vertx.eventBus().consumer("hello.named.addr",message->{
      final String name = (String) message.body();
      message.reply(name+",from "+verticleId);
    });
  }
}