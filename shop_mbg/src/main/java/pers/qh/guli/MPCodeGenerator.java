package pers.qh.guli;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.fill.Property;

public class MPCodeGenerator {
    private static final String IS_AUTH = "qianhui";//作者
    private static final String OUT_DIR = "F:\\gitee\\guli\\gulimall-parent\\shop_core\\shop_product";//输出目录
    private static final String PARENT_CLASS_NAME = "pers.qh.guli";//父包名
    private static final String MYSQL_IP = "192.168.16.16:3306";//数据库地址
    private static final String MYSQL_DATABASE_NAME = "shop_product";//数据库名称
    private static final String MYSQL_TABLE_NAME = "User";//表名称
    private static final String MYSQL_USERNAME = "root";//数据库连接用户名
    private static final String MYSQL_PASSWORD = "root";//数据库连接密码

    public static void main(String[] args) {
        //创建一个代码生成器
        FastAutoGenerator
                .create("jdbc:mysql://"+MYSQL_IP+"/" + MYSQL_DATABASE_NAME +
                                "?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf-8",
                        MYSQL_USERNAME,
                        MYSQL_PASSWORD)
                //全局配置(GlobalConfig)
                .globalConfig(builder -> {
                    builder.author(IS_AUTH) // 设置作者
                            .commentDate("yyyy-MM-dd")//注释日期
                            .enableSwagger()//开启Swagger模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir(OUT_DIR + "\\src\\main\\java") // 指定输出目录，一般指定到java目录
                            .disableOpenDir(); //禁止打开输出目录
                })
                //包配置(PackageConfig)
                .packageConfig(builder -> {
                    builder
                            .parent(PARENT_CLASS_NAME) // 设置父包名
                            //.moduleName("") // 设置父包模块名
                            //.entity("pojo")
                            //.service("service")
                            //.serviceImpl("service.impl")
                            //.controller("controller")
                            //.mapper("mapper")
                            //.xml("xml")
                            //.pathInfo(Collections.singletonMap(OutputFile.mapperXml,
                    // OUT_DIR + "\\src\\main\\resources\\mapper")); // 设置mapperXml生成路径
                    ;
                })
                //策略配置(StrategyConfig)
                .strategyConfig(builder -> {
                    builder
                            //.addTablePrefix("t_")//设置过滤表前缀
                            .addInclude("platform_property_key","platform_property_value"); // 设置表名,多个用逗号
                    builder
                            .entityBuilder()
                            .enableLombok() //开启 lombok 模型
                            .naming(NamingStrategy.underline_to_camel)//生成驼峰命名
                            .enableChainModel()//链式书写
                            .enableTableFieldAnnotation() //生成字段注解
                            .addTableFills(new Property("create_time", FieldFill.INSERT))
                            .addTableFills(new Property("update_time",FieldFill.INSERT_UPDATE))
                            .logicDeletePropertyName("is_deleted");//逻辑删除
                    builder
                            .mapperBuilder()
                            .formatMapperFileName("%sDao")
                            .formatXmlFileName("%sMapper");
                    builder
                            .serviceBuilder()
                            .formatServiceFileName("%sService") //设置service的命名策略
                            .formatServiceImplFileName("%sServiceImpl"); //设置serviceImpl的命名策略
                    builder
                            .controllerBuilder()
                            .formatFileName("%sController")
                            //.enableHyphenStyle()//开启驼峰装连字符
                            .enableRestStyle(); //开启生成@RestController控制器，不配置这个默认是@Controller注解
                })
                //.templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute(); //执行以上配置
    }
}