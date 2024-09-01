# shardingsphere-5.5.0-example
   ### 参考官网：https://shardingsphere.apache.org/

--------
# 背景
>  由于网上对于springboot3.x集成的shardingsphere-5.5.0版本，都是基于本地数据源配置，而我们在实际生产
   中，都是基于远程配置中心，本地配置如：jdbc:shardingsphere:classpath:sharding.yaml。然而这种方式不能满足企业级应用开发
   ，于是，我这里做了一个集成工程，通过实现SPI方式，基于nacos配置中心，
   进行远程数据源配置，实现读写分离，分库分表。
   
   我只是雷锋，欢迎大家提出意见，欢迎大家提pr

# 集成工程
#### springboot3.x + shardingsphere-5.5.0 + nacos-2.3.0 + mybatis-plus

#### 运行环境：jdk17/jdk21 + mysql8.0

## 工程结构

      shardingsphere5-example
      |- example-common  常用的mybatis-plus
      |- local-mode-5.5.0  本地配置模式
      |- nacos-mode-5.5.0  nacos配置模式

      dependencies 
      |-parent.pom  maven工程父依赖文件
      |- scripts
         |- 数据库脚本


#### springboot3.x集成web启动方式这里就不过多介绍，直接看代码，这里主要是介绍如何引入shardingsphere-5.5.0如何配置


# shardingsphere5.X 配置

shardingsphere5.X方式与4.x以前以往不一样，shardingsphere5.X使用了原生的org.apache.shardingsphere.driver.ShardingSphereDriver，
使用spring.datasource.url=jdbc:shardingsphere:classpath:sharding.yaml方式引入数据源和分库分表配置，如下：

[sharding.yaml](local-mode-5.5.0%2Fsrc%2Fmain%2Fresources%2Fsharding.yaml)

````yaml
dataSources:
  ds_0:
    driverClassName: com.mysql.cj.jdbc.Driver
    dataSourceClassName: com.zaxxer.hikari.HikariDataSource
    url: jdbc:mysql://localhost:3306/db_sequence_0?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&autoReconnect=true&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true&nullCatalogMeansCurrent=true
    username: root
    password: root
  ds_1:
    driverClassName: com.mysql.cj.jdbc.Driver
    dataSourceClassName: com.zaxxer.hikari.HikariDataSource
    url: jdbc:mysql://localhost:3306/db_sequence_1?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&autoReconnect=true&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true&nullCatalogMeansCurrent=true
    username: root
    password: root
props:
  sql-show: true


rules:
  - !SHARDING
    tables:
      sequence:
        actualDataNodes: ds_0.sequence_${0..3},ds_1.sequence_${0..3}
        databaseStrategy: # 分库策略，缺省表示使用默认分库策略，以下的分片策略只能选其一
          standard: # 用于单分片键的标准分片场景
            shardingColumn: sharding_key
            shardingAlgorithmName: database_inline
        tableStrategy:
          standard:
            shardingColumn: sharding_key
            shardingAlgorithmName: table_inline
    shardingAlgorithms:
      database_inline: # 分库策略
        type: CLASS_BASED
        props:
          strategy: STANDARD
          algorithmClassName: com.base.strategy.StandardDbPreciseStandardShardingAlgorithm
      table_inline: # 分表策略
        type: CLASS_BASED
        props:
          strategy: STANDARD
          algorithmClassName: com.base.strategy.StandardTablePreciseStandardShardingAlgorithm
````




### 本地配置模式 local-mode-5.5.0
   结构如下图：
   ![local-config.png](script%2Fimages%2Flocal-config.png)
   
   本地模式配置比较简单，[application.yaml](local-mode-5.5.0%2Fsrc%2Fmain%2Fresources%2Fapplication.yaml)

````yaml
server:
  port: 8088
  servlet:
    context-path: /
    encoding:
      force-response: true

spring:
  profiles:
    active: dev
  main:
    allow-bean-definition-overriding: true
  application:
    name: local-mode
  datasource:
    driver-class-name: org.apache.shardingsphere.driver.ShardingSphereDriver
    url: jdbc:shardingsphere:classpath:sharding.yaml
  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 100MB


# MyBatisPlus配置
# https://baomidou.com/config/
mybatis-plus:
  # 多包名使用 例如 com.base.**.mapper,org.xxx.**.mapper
  mapperPackage: com.base.**.mapper
  # 对应的 XML 文件位置
  mapperLocations: classpath*:mapper/**/*Mapper.xml
  # 实体扫描，多个package用逗号或者分号分隔
  typeAliasesPackage: com.base.**.domain
  global-config:
    dbConfig:
      # 主键类型
      # AUTO 自增 NONE 空 INPUT 用户输入 ASSIGN_ID 雪花 ASSIGN_UUID 唯一 UUID
      # 如需改为自增 需要将数据库表全部设置为自增
      idType: ASSIGN_ID

````

核心的配置在于： 

````
spring:
  datasource:
    driver-class-name: org.apache.shardingsphere.driver.ShardingSphereDriver
    url: jdbc:shardingsphere:classpath:sharding.yaml
````

  通过jdbc:shardingsphere:classpath:sharding.yaml方式引入数据源和分库分表配置

### nacos配置模式 nacos-mode-5.5.0
> 我们尝试使用nacos配置中心，实现远程配置，实现读写分离，分库分表。结构如下图：
![nacos-config.png](script%2Fimages%2Fnacos-config.png)

> 直接看配置：
* 这里是本地配置文件
[bootstrap.yaml](nacos-mode-5.5.0%2Fsrc%2Fmain%2Fresources%2Fbootstrap.yaml)
````yaml
nacos:
  namespace: ecommerce-local
  group: MALL_GROUP
  service-address: 192.168.8.166:8848
  username: nacos
  password: nacos

spring:
  profiles:
    active: local
  application:
    name: nacos-mode
  main:
    allow-circular-references: true
    allow-bean-definition-overriding: true
  cloud:
    nacos:
      config:
        server-addr: ${nacos.service-address}
        file-extension: yaml
        namespace: ${nacos.namespace}
        group: ${nacos.group}
        username: ${nacos.username}
        password: ${nacos.password}
      discovery:
        server-addr: ${nacos.service-address}
        namespace: ${nacos.namespace}
        group: ${nacos.group}
        username: ${nacos.username}
        password: ${nacos.password}
  datasource:
    driver-class-name: org.apache.shardingsphere.driver.ShardingSphereDriver
    url: jdbc:shardingsphere:nacos:sharding.yaml?serverAddr=${nacos.service-address}&namespace=${nacos.namespace}&group=${nacos.group}&username=${nacos.username}&password=${nacos.password}
````

> nacos 配置中心有两个文件
* 1.项目配置：nacos-mode.yaml
````yaml
server:
  port: 8088
  servlet:
    context-path: /
    encoding:
      force-response: true

# spring 配置
spring:
  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 100MB

# MyBatisPlus配置
# https://baomidou.com/config/
mybatis-plus:
  # 多包名使用 例如 com.base.admin.**.mapper,org.xxx.**.mapper
  mapperPackage: com.base.**.mapper
  # 对应的 XML 文件位置
  mapperLocations: classpath*:mapper/**/*Mapper.xml
  # 实体扫描，多个package用逗号或者分号分隔
  typeAliasesPackage: com.base.**.domain
  global-config:
    dbConfig:
      # 主键类型
      # AUTO 自增 NONE 空 INPUT 用户输入 ASSIGN_ID 雪花 ASSIGN_UUID 唯一 UUID
      # 如需改为自增 需要将数据库表全部设置为自增
      idType: ASSIGN_ID
````

* 2.远程nacos数据源/分库分表配置：[sharding.yaml](local-mode-5.5.0%2Fsrc%2Fmain%2Fresources%2Fsharding.yaml)

> 本地配置url: jdbc:shardingsphere:classpath:sharding.yaml;
> 这里唯一不同把配置改为了url: jdbc:shardingsphere:nacos:sharding.yaml?serverAddr=${nacos.service-address}&namespace=${nacos.namespace}&group=${nacos.group}&username=${nacos.username}&password=${nacos.password}

# ShardingSphereURLLoader 实现配置
> 通过观察，classpath:sharding.yaml实现的是从项目路径加载sharding.yaml，而我们可以设想是否可以定义nacos:sharding.yaml，则通过nacos配置中心加载的。

* 1.于是通过阅读ShardingSphere源码，发现classpath:是通过集成ShardingSphereURLLoader实现（ClassPathURLLoader）SPI来加载本地的sharding.yaml，于是我们则参考实现ClassPathURLLoader
来实现CustomNacosURLLoader，从而达到nacos配置中心加载sharding.yaml的目的。

* 2.实现SPI：[CustomNacosURLLoader.java](nacos-mode-5.5.0%2Fsrc%2Fmain%2Fjava%2Fcom%2Fbase%2Fspi%2FCustomNacosURLLoader.java)
````java
package com.base.spi;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import lombok.SneakyThrows;
import org.apache.shardingsphere.infra.url.spi.ShardingSphereURLLoader;

import java.util.Properties;

/**
 * @title: CustomNacosURLLoader
 * @description: 实现SPI，读取远程的nacos配置
 * @author: arron
 * @date: 2024/8/28 22:09
 */
public class CustomNacosURLLoader implements ShardingSphereURLLoader {

    /**
     * 定义jdbc:shardingsphere:后的类型为nacos:
     */
    private static final String NACOS_TYPE = "nacos:";

    /**
     * 接收nacos:后的参数sharding.yaml?serverAddr=${nacos.service-address}&namespace=${nacos.namespace}&group=${nacos.group}&username=${nacos.username}&password=${nacos.password}
     * @param configurationSubject configuration dataId
     * @param queryProps url参数，已经解析成为Properties
     * @return
     */
    @Override
    @SneakyThrows
    public String load(String configurationSubject, Properties queryProps) {
        ConfigService configService = NacosFactory.createConfigService(queryProps);
        String dataId = configurationSubject;
        //获取nacos配置
        String config = configService.getConfig(dataId, queryProps.getProperty(Constants.GROUP, Constants.DEFAULT_GROUP), 500);
        Preconditions.checkArgument(config != null, "Nacos config [" + dataId + "] is Empty.");
        return config;
    }

    @Override
    public Object getType() {
        return NACOS_TYPE;
    }
}
````

* 3.声明SPI：在项目的resources目录下创建：META-INF/services/org.apache.shardingsphere.infra.url.spi.ShardingSphereURLLoader
  并且添加内容：com.base.spi.CustomNacosURLLoader（SPI 实现类）

---
# 测试：
````
http://localhost:8088/list?shardingKey=1
````
![test.png](script%2Fimages%2Ftest.png)


# 自定义分库分表参数

## 1.自定义配置：

![sharding_table.png](script%2Fimages%2Fsharding_table.png)

````yaml
       sequence_mode: # sequence 分表策略
        type: CLASS_BASED
        props:
          strategy: STANDARD
          algorithmClassName: com.base.strategy.StandardTablePreciseStandardShardingAlgorithm
          expression: SHARDING_KEY%4*8+SHARDING_KEY*2+SHARDING_KEY/8 #自定义分表公式
````


## 2.实现类添加初始化方法，接受自定义参数
````java
/**
 * @title: 分表策略
 * @description: 精确分表算法
 * @author: arron
 * @date: 2024/8/25 11:57
 */
public class TablePreciseShardingAlgorithm implements StandardShardingAlgorithm<Long> {

    /**
     * 默认常量
     */
    private static String SHARDING_KEY = "SHARDING_KEY";

    /**
     * 自定义参数
     */
    private Properties properties;

    @Override
    public void init(Properties properties) {
        this.properties = properties;
    }

    /**
     * 计算分表
     *
     * @param preciseShardingValue
     * @return
     */
    private int getShardingValue(long preciseShardingValue) {
        String expression = properties.getProperty("expression");
        String expressionStr = expression.replace(SHARDING_KEY, String.valueOf(preciseShardingValue));
        // 执行计算
        int value = ExpressionEvaluator.evaluateExpression(expressionStr);
        return value;
    }

    @Override
    public String doSharding(Collection<String> tables, PreciseShardingValue<Long> preciseShardingValue) {
        int value = getShardingValue(preciseShardingValue.getValue().longValue());

        String tableName = null;
        for (String table : tables) {
            if (table.endsWith(String.valueOf(value))) {
                tableName = table;
                break;
            }
        }
        if (StringUtils.isBlank(tableName)) {
            throw new IllegalArgumentException("根据 sharding_key取模 分表策略获取表名错误");
        }
        return tableName;
    }

    @Override
    public Collection<String> doSharding(Collection<String> tables, RangeShardingValue<Long> rangeShardingValue) {
        int size = tables.size();
        Set<String> result = new LinkedHashSet<>();
        // between and 的起始值
        long lower = rangeShardingValue.getValueRange().lowerEndpoint();
        long upper = rangeShardingValue.getValueRange().upperEndpoint();
        // 循环范围计算分库逻辑
        for (long i = lower; i <= upper; i++) {
            for (String table : tables) {
                if (table.endsWith(String.valueOf(i % size))) {
                    result.add(table);
                }
            }
        }
        return result;
    }
}

````




