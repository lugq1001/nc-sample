package com.nextcont.mobilization.model

import com.nextcont.mobilization.R

data class News(val title: String, val desc: String, val url: String, val time: String) {


    companion object {

        fun data(): List<News> {
            return listOf(
                News(
                    "军队文职人员聘用合同管理暂行规定",
                    "第一条  为了规范军队文职人员聘用合同管理工作，保障用人单位和文职人员的合法权益，根据《中国人民解放军文职人员条例》等有关法律法规，制定本规定。",
                    "http://www.gfdy.gov.cn/regulations/2019-01/06/content_9419053.htm",
                    "昨天"
                ),
                News(
                    "军队行业部门廉政主管责任规定",
                    "解放军报北京3月30日电 近日，经中央军委批准，中央军委办公厅印发《军队行业部门廉政主管责任规定》（以下简称《规定》），自2020年5月1日起施行。",
                    "http://www.gfdy.gov.cn/regulations/2020-03/31/content_9781001.htm",
                    "5月29日"
                ),
                News(
                    "中央军委办公厅印发新修订的《军队社会团体管理工作规定》",
                    "新华社北京3月29日电　经中央军委批准，中央军委办公厅近日印发新修订的《军队社会团体管理工作规定》，2020年3月24日起施行。",
                    "http://www.gfdy.gov.cn/topnews/2020-03/29/content_9780060.htm",
                    "5月27日"
                ),
                News(
                    "退役士兵安置及创业就业优惠政策摘登",
                    "退役士兵符合下列条件之一的，由人民政府安排工作：士官服现役满12年的；服现役期间平时荣获二等功以上奖励或者战时荣获三等功以上奖励的；因战致残被评定为5级至8级残疾等级的；是烈士子女的。符合以上规定条件的退役士兵，在艰苦地区和特殊岗位服现役的优先安排工作。",
                    "http://www.gfdy.gov.cn/regulations/2017-12/05/content_7855355.htm",
                    "5月27日"
                ),
                News(
                    "白城市规范重大涉军涉民信息上报机制",
                    "近日，吉林省白城军分区接到通报，驻军部队在执行试验任务时发现不明“黑广播”干扰，请求地方政府协助查处不明干扰源。接此通报后，白城军分区立即协调该市无线电部门紧急采取应对措施，通过仪器监测定位，很快锁定了“黑广播”的源头。该市公安机关第一时间查封并拆除了“黑广播”发射设备。白城军地联合短时间内快速清除“黑广播”干扰，得益于该市建立的重大涉军涉民信息四级上报机制。",
                    "http://www.gfdy.gov.cn/regulations/2017-05/24/content_7688764.htm",
                    "5月27日"
                ),
                News(
                    "民用运力国防动员条例",
                    "在战时及平时特殊情况下，根据国防动员需要，国家有权依法对机关、社会团体、企业、事业单位和公民个人(以下简称单位和个人)所拥有或者管理的民用运载工具及相关设备、设施、人员，进行统一组织和调用。",
                    "http://www.gfdy.gov.cn/regulations/2016-02/17/content_7691291.htm",
                    "5月27日"
                ),
                News(
                    "军队文职人员管理规定",
                    "第一条 为了完善文职人员制度，加强文职人员管理，根据《中国人民解放军文职人员条例》以及其他有关法规，制定本规定。 ",
                    "http://www.gfdy.gov.cn/regulations/2016-02/19/content_7691289.htm",
                    "5月27日"
                ),
                News(
                    "国家安全需要全民关注",
                    "4月15日，是全民国家安全教育日。今年教育日宣传活动的主题是“坚持总体国家安全观，统筹传统安全和非传统安全，为决胜全面建成小康社会提供坚强保障”。日前，各地通过多种形式开展了宣传教育活动。",
                    "http://www.gfdy.gov.cn/education/2020-04/17/content_9794299.htm",
                    "5月27日"
                ),
                News(
                    "广西百色军分区帮扶边境贫困群众打赢脱贫攻坚战",
                    "20世纪80年代，广西百色市德保县足荣镇孟棉村15名民兵奉命参加拥军支前任务，所在民兵连因完成任务出色，被中央军委授予“英雄民兵担架连”荣誉称号。",
                    "http://www.mod.gov.cn/mobilization/2020-05/15/content_4865105.htm",
                    "5月26日"
                ),
                News(
                    "勇往直前的“蒙古马精神” 是中国军人的品质",
                    "5月22日下午，习近平总书记参加他所在的十三届全国人大三次会议内蒙古代表团审议时，勉励内蒙古的同志大力弘扬“蒙古马精神”。这是习近平总书记第三次提到“蒙古马精神”。",
                    "http://www.mod.gov.cn/education/2020-05/25/content_4865746.htm",
                    "5月25日"
                )
            )
        }

    }

}

data class BannerNews(val title: String, val image: Int, val url: String) {


    companion object {

        fun data(): List<BannerNews> {
            return listOf(
                BannerNews("纵览海军航空大学实弹打靶现场", R.mipmap.img_news_1, "http://www.mod.gov.cn/photos/2020-05/14/content_4865076.htm"),
                BannerNews("跨昼夜磨砺保障硬功", R.mipmap.img_news_2, "http://www.mod.gov.cn/photos/2019-12/05/content_4856269.htm"),
                BannerNews("直-20多用途直升机亮相天津直博会", R.mipmap.img_news_3,"http://www.mod.gov.cn/photos/2019-10/10/content_4852607.htm"),
                BannerNews("远望3号船护送北斗导航卫星入轨记", R.mipmap.img_news_4,"http://www.mod.gov.cn/photos/2019-06/25/content_4844330.htm")
            )
        }

    }

}