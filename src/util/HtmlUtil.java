package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.codec.*;


/**
 * 把原始文本格式化为HTML文本，在聊天窗口实现居中字体颜色等
 * @param //info 昵称、ip和端口号的文本。
 * @param //origin 原始文本。
 * @return 处理后的HTML文本。
 */



public class HtmlUtil {

	public static String formatText2HTML(String origin, boolean isProtect) {
		String escaped = isProtect?protect(origin):origin;
		String[] res = escaped.split("\n");
		StringBuilder stringBuilder = new StringBuilder();
		for(String s : res) {
			stringBuilder.append("<li>");
			stringBuilder.append(s);
			stringBuilder.append("</li>");
		}
		return stringBuilder.toString();
	}
	public static String addUserInfo(String info, String s) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<ul>");
		stringBuilder.append("<font color=\"#008800\">");
		stringBuilder.append(info);
		stringBuilder.append(":</font>");
		stringBuilder.append(s);
		stringBuilder.append("</ul>");
		return stringBuilder.toString();
	}
	public static String addSystemInfo(String info, String s) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<ul>");
		stringBuilder.append("<font color=\"#088880\"><strong>");
		stringBuilder.append(info);
		stringBuilder.append("</strong>:</font>");
		stringBuilder.append(s);
		stringBuilder.append("</ul>");
		return stringBuilder.toString();
	}
	/**
	 * 把新的内容追加到原HTML中。
	 * @param newContent
	 * @return 追加了新的内容的HTML文本。
	 */
	public static String append(String originHTML, String newContent) {
		Date date = new Date();
		//设置要获取到什么样的时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//获取String类型的时间
		String createdate = sdf.format(date);
		String newEnd = newContent + "</body></html>";
		String newHTML = originHTML.replaceFirst("<\\/body>[\\s\\S]*<\\/html>", "时间："+createdate+newEnd);
		return newHTML;
	}
	public static String getBase() {
		return "<html><style type=\"text/css\"> ul{ margin: 0px; list-style-type:none; } div{ text-align:center; }</style><body></body></html>";
	}
	public static String welcome(String nickname) {
		String s = "<div><font color=\"#880000\">用户 " + protect(nickname) + " 进入了聊天室。</font></div>";
		return s;
	}
	public static String leave(String nickname) {
		String s = "<div><font color=\"#000088\">用户 " + protect(nickname) + " 离开了聊天室。</font></div>";
		return s;
	}
	public static String serverShutdown() {
		String s = "<div><font color=\"#000088\"><strong>注意：服务器已关闭。</strong></font></div>";
		return s;
	}
	/**
	 * 防止直接输入html代码到聊天窗口或昵称上导致的标签注入。
	 * @author
	 * @param origin 可能存在标签注入的字符串。
	 * @return
	 */
	private static String protect(String origin) {
		String r;
		r = origin.replaceAll("&", "&amp;");
		r = r.replaceAll(" ", "&nbsp;");
		r = r.replaceAll("\"", "&quot;");
		r = r.replaceAll("<", "&lt;");
		r = r.replaceAll(">", "&gt;");
		return r;
	}
}
