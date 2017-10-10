package com.hc9.common.util;


public final class DataTools {
	
	public static String bytes2Hex(byte[] inbuf) {
        int i;
        String byteStr;
        StringBuffer strBuf = new StringBuffer();
        for (i = 0; i < inbuf.length; i++)
        {
            byteStr = Integer.toHexString(inbuf[i] & 0x00ff);
            if (byteStr.length() != 2)
            {
                strBuf.append('0').append(byteStr);
            }
            else
            {
                strBuf.append(byteStr);
            }
        }
        return new String(strBuf);
    }

	/**
	 * char数组转化为byte数组,高字节在前
	 * 
	 * @param chs
	 *            待转化的char数组
	 * @param buf
	 *            待存储的byte数组
	 * @param offset
	 *            buf的偏移值
	 */
	public static void chars2Bytes(char[] chs, byte[] buf, int offset)
	{
		for (int i = 0; i < chs.length; i++)
		{
			char c = chs[i];
			buf[offset + 2 * i] = (byte) c;
			c >>= 8;
			buf[offset + 2 * i + 1] = (byte) c;
		}
	}

	/**
	 * char转化为byte型,高字节在前
	 * @param c
	 *            待转化的的字符
	 * @param buf
	 *            待存储的byte数组
	 * @param offset
	 *            buf的偏移值
	 */
	public static void char2Bytes(char c, byte[] buf, int offset)
	{
		char ch = c;
		buf[offset] = (byte) ch;
		ch >>= 8;
		buf[offset + 1] = (byte) ch;
	}

	/**
	 * byte型转化为char,高字节在前
	 *  
	 * @param buf
	 *            待转化的的byte数组(长度至少必须为2)
	 * @param offset
	 *            buf的偏移值
	 * 
	 * @return 转化后的char字符
	 */
	public static char bytes2Char(byte[] buf, int offset)
	{
		char ret = 0;
		ret |= buf[offset + 1];
		ret <<= 8;
		byte b = buf[offset];
		ret |= b < 0 ? b + 256 : b;
		return ret;
	}

	/**
	 * short转化为byte数组,高字节在前
	 *  
	 * @param i
	 *            待转化的的short型变量
	 * @param offset
	 *            buf的偏移值
	 */
	public static byte[] short2Bytes(short i, int offset)
	{
		byte[] buf = new byte[2];
		buf[offset + 1] = (byte) i;
		i >>= 8;
		buf[offset] = (byte) i;
		return buf;
	}

	/**
	 * byte数组转化为shor型,高字节在前
	 * 
	 * 
	 * @param buf
	 *            待转化的的byte数组(长度至少必须为2)
	 * @param offset
	 *            buf的偏移值
	 * 
	 * @return 转化后的short值
	 */
	public static short bytes2Short(byte[] buf, int offset)
	{
		short ret = 0;
		ret |= buf[offset];
		ret <<= 8;
		ret |= buf[offset + 1] < 0 ? buf[offset + 1] + 256 : buf[offset + 1];
		return ret;
	}

	/**
	 * int转化为byte数组,高字节在前
	 * 
	 * 
	 * @param i
	 *            待转化的的int型变量
	 * 
	 * @param buf
	 *            待存储的byte数组
	 * @param offset
	 *            buf的偏移值
	 */
	public static byte[] int2Bytes(int i, int offset)
	{
		byte[] buf = new byte[4];
		buf[offset + 3] = (byte) i;
		i >>= 8;
		buf[offset + 2] = (byte) i;
		i >>= 8;
		buf[offset + 1] = (byte) i;
		i >>= 8;
		buf[offset] = (byte) i;
		return buf;
	}
	
	/**
	 * int转化为byte数组,数组长度为2(取低位)
	 * @param i
	 * @return
	 */
	public static byte[] int2Bytes(int i)
	{
		byte[] buf = new byte[2];
		buf[0] = (byte)((i >> 8) & 0xFF);
		buf[1] = (byte)(i & 0xFF);
		return buf;
	}

	/**
	 * byte数组转化为int,高字节在前
	 * 
	 * 
	 * @param buf
	 *            待转化的的byte数组(长度至少必须为4)
	 * @param offset
	 *            buf的偏移值
	 * 
	 * @return 转化后的int值
	 */
	public static int bytes2Int(byte[] buf, int offset)
	{
		int ret = 0;
		ret |= buf[offset];
		ret <<= 8;
		ret |= buf[offset + 1] < 0 ? buf[offset + 1] + 256 : buf[offset + 1];
		ret <<= 8;
		ret |= buf[offset + 2] < 0 ? buf[offset + 2] + 256 : buf[offset + 2];
		ret <<= 8;
		ret |= buf[offset + 3] < 0 ? buf[offset + 3] + 256 : buf[offset + 3];
		return ret;
	}
	
	/**
	 * 将2字节byte数组转为int
	 * @param buf	--2字节长度
	 * @return
	 */
	public static int bytes2Int(byte[] buf)
	{
		int tmp = 0,n = 0;
		for(int i=0;i<2;i++){
			n<<=8;
			tmp = buf[i]&0xff;
			n |= tmp;
		}
		return n;
	}
	
	/**
	 * 将3字节byte数组转为int
	 * @param buf	--3字节长度
	 * @return
	 */
	public static int bytes2Int3(byte[] buf)
	{
		int tmp = 0,n = 0;
		for(int i=0;i<3;i++){
			n<<=8;
			tmp = buf[i]&0xff;
			n |= tmp;
		}
		return n;
	}
	
	/**
	 * byte[]转换为int  低字节在前
	 * @param buf
	 * @return
	 */
	public static int lBytesToInt(byte[] buf) 
	{
		int s = 0;
		for(int i=0;i<3;i++)
		{
			if (buf[3-i] >= 0) {
				s = s + buf[3-i];
			}else{
				s = s + 256 + buf[3-i];
			}
			s = s * 256;
		}
		if (buf[0] >= 0) {  
			s = s + buf[0];
		} else {  
			s = s + 256 + buf[0];  
		}  
		return s;
		  
	}

	/**
	 * long转化为byte数组,高字节在前
	 * 
	 * 
	 * @param i
	 *            待转化的的long型变量
	 * 
	 * @param buf
	 *            待存储的byte数组
	 * @param offset
	 *            buf的偏移值
	 */
	public static void long2Bytes(long i, byte[] buf, int offset)
	{
		buf[offset + 7] = (byte) i;
		i >>= 8;
		buf[offset + 6] = (byte) i;
		i >>= 8;
		buf[offset + 5] = (byte) i;
		i >>= 8;
		buf[offset + 4] = (byte) i;
		i >>= 8;
		buf[offset + 3] = (byte) i;
		i >>= 8;
		buf[offset + 2] = (byte) i;
		i >>= 8;
		buf[offset + 1] = (byte) i;
		i >>= 8;
		buf[offset] = (byte) i;
	}

	/**
	 * byte数组转化为long,高字节在前
	 * 
	 * 
	 * @param buf
	 *            待转化的的byte数组(长度至少必须为8)
	 * @param offset
	 *            buf的偏移值
	 * 
	 * @return 转化后的long值
	 */
	public static long bytes2Long(byte[] buf, int offset)
	{
		long ret = 0;
		ret |= buf[offset];
		ret <<= 8;
		ret |= buf[offset + 1] < 0 ? buf[offset + 1] + 256 : buf[offset + 1];
		ret <<= 8;
		ret |= buf[offset + 2] < 0 ? buf[offset + 2] + 256 : buf[offset + 2];
		ret <<= 8;
		ret |= buf[offset + 3] < 0 ? buf[offset + 3] + 256 : buf[offset + 3];
		ret <<= 8;
		ret |= buf[offset + 4] < 0 ? buf[offset + 4] + 256 : buf[offset + 4];
		ret <<= 8;
		ret |= buf[offset + 5] < 0 ? buf[offset + 5] + 256 : buf[offset + 5];
		ret <<= 8;
		ret |= buf[offset + 6] < 0 ? buf[offset + 6] + 256 : buf[offset + 6];
		ret <<= 8;
		ret |= buf[offset + 7] < 0 ? buf[offset + 7] + 256 : buf[offset + 7];
		return ret;
	}

	/**
	 * byte数组转化为short,低字节在前
	 * 
	 * 
	 * @param buf
	 *            待转化的的byte数组(长度至少必须为2)
	 * @param offset
	 *            buf的偏移值
	 * 
	 * @return 转化后的short值
	 */
	public static short sBytes2Short(byte[] buf, int offset)
	{
		short ret = 0;
		ret |= buf[offset + 1];
		ret <<= 8;
		ret |= buf[offset] < 0 ? buf[offset] + 256 : buf[offset];
		return ret;
	}

	/**
	 * byte数组转化为int,低字节在前
	 * 
	 * 
	 * @param buf
	 *            待转化的的byte数组(长度至少必须为4)
	 * @param offset
	 *            buf的偏移值
	 * 
	 * @return 转化后的int值
	 */
	public static int sBytes2Int(byte[] buf, int offset)
	{
		int ret = 0;
		ret |= buf[offset + 3];
		ret <<= 8;
		ret |= buf[offset + 2] < 0 ? buf[offset + 2] + 256 : buf[offset + 2];
		ret <<= 8;
		ret |= buf[offset + 1] < 0 ? buf[offset + 1] + 256 : buf[offset + 1];
		ret <<= 8;
		ret |= buf[offset] < 0 ? buf[offset] + 256 : buf[offset];
		return ret;
	}

	/**
	 * byte数组转化为float,高字节在前
	 * 
	 * 
	 * @param buf
	 *            待转化的的byte数组
	 * @param offset
	 *            buf的偏移值
	 * 
	 * @return 转化后的float值
	 */
	public static float bytes2Float(byte[] buf, int offset)
	{
		return Float.intBitsToFloat(bytes2Int(buf, offset));
	}

	/**
	 * 把十六进制的字符串转化为二进制
	 * 
	 * <p>
	 * 字符串中存放的为AB089D类似这种数据.
	 * 注意:是否0x开头及大小写均可,字符串长度必须为偶数,否则抛出IllegalArgumentException异常
	 * </p>
	 * 
	 * @param hex
	 *            待转化的十六进制的字符串
	 * @return 转化后的byte数组
	 */
	public static byte[] hex2Binary(String hex)
	{
		if (hex == null || hex.length() < 1)
			return null;

		if (hex.startsWith("0x") || hex.startsWith("0X"))
			hex = hex.substring(2);

		int len = hex.length();
		if (len % 2 != 0)
			throw new IllegalArgumentException("待转化的十六进制字符串长度应该为偶数");

		byte[] bys = new byte[len / 2];
		for (int i = 0; i < len; i += 2)
		{
			String tmp = hex.substring(i, i + 2);
			bys[i / 2] = (byte) Integer.parseInt(tmp, 16);
		}
		return bys;
	}

	/**
	 * 把二进制数据转化为十六进制的字符串
	 * 
	 * <p>
	 * 字符串中存放的为AB089D类似这种数据,但是没有0x开头,一个byte对应的字符串长度为2
	 * </p>
	 * 
	 * @param bys
	 *            待转化的二六进制数据
	 * @return 转化后的十六进制字符串,如果bys为空或者长度为0则返回null
	 */
	public static String binary2Hex(byte[] bys)
	{
		if (bys == null || bys.length < 1)
			return null;

		StringBuffer sb = new StringBuffer(100);

		for (byte b : bys)
		{
			if (b >= 16)
				sb.append(Integer.toHexString(b));
			else if (b >= 0)
				sb.append("0" + Integer.toHexString(b));
			else
				sb.append(Integer.toHexString(b).substring(6, 8));
			//sb.append(" ");
		}

		return sb.toString();
	}
	
	public static String binary2Hex2(byte[] bys)
	{
		if (bys == null || bys.length < 1)
			return null;

		StringBuffer sb = new StringBuffer(100);

		for (byte b : bys)
		{
			if (b >= 16)
				sb.append(Integer.toHexString(b));
			else if (b >= 0)
				sb.append("0" + Integer.toHexString(b));
			else
				sb.append(Integer.toHexString(b).substring(6, 8));
			sb.append(" ");
		}

		return sb.toString();
	}
	
	/**
	 * 二进制数据转成字符串(默认编码)
	 * @param bys
	 * @return
	 */
	public static String bytes2String(byte[] bys)
	{
		return new String(bys);
	}
	
	/**
	 * BCD转换字符串
	 * @param bytes
	 * @return
	 */
	public static String bcd2Str(byte[] bytes)
	{
		char temp[] = new char[bytes.length * 2], val;  
		  
        for (int i = 0; i < bytes.length; i++) {  
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);  
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');  
  
            val = (char) (bytes[i] & 0x0f);  
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');  
        }  
        return new String(temp); 
	}
	
	/**
	 * int转BCD
	 * @param wSrc
	 * @return
	 */
	public static byte[] int2Bcd(int wSrc)
	{
		byte[] b = new byte[2];
		b[0] = (byte) ((wSrc / 100) / 10 * 0x10 + (wSrc / 100) % 10);
		b[1] = (byte) ((wSrc % 100) / 10 * 0x10 + (wSrc % 100) % 10);
		return b;
	}
	
	public static byte[] u32ToBcd(int wSrc,int len)
	{
		byte[] bcd = new byte[len];
		int num = wSrc;
		for(int i=len;i>0;i--)
		{
			bcd[i-1] = (byte) ((((num%100) / 10) << 4) | ((num%100) % 10));
			num /= 100;
		}
		return bcd;
	}
	
	private static byte asc_to_bcd(byte asc) 
	{  
		byte bcd;  

		if ((asc >= '0') && (asc <= '9'))  
			bcd = (byte) (asc - '0');  
		else if ((asc >= 'A') && (asc <= 'F'))  
			bcd = (byte) (asc - 'A' + 10);  
		else if ((asc >= 'a') && (asc <= 'f'))  
			bcd = (byte) (asc - 'a' + 10);  
		else  
			bcd = (byte) (asc - 48);  
        return bcd;  
    }
	
	public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) 
	{  
        byte[] bcd = new byte[asc_len / 2];  
        int j = 0;  
        for (int i = 0; i < (asc_len + 1) / 2; i++) {  
            bcd[i] = asc_to_bcd(ascii[j++]);  
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));  
        }  
        return bcd;  
    }
	
	/**
	 * 计算冗余检验值
	 * @param bytes
	 * @param len
	 * @return
	 */
	public static int countXOR(byte[] bytes,int len)
	{
		int tmp = 0x00;
		for(int i=0;i!=len;i++)
		{
			tmp = tmp ^ toInt(bytes[i]);
		}
		return tmp;
	}
	
	/**
	 * 计算数组中的数据累加和
	 * @param bytes
	 * @return
	 */
	public static int lcr(byte[] bytes)
	{
		if(null == bytes)
			return 0;
		int sum = 0;
		for(byte b:bytes)
		{
			sum += toInt(b);
		}
		return sum;
	}
	
	public static int toInt(byte b) {  
        return (int) b & 0xFF;  
    }
	
	public static String byte2String(byte b)
	{
		byte[] bs = new byte[1];
		bs[0] = b;
		return new String(bs);
	}
	
	/**
	 * 将数据按指定长度输出 不足右补0x20
	 * @param bytes
	 * @param len
	 * @return
	 */
	public static byte[] specifiedLengthBytes(byte[] bytes,int len)
	{
		if(bytes.length == len)
			return bytes;
		
		byte[] data = new byte[len];
		if(bytes.length > len)
		{
			System.arraycopy(bytes, 0, data, 0, len);
		}else{
			System.arraycopy(bytes, 0, data, 0, bytes.length);
			byte[] b = initBytes(len - bytes.length,(byte)0x20);
			System.arraycopy(b, 0, data, bytes.length, b.length);
		}
		return data;
	}
	
	/**
	 * 用指定数据初始化bytes
	 * @param len
	 * @param value
	 * @return
	 */
	public static byte[] initBytes(int len,byte value)
	{
		byte[] data = new byte[len];
		for(int i=0;i<len;i++)
		{
			data[i] = value;
		}
		return data;
	}
	
	/***
	 * 将int转换成高低两个字节
	 * 
	 * @param src
	 *            最大值 127*256+127
	 * @return
	 */
	public static byte[] intToHLbyte(int src) {
		byte[] result = new byte[2];
		result[0] = (byte) (src / 256);
		result[1] = (byte) (src % 256);
		return result;
	}

	/***
	 * 将int转换成低高两个字节
	 * 
	 * @param src
	 *            最大值 127*256+127
	 * @return
	 */
	public static byte[] intToLHbyte(int src) {
		byte[] result = new byte[2];
		result[0] = (byte) (src % 256);
		result[1] = (byte) (src / 256);
		return result;
	}
	
	/**
	 * 处理数组合并
	 * @param src
	 * @param dest
	 * @return
	 */
	public static byte[] bytesMerge(byte[] src,byte[] dest)
	{
		byte[] newArr = new byte[src.length + dest.length];
		System.arraycopy(src, 0, newArr, 0, src.length);
		System.arraycopy(dest, 0, newArr, src.length, dest.length);
		//System.out.println("aaaa = "+DataTools.binary2Hex2(newArr));
		return newArr;
	}
	
	public static void main(String[] arsg)
	{
		/*
		byte[] a = new byte[4];
		int2Bytes(241,a,0);
		if(null != a)
		{
			for(int i=0;i<a.length;i++)
			{
				System.out.println(a[i]);
			}
		}
		*/
		/*
		byte[] a = new byte[4];
		a[3] = -15;
		System.out.println(bytes2Int(a,0));
		*/
		
		/*
		ByteBuffer buffer = ByteBuffer.allocate(86);
		buffer.put((byte)2);
		buffer.position(3);
		buffer.put((byte)0xf1);
		
		byte[] bytes = buffer.array();
		for(int i=0;i<bytes.length;i++)
		{
			System.out.print(bytes[i]+",");
		}
		*/
		
		byte[] a = {1,2,3,4};
		byte[] b = {0,5,4,4,3};
		
		byte[] c = bytesMerge(a,b);
		for(byte b1:c)
		{
			System.out.println(b1+" ");
		}
	}
}
