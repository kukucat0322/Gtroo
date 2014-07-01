package cn.burgeon.core;

public class Constant {
    public static final String[] sysTextValues = {"销售管理", "会员管理", "调拨说明", "盘点管理", "库存管理", "系统管理"};
    public static final int[] sysImgValues = {R.drawable.manager_sale, R.drawable.manager_member, R.drawable.manager_allot,
            R.drawable.manager_check, R.drawable.manager_inventory, R.drawable.manager_sys};

    public static final String[] allotManagerTextValues = {"调入调出", "补货", "补货订单"};
    public static final int[] allotManagerImgValues = {R.drawable.allot_in_out, R.drawable.allot_replenishment_manager, R.drawable.allot_replenishment_order_manager};

    public static final String[] checkManagerTextValues = {"盘点扫描", "单据管理", "盘点查询", "盘点上传"};
    public static final int[] checkManagerImgValues = {R.drawable.check_scan, R.drawable.check_doc_manager, R.drawable.check_query, R.drawable.check_upload};

    public static final String[] allotOutInTextValues = {"调入", "调出", "调入查询", "调出查询", "调拨上传", "申请上传"};
    public static final int[] allotOutInImgValues = {R.drawable.allot_in, R.drawable.allot_out, R.drawable.allot_in_query,
            R.drawable.allot_out_query, R.drawable.allot_upload, R.drawable.allot_shipper_upload};

    public static final String[] replenishmentTextValues = {"补货", "补货上传", "补货查询"};
    public static final int[] replenishmentImgValues = {R.drawable.replenishment, R.drawable.replenishment_upload, R.drawable.replenishment_query};

    public static final String[] replenishmentOrderTextValues = {"补货订单", "补货订单上传", "补货订单查询"};
    public static final int[] replenishmentOrderImgValues = {R.drawable.replenishment_order, R.drawable.replenishment_order_upload, R.drawable.replenishment_order_query};

    public static final String[] salesTopMenuTextValues = {"日常销售", "销售报表", "销售上传"};
    public static final int[] salesTopMenuImgValues = {R.drawable.sales_daily, R.drawable.sales_report, R.drawable.sales_upload};

    public static final String[] inventoryManagerTextValues = {"库存查询", "刷新库存", "临店库存"};
    public static final int[] inventoryManagerImgValues = {R.drawable.inventory_query, R.drawable.inventory_refresh, R.drawable.inventory_near};

    public static final String[] salesReportTextValues = {"单据查询", "销售日报", "销售月报", "商品汇总", "业绩查询"};
    public static final int[] salesReportImgValues = {R.drawable.sales_report_doc_query, R.drawable.sales_report_daily, R.drawable.sales_report_monthly,
            R.drawable.sales_report_summer_total, R.drawable.sales_report_summer_query};

    public static final String[] memberManagerTextValues = {"会员注册", "会员查询", "会员上传", "会员下载"};
    public static final int[] memberManagerImgValues = {R.drawable.member_regist, R.drawable.member_query, R.drawable.member_upload, R.drawable.member_down};

    public static final String[] systemManagerTextValues = {"系统配置", "网络测试", "系统升级", "资料下载", "数据清理"};
    public static final int[] systemManagerImgValues = {R.drawable.system_configuration, R.drawable.system_net_test, R.drawable.system_update, R.drawable.system_data_download,R.drawable.system_data_clean};

}
