package com.fwtai;

import com.fwtai.service.VertxCluster;
import com.fwtai.tool.ToolClient;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Vert.x Infinispan Cluster Manager_分布式_集群,单实例启动多个则构成集群模式,如下:
 * 启动命令 java -jar cluster-fat.jar -cluster -Djava.net.preferIPv4Stack=true -Dhttp.port=8070
 * 启动命令 java -jar cluster-fat.jar -cluster -Djava.net.preferIPv4Stack=true -Dhttp.port=8060
 * 启动命令 java -jar cluster-fat.jar -cluster -Djava.net.preferIPv4Stack=true -Dhttp.port=8050
 * @作者 田应平
 * @版本 v1.0
 * @创建时间 2021-02-08 9:36
 * @QQ号码 444141300
 * @Email service@dwlai.com
 * @官网 http://www.fwtai.com
*/
public final class VertxLauncher extends AbstractVerticle{

  @Override
  public void start(final Promise<Void> start){
    vertx.deployVerticle(new VertxCluster());//ok,部署调度启动
    final Router router = Router.router(vertx);
    router.get("/index").blockingHandler(context -> {
      ToolClient.getResponse(context).end("Vertx Router,欢迎访问");
    });
    router.get("/api/v1.0/eventBusName/:name").handler(this::eventBusName);// http://127.0.0.1:8011/api/v1.0/eventBusName/fwtai
    final ConfigStoreOptions config = new ConfigStoreOptions()
      .setType("file")
      .setFormat("json")
      .setConfig(new JsonObject().put("path","config.json"));//当然也可以再创建一个
    final ConfigRetrieverOptions opts = new ConfigRetrieverOptions()
      .addStore(config);//当然可以根据上面再创建多个可以添加多个
    final ConfigRetriever cfgRetrieve = ConfigRetriever.create(vertx,opts);

    //方式1,参数类型:void getConfig(Handler<AsyncResult<JsonObject>> completionHandler);//都是函数接口类型,ok
    /*cfgRetrieve.getConfig(asyncResult ->{
      this.configHandle(start,router,asyncResult);
    });*/

    //方式2,参数类型:void getConfig(Handler<AsyncResult<JsonObject>> completionHandler);//都是函数接口类型
    final Handler<AsyncResult<JsonObject>> handler = asyncResult -> configHandle(start,router,asyncResult);
    cfgRetrieve.getConfig(handler);
  }

  protected void eventBus(final RoutingContext context){
    vertx.eventBus().request("hello.vertx.addr","",reply->{
      ToolClient.getResponse(context).end("EventBus,"+reply.result().body());
    });
  }

  protected void eventBusName(final RoutingContext context){
    final String name = context.pathParam("name");
    vertx.eventBus().request("hello.named.addr",name,reply->{
      ToolClient.getResponse(context).end("EventBus,"+reply.result().body());
    });
  }

  protected void configHandle(final Promise<Void> start,final Router router,final AsyncResult<JsonObject> asyncResult){
    if(asyncResult.succeeded()){
      final JsonObject jsonObject = asyncResult.result();//请注意json文件格式数据
      final JsonObject http = jsonObject.getJsonObject("http");
      final Integer httpPort = http.getInteger("port",9011);
      vertx.createHttpServer().requestHandler(router).listen(httpPort);
      start.complete();
    }else{
      start.fail("应用启动失败");
    }
  }
}