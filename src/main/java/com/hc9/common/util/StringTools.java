package com.hc9.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class StringTools {
	private static final char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private static final String[][] sqlhandles = { { "'", "''" },
			{ "\\\\", "\\\\\\\\" } };

	public static String uuid() {
		return UUID.randomUUID().toString();
	}

	public static boolean isContainsString(String str, String[] array) {
		if (array == null) {
			return false;
		}
		for (String s : array) {
			if (s.equals(str)) {
				return true;
			}
		}
		return false;
	}

	public static String listingString(Object data) {
		return listingString(data, true);
	}

	public static String listingString(Object data, boolean snapped) {
		StringBuilder sb = new StringBuilder(100);
		sb.append(data.getClass().getSimpleName()).append("[");
		try {
			boolean flag = false;
			boolean isstring = true;
			Object obj = null;
			String str = "";
			for (Method m : data.getClass().getDeclaredMethods()) {
				if (((!m.getName().startsWith("get")) && (!m.getName()
						.startsWith("is")))
						|| (m.getParameterTypes().length != 0))
					continue;
				int l = m.getName().startsWith("get") ? 3 : 2;
				obj = m.invoke(data, new Object[0]);
				if ((!snapped) || (obj != null)) {
					isstring = obj instanceof String;
					if ((!isstring)
							&& (snapped)
							&& ((((obj instanceof Number)) && (((Number) obj)
									.intValue() == 0)) || (((obj instanceof Boolean)) && (!((Boolean) obj)
									.booleanValue()))))
						continue;
					str = isstring ? "\"" + obj + "\"" : String.valueOf(obj);
					if (flag)
						sb.append(", ");
					sb.append(m.getName().substring(l).toLowerCase())
							.append("=").append(str);
					flag = true;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		sb.append("]");
		return sb.toString();
	}

	public static String subString(String t, int size) {
		if (t == null)
			return null;
		int hansize = size * 3 / 2;
		int len = hansize;
		if (t.length() > size) {
			int p = 0;
			for (int i = 0; (i < hansize) && (i < t.length()); i++) {
				if (t.charAt(i) <= '')
					continue;
				p++;
			}
			len -= p * 2 / 3;
			if (len < size)
				len = size;
			if (t.length() <= len)
				return t;
			return t.substring(0, len) + "...";
		}
		return t;
	}

	public static String createSequence() {
		return UUID.randomUUID().toString();
	}

	public static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		byte[] arrayOfByte = bytes;
		int j = bytes.length;
		for (int i = 0; i < j; i++) {
			byte b = arrayOfByte[i];
			sb.append(hex[(b >> 4 & 0xF)]).append(hex[(b >> 0 & 0xF)]);
		}
		return sb.toString();
	}

	public static byte[] hexStringToBytes(String inString) {
		int fromLen = inString.length();
		int toLen = (fromLen + 1) / 2;
		byte[] b = new byte[toLen];
		for (int i = 0; i < toLen; i++) {
			b[i] = (byte) hexPairToInt(inString.substring(i * 2, (i + 1) * 2));
		}
		return b;
	}

	public static String ArrayToSortString(List<String> totalStringList) {
		String str = "";

		if ((totalStringList != null) && (totalStringList.size() > 0)) {
			String[] strs = (String[]) totalStringList
					.toArray(new String[totalStringList.size()]);
			Arrays.sort(strs);
			for (String s : strs) {
				str = str + s;
			}
		}
		return str;
	}

	public static byte[] convertStringCid2Bytes(String sCid) {
		byte[] cid = new byte[20];
		for (int i = 0; i < cid.length; i++) {
			cid[i] = (byte) Integer.parseInt(sCid.substring(i * 2, i * 2 + 2),
					16);
		}
		return cid;
	}

	public static int search(String no, String[] noes) {
		for (int i = 0; i < noes.length; i++) {
			if (no.equals(noes[i]))
				return i;
		}
		return -1;
	}

	private static int hexPairToInt(String inString) {
		String digits = "0123456789abcdefghijklmnopqrstuvwxyz";
		String s = inString.toLowerCase();
		int n = 0;
		int thisDigit = 0;
		int sLen = s.length();
		if (sLen > 2)
			sLen = 2;
		for (int i = 0; i < sLen; i++) {
			thisDigit = digits.indexOf(s.substring(i, i + 1));
			if (thisDigit < 0)
				throw new NumberFormatException();
			if (i == 0)
				thisDigit *= 16;
			n += thisDigit;
		}
		return n;
	}

	public static String read(InputStream in, String charset)
			throws IOException {
		int pos = -1;
		byte[] buf = new byte[8192];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while ((pos = in.read(buf)) != -1) {
			out.write(buf, 0, pos);
		}
		return charset == null ? new String(out.toByteArray()) : new String(
				out.toByteArray(), charset);
	}

	public static String read(InputStream in) throws IOException {
		return read(in, null);
	}

	public static String encodeURL(String str) {
		try {
			return URLEncoder.encode(str, "utf-8").replaceAll("\\+", "%20");
		} catch (Exception ex) {
		}
		return "";
	}

	public static String decodeURL(String str) {
		try {
			return URLDecoder.decode(str, "utf-8");
		} catch (Exception ex) {
		}
		return "";
	}

	public static boolean isEmpty(String str) {
		return (str == null) || (str.trim().isEmpty());
	}

	public static boolean isNotEmpty(String str) {
		return (str != null) && (!str.trim().isEmpty());
	}

	public static final String escapeSql(String str) {
		if (str == null) {
			return "";
		}
		for (String[] ss : sqlhandles) {
			str = str.replaceAll(ss[0], ss[1]);
		}
		return str;
	}

	public static final String escapeSql(Object obj) {
		if (obj == null) {
			return "";
		}
		return escapeSql(obj.toString());
	}

	public static int safeToInt(Object o) {
		int rs = 0;
		try {
			rs = Integer.parseInt(o.toString());
		} catch (Exception ex) {
			rs = 0;
		}
		return rs;
	}

	public static int safeToShort(Object o) {
		short rs = 0;
		try {
			rs = Short.parseShort(o.toString());
		} catch (Exception ex) {
			rs = 0;
		}
		return rs;
	}

	public static long safeToLong(Object o) {
		long rs = 0L;
		try {
			rs = Long.parseLong(o.toString());
		} catch (Exception ex) {
			rs = 0L;
		}
		return rs;
	}

	public static double safeToDouble(Object o) {
		double rs = 0.0D;
		try {
			rs = Double.parseDouble(o.toString());
		} catch (Exception ex) {
			rs = 0.0D;
		}
		return rs;
	}

	public static double tryParseDouble(Object fieldValue) {
		try {
			double rs = ((Double) fieldValue).doubleValue();
			return rs;
		} catch (Exception ex) {
			try {
				return Double.parseDouble(fieldValue.toString());
			} catch (Exception exx) {
			}
		}
		return 0.0D;
	}


	public static boolean isNotContains(String one, String[] arrays) {
		if (arrays == null) {
			return true;
		}
		for (String str : arrays) {
			if (one.equalsIgnoreCase(str))
				return false;
		}
		return true;
	}

	public static String capitalize(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	protected static String toString(List<String> list) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String s : list) {
			if (first)
				first = false;
			else {
				sb.append(",");
			}
			sb.append("'").append(s).append("'");
		}
		return sb.toString();
	}

	protected static String toString(String[] arrays) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		String[] arrayOfString = arrays;
		int j = arrays.length;
		for (int i = 0; i < j; i++) {
			String s = arrayOfString[i];
			if (first)
				first = false;
			else {
				sb.append(",");
			}
			sb.append("'").append(s).append("'");
		}
		if (sb.length() < 1)
			return "'null_'";
		return sb.toString();
	}

	protected static String toString(long[] arrays) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		long[] arrayOfLong = arrays;
		int j = arrays.length;
		for (int i = 0; i < j; i++) {
			long s = arrayOfLong[i];
			if (first)
				first = false;
			else {
				sb.append(",");
			}
			sb.append(s);
		}
		return sb.toString();
	}
}