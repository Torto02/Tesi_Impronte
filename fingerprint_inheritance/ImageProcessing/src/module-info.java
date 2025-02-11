module ImageProcessing {
	exports main;
	exports rendering;
	exports rendering.Utility;
	exports rendering.Utility.json;

	requires java.desktop;
	requires java.xml;
	requires json.simple;
	requires org.apache.commons.io;
	requires org.json;
}