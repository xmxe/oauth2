package me.zhyd.justauth.custom;

public enum LoginPlatFormEnum {
	DINGTALK("dingtalk"),
	BAIDU("baidu"),
	GITHUB("github"),
	GITEE("gitee"),
	WEIBO("weibo"),
	CODING("coding"),
	OSCHINA("oschina"),
	ALIPAY("alipay"),
	QQ("qq"),
	WECHAT_OPEN("wechat_open"),
	CSDN("csdn"),
	TAOBAO("taobao"),
	GOOGLE("google"),
	FACEBOOK("facebook"),
	DOUYIN("douyin"),
	LINKEDIN("linkedin"),
	MICROSOFT("microsoft"),
	MI("mi"),
	TOUTIAO("toutiao"),
	TEAMBITION("teambition"),
	PINTEREST("pinterest"),
	RENREN("renren"),
	STACK_OVERFLOW("stack_overflow"),
	HUAWEI("huawei"),
	WECHAT_ENTERPRISE("wechat_enterprise"),
	KUJIALE("kujiale"),
	GITLAB("gitlab"),
	MEITUAN("meituan"),
	ELEME("eleme"),
	TWITTER("twitter"),
	WECHAT_MP("wechat_map"),
	ALIYUN("aliyun"),
	XMLY("xmly"),
	FEISHU("feishu");

	private String source;

	LoginPlatFormEnum(String source){
		this.source = source;
	}

	public String getSource(){
		return source;
	}

	public static LoginPlatFormEnum getLoginPlatForm(String source){
		for (LoginPlatFormEnum loginPlatFormEnum : LoginPlatFormEnum.values()) {
			if (loginPlatFormEnum.getSource().equals(source)) {
				return loginPlatFormEnum;
			}
		}
		return null;
	}
}
