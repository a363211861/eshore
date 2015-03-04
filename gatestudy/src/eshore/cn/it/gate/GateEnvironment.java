package eshore.cn.it.gate;

import gate.util.Out;

public final class GateEnvironment {
	//指定GATE安装目录
	private static String gateHome = "F:\\java\\GATE_Developer_8.0";
	/**
	 * method<code>setGateHome</code>
	 * 此方法可以设置GATE安装目录环境变量
	 * */
	public static void setGateHome() {
		System.setProperty("gate.home", gateHome);
		Out.prln("The GATE_HOME directory is:" + (System.getProperties().getProperty("gate.home")));
	}
}
