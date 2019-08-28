
package com.tahoecn.bo;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/***
 * 代码生成器
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GenerateCodeBuilder {

    @Value("${spring.datasource.dynamic.datasource.master.url}")
    private String url;

    @Value("${spring.datasource.dynamic.datasource.master.username}")
    private String user;

    @Value("${spring.datasource.dynamic.datasource.master.password}")
    private String password;

    @Value("${spring.datasource.dynamic.datasource.master.driver-class-name}")
    private String driver;

    //生成文件所在项目路径
    private static String baseProjectPath = "d:/test_sql/";

    //基本包名
    private static String basePackage = "com.tahoecn.bo";

    //要生成的表名
    private static String[] tables = {
//            "bo_approve_record",
//            "bo_approve_callback_log",
//            , "bo_building"
//            , "bo_building_product_type_map"
//            , "bo_building_product_type_quota_map"
//            , "bo_building_quota_map"
//            , "bo_dictionary_item"
//            , "bo_dictionary_type"
//            , "bo_land_part_product_type_map"
//            , "bo_land_part_product_type_quota_map"
//            , "bo_product_type"
//            , "bo_project_extend"
//            "bo_project_land_part_map"
//            , "bo_project_price_extend"
//            , "bo_project_price_quota_map"
//            , "bo_project_quota_extend"
//            , "bo_project_quota_map"
//            , "bo_quota_group_map"
//            , "bo_quota_store"
//            , "bo_sys_log"
//            , "bo_sys_permission"
//            , "mdm_project_info"
//            , "uc_org"
//            , "uc_standard_role"
//            , "uc_user"
//            "tutou_th_budget_tb"
//            , "tutou_th_business_info_tb"
//            , "tutou_th_cooperation_mood_tb"
//            , "tutou_th_landarea_new_tb"
//            , "tutou_th_landarea_tb"
//            , "tutou_th_landinformation_tb"
//            , "tutou_th_landuse_tb"
//            , "tutou_th_product_position_tb"
//            , "tutou_th_state_tb"
//            "bo_project_part"
//            "bo_project_land_part_quota_map"
//            "bo_gov_plan_card",
//            "bo_project_plan_card",
//            "bo_gov_plan_card_building_map",
//            "bo_project_plan_card_building_map",
//            "bo_project_land_part_product_type_map",
//            "bo_project_land_part_product_type_quota_map",
//            "sale_room_stream",
//            "sale_room_sync_log",
//            "sale_room",
//            "bo_building_speed",
            "bo_manage_overview_price",
//            "bo_manage_overview_version",
//            "bo_manage_overview_area",
//            "bo_bpm_template_config",
//            "bo_project_supply_plan_version",
//            "bo_supply_plan_product_type_map",
//            "bo_supply_plan_data",
    };

    @Test
    public void generateCodeBuilder() {

        AutoGenerator gen = new AutoGenerator();

        gen.setDataSource(new DataSourceConfig()
                .setDbType(DbType.MYSQL)
                .setDriverName(this.driver)
                .setUrl(this.url)
                .setUsername(this.user)
                .setPassword(this.password)
        );

        gen.setGlobalConfig(new GlobalConfig()
                .setOutputDir(this.baseProjectPath)
                .setFileOverride(true)
                .setBaseResultMap(false)
                .setBaseColumnList(false)
                .setOpen(true)
                .setSwagger2(false)
                .setServiceName("%sService")
                .setIdType(IdType.INPUT)
                .setAuthor("panglx")
        );

        gen.setStrategy(new StrategyConfig()
                .setNaming(NamingStrategy.underline_to_camel)
                .setColumnNaming(NamingStrategy.underline_to_camel)
                .setInclude(tables)
                .setRestControllerStyle(true)
                .setEntityLombokModel(false)
                .setSuperControllerClass("com.tahoecn.bo.controller.TahoeBaseController")
                .setCapitalMode(false)//大写命名

        );

        gen.setPackageInfo(new PackageConfig()
                .setParent(basePackage)
                .setEntity("model.entity")
        );

        gen.setTemplateEngine(new FreemarkerTemplateEngine());
        gen.setTemplate(new TemplateConfig());

        // 执行生成
        gen.execute();
    }
}
