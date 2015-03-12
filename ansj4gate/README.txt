这是集成 Ansj 中文分词器到  Gate 中的插件，制作参考网址：
1) http://gatechinese.com/bbs/thread-115-1-1.html
2) http://gatechinese.com/blog/gate中文自然语言处理系列/02/
插件类：gate.creole.tokeniser
安装方式：将该工程打包成jar包
第一步：拷贝jar包
另外需要用到两个jar包请再lib目录中拷贝，分别是：
1 ansj_seg-2.0.8.jar
2 nlp-lang-0.2.jar

在Gate主目录在plugins文件夹中新建 Ansj4Gate目录，拷贝上面三个JAR包到该目录下面。

第二步：新建creole.xml配置文件
在同目录下面新建：creole.xml，拷贝下面内容：

<?xml version="1.0"?>
<!-- $Id$ -->
<CREOLE-DIRECTORY>
	<JAR SCAN="true">AnsjForGate.jar</JAR>
    <JAR SCAN="true">ansj_seg-2.0.8.jar</JAR>
    <JAR SCAN="true">nlp-lang-0.2.jar</JAR>

	<!-- Processing Resources -->
	<CREOLE>
		<!-- creole.xml for the Chinese Segmenter PR -->
		<RESOURCE>
			<NAME>Ansj Chinese Tokeniser</NAME>
			<COMMENT>A customisable Chinese tokeniser.</COMMENT>
			<HELPURL>http://gatechinese.com</HELPURL>
			<CLASS>gate.creole.tokeniser.AnsjGateTokeniser</CLASS>
		</RESOURCE>		
	</CREOLE>
</CREOLE-DIRECTORY>

第三步：拷贝library文件夹
最后拷贝library目录到gate.exe同一级目录中。