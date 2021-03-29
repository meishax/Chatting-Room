package util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.*;

import static util.DESCrypt.crypt_mode.DECRYPT_MODE;
import static util.DESCrypt.crypt_mode.ENCRYPT_MODE;

/**
 * 根据类型构建不同的消息
 * @param //info 昵称、ip和端口号的文本。
 * @param //origin 原始文本。
 * @return 处理后的HTML文本。
 */


public class MessageConstructor {
	public static class Code {
		public static class UDP {
			public static final int REQUEST_SERVER_ADDRESS_AND_PORT = 0;
			public static final int SERVER_ADDRESS_AND_PORT_FEEDBACK = 1;
		}
		public static class UDPMessage {
			public static final int MESSAGE_FROM_CLIENT_TO_SERVER = 0;
			public static final int MESSAGE_FROM_SERVER_TO_CLIENT = 1;
			public static final int LOGIN_REQUEST = 3;
			public static final int LOGIN_FEEDBACK = 4;
			public static final int MESSAGE_PRIVATE = 5;
			public static final int USER_ONLINE = 6;
			public static final int USER_OFFLINE = 7;
			public static final int CURRENT_USER_REQUEST = 8;
			public static final int CURRRENT_USER_FEEDBACK = 9;
		}
	}
	public static class Msg {
		private int code;
		private String message;
		public Msg(int code, String additionMessage) {
			this.code = code;
			message = additionMessage;
		}
		public int getCode() {
			return code;
		}
		public String getMessage() {
			return message;
		}
	}
	
	public static String constructMessage(int code, String additionMessage) {
		String cipher = new String();
		String key = new String();
		String result = new String();
		key = "MIKA";
		cipher = additionMessage;
		DESCrypt desCrypt = new DESCrypt();
		result = desCrypt.doFinal(ENCRYPT_MODE,cipher,key, DESCrypt.paraCoding.NOBASE64);
		return String.valueOf(code) + "-" + result;
	}
	/**
	 * 解析消息
	 * @param s
	 * @return
	 */
	public static Msg parseMessage(String s) {
		String cipher = new String();
		String key = new String();
		String result = new String();
		cipher = s;
		key = "MIKA";
		DESCrypt desCrypt = new DESCrypt();
		result = desCrypt.doFinal(DECRYPT_MODE,cipher,key, DESCrypt.paraCoding.NOBASE64);
		Pattern pattern = Pattern.compile("(\\d+)-(.*)");
		Matcher matcher = pattern.matcher(result);
		if(matcher.find()) {
			return new Msg(Integer.parseInt(matcher.group(1)), matcher.group(2));
		}
		return null;
	}
	/**
	 * 解析地址和端口
	 * @param s
	 * @return
	 */
	public static Msg parseIPAP(String s) {
		Pattern pattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)");
		Matcher matcher = pattern.matcher(s);
		if(matcher.find()) {
			return new Msg(Integer.parseInt(matcher.group(2)), matcher.group(1));
		}
		return null;
	}
	public static String constructPrivateMessage(String message, String nickname) {
		return nickname + "-" + message;
	}
	public static String[] parsePrivateMessage(String s) {
		Pattern pattern = Pattern.compile("([^-]*)-(.*)");
		Matcher matcher = pattern.matcher(s);
		if(matcher.find()) {
			String nickname = matcher.group(1);
			String message = matcher.group(2);
			String[] arr = new String[]{nickname, message};
			return arr;
		}
		return null;
	}
	public static Msg constructCurrentUserMsg(String nickname, Msg pre) {
		String next;
		if(pre.getCode() == 0) {
			next = nickname;
		}
		else {
			next = pre.getMessage() + "-" + nickname;
		}
		return new Msg(pre.getCode() + 1, next);
	}
	public static List<String> parseCurrentUserMsg(String msg) {
		Msg infoPair = parseMessage(msg);
		List<String> nicknameSet = new ArrayList<String>();
		if(infoPair.getCode() == 0) {
			return nicknameSet;
		}
		if(infoPair.getCode() == 1) {
			nicknameSet.add(infoPair.getMessage());
			return nicknameSet;
		}
		String unparse = infoPair.getMessage();
		for(int i=0; i< infoPair.getCode() - 1; i++) {
			String[] x = parsePrivateMessage(unparse);
			System.out.println(x[1]);
			nicknameSet.add(x[0]);
			unparse = x[1];
		}
		nicknameSet.add(unparse);
		return nicknameSet;
	}
}
