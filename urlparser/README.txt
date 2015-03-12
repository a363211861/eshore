urlparser 本java工程是通过一些重要的门户网站网站地图，通过JSOUP提取其中分类的锚文本信息作为分类，然后从锚文本信息对应的链接中提取关键词

本工程入口是 eshore.cn.it.main.ParseStart 不需要任何参数即可启动程序

本工程所有的资源文件在resource目录中，注意文件名称不能更改，位置也不行，除非修改源代码，其中：
filterwords.txt 表示需要过滤的关键词，允许以^开头，表示只要以这个开始的关键词全部过滤掉
sitemaps.txt 	存放各个知名网站的网站地图url以及需要选择的提取的类csspath，必须严格按照文本的实例记录形式书写
即一行注释一行文本 例如：
>>>
#新浪新闻
http://news.sina.com.cn/guide/|div#tab01 a
#网易
http://sitemap.163.com/|div.colLM a
>>>

启动程序之后，等待一段时间就会得到提取结果，结果保存在data目录下面名称叫keywords.txt文件中，保存格式如下：
>>> 
国内:news,domestic,guoneixinwen,china,internal_play,travel,life
>>>

下面具体说明各个类的结构，详细信息请阅读代码详细注释：
eshore.cn.it.data    包中主要是对数据的处理类
DataFilter.java		对关键词进行过滤
DataLoader.java		加载数据，比如过滤信息，还有提取网站
DataWriter.java		负责保存提取的关键词结果
SiteMap.java		网站地图的抽象，具体记录网站地图的网址，注释信息，网站选择器（选择目标链接区，不需要的区域不做处理）

eshore.cn.it.parser  包主要是解析网址做关键词抽取
UrlParser.java		
首先会提取指定部分的链接，以锚文本作为key、链接作为value，然后提取关键词（可以改进为边解析边提取），具体算法请查看类
注意Jsoup的用法请参考网址：http://jsoup.org/

eshore.cn.it.main	    包是程序的入口

eshore.cn.it.test	     测试包