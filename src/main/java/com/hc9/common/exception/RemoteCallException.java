package com.hc9.common.exception;

public class RemoteCallException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	// 异常码:表示何种异常，面向开发人员。
	private int nCode;

	// 错误码：对照错误编码表，可找到具体错误原因和解决方案，面向产品用户、技术合作伙伴和技术支持人员。
	private int errorCode = -999;

	public static final int RCE_LOGOUT = -1;// 未登录或登录超时
	public static final int RCE_UNKNOWN = -2;// 未知错误
	public static final int RCE_NOKEY = -3;// no secure key to develop request
	public static final int RCE_INVALID = -4;// invalid request xml
	public static final int RCE_DATABASE = -5;// database exception
	public static final int RCE_PARAMETER = -6;// parameter exception
	public static final int RCE_ENCRYPT = -7;// 加密数据异常
	public static final int RCE_DECRYPT = -8;// 加密数据异常
	public static final int RCE_NOTIFY = -9;// email Exception
	public static final int RCE_DEPRECATED = -10;// ever used but not support
	// now
	public static final int RCE_NOSESSION = -11;// no session for this call
	public static final int RCE_FAILED = -12;// failed
	public static final int RCE_OUTOFMEMORY = -13;// 内存溢出
	public static final int RCE_IO = -14;// IO 错误
	public static final int RCE_UNAUTHORIZED = -15;// the requested operation is
	// out of the authority
	public static final int RCE_NOMETHOD = -16;// the requested method is not
	// found
	public static final int RCE_ACCOUNT = -17;// the user account exception

	// public static final int RCE_REVOKE=-18;//the sign already revoke
	// exception

	public int getNCode() {
		return nCode;
	}

	public void setNCode(int code) {
		nCode = code;
	}

	public RemoteCallException(int code) {
		super("");
		this.nCode = code;
	}

	/**
	 * 
	 * @param code
	 *            异常码
	 * @param message
	 *            异常信息
	 */
	public RemoteCallException(int code, String message) {
		super(message);
		this.nCode = code;
	}

	/**
	 * 
	 * @param code
	 *            异常码
	 * @param cause
	 *            异常
	 */
	public RemoteCallException(int code, Exception cause) {
		super(cause);
		this.nCode = code;
	}

	/**
	 * 
	 * @param code
	 *            异常码
	 * @param message
	 *            异常信息
	 * @param errCode
	 *            错误码
	 */
	public RemoteCallException(int code, String message, int errCode) {
		super(message);
		this.nCode = code;
		this.errorCode = errCode;
	}

	public String getMessage() {
		String str = super.getMessage();
		if (str != "")
			return str;
		switch (this.nCode) {
		case RCE_LOGOUT:
			return "尚未登录或者会话超时。";
		case RCE_NOKEY:
			return "没有找到可以解密的密钥。";
		case RCE_INVALID:
			return "不合理的请求。";
		case RCE_DATABASE:
			return "数据库操作出现问题。";
		case RCE_PARAMETER:
			return "不合理的调用参数。";
		case RCE_ENCRYPT:
			return "加密应答出现问题。";
		case RCE_DECRYPT:
			return "解密请求出现问题。";
		case RCE_NOTIFY:
			return "通知出现问题。";
		case RCE_DEPRECATED:
			return "不再支持这个调用。";
		case RCE_NOSESSION:
			return "没有会话信息。";
		case RCE_FAILED:
			return "调用结果为失败。";
		case RemoteCallException.RCE_OUTOFMEMORY:
			return "内存不足，无法完成相应的操作。";
		case RemoteCallException.RCE_IO:
			return "IO错误发生。";
		case RemoteCallException.RCE_UNAUTHORIZED:
			return "操作超出权限，无法完成。";
		case RemoteCallException.RCE_NOMETHOD:
			return "没有找到这个函数供调用。";
		case RemoteCallException.RCE_ACCOUNT:
			return "用户无账户或账户出现问题。";
		default:
			return "未知异常";
		}
	}

	public String toString() {
		return super.toString() + " with nCode code: " + this.nCode
				+ " , and error code: " + this.errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

}
