package com.fwtai.tool;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户端
 * @作者 田应平
 * @版本 v1.0
 * @创建时间 2021-02-08 10:43
 * @QQ号码 444141300
 * @Email service@dwlai.com
 * @官网 http://www.fwtai.com
 */
public final class ToolClient{

  public static HttpServerResponse getResponse(final RoutingContext context){
    return context.response().putHeader("Cache-Control","no-cache").putHeader("content-type","text/html;charset=utf-8");
  }

  /**获取表单请求参数*/
  public static HashMap<String,String> getParams(final RoutingContext context){
    final HashMap<String,String> result = new HashMap<>();
    final List<Map.Entry<String,String>> list = context.queryParams().entries();
    for(int i = 0; i < list.size(); i++){
      final Map.Entry<String,String> entry = list.get(i);
      final String value = entry.getValue();
      if(value != null && !value.isEmpty()){
        result.put(entry.getKey(),entry.getValue());
      }
    }
    return result;
  }
}