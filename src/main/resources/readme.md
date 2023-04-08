## 拦截器
1. 拦截器要实现`HandlerInterceptor`接口，可以重写`preHandle()`、`postHandle()`、`afterCompletion()`三种方法
  * preHandle()：前置拦截
  * postHandle()：Controller执行之后
  * afterCompletion()：渲染之后，返回给用户之前 
2. 编写配置类，添加@Configuration注解，实现`WebMvcConfigurer`接口，通过重写`addInterceptors()`方法添加拦截器

## 集群的session共享问题
* 多台Tomcat并不共享session存储空间，当请求切换到不同tomcat服务时导致数据丢失的问题
* 解决方案：使用redis存储

## 缓存
* 缓存的作用
  * 降低后端负载
  * 提高读写效率，降低响应时间
* 缓存的成本
  * 数据一致性成本
  * 代码维护成本
  * 运维成本