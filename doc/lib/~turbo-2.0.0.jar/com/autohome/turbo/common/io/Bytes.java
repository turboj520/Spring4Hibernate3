// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Bytes.java

package com.autohome.turbo.common.io;

import com.autohome.turbo.common.utils.IOUtils;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

// Referenced classes of package com.autohome.turbo.common.io:
//			UnsafeByteArrayOutputStream, UnsafeByteArrayInputStream

public class Bytes
{

	private static final String C64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
	private static final char BASE16[] = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
		'a', 'b', 'c', 'd', 'e', 'f'
	};
	private static final char BASE64[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
	private static final int MASK4 = 15;
	private static final int MASK6 = 63;
	private static final int MASK8 = 255;
	private static final Map DECODE_TABLE_MAP = new ConcurrentHashMap();
	private static ThreadLocal MD = new ThreadLocal();

	public static byte[] copyOf(byte src[], int length)
	{
		byte dest[] = new byte[length];
		System.arraycopy(src, 0, dest, 0, Math.min(src.length, length));
		return dest;
	}

	public static byte[] short2bytes(short v)
	{
		byte ret[] = {
			0, 0
		};
		short2bytes(v, ret);
		return ret;
	}

	public static void short2bytes(short v, byte b[])
	{
		short2bytes(v, b, 0);
	}

	public static void short2bytes(short v, byte b[], int off)
	{
		b[off + 1] = (byte)v;
		b[off + 0] = (byte)(v >>> 8);
	}

	public static byte[] int2bytes(int v)
	{
		byte ret[] = {
			0, 0, 0, 0
		};
		int2bytes(v, ret);
		return ret;
	}

	public static void int2bytes(int v, byte b[])
	{
		int2bytes(v, b, 0);
	}

	public static void int2bytes(int v, byte b[], int off)
	{
		b[off + 3] = (byte)v;
		b[off + 2] = (byte)(v >>> 8);
		b[off + 1] = (byte)(v >>> 16);
		b[off + 0] = (byte)(v >>> 24);
	}

	public static byte[] float2bytes(float v)
	{
		byte ret[] = {
			0, 0, 0, 0
		};
		float2bytes(v, ret);
		return ret;
	}

	public static void float2bytes(float v, byte b[])
	{
		float2bytes(v, b, 0);
	}

	public static void float2bytes(float v, byte b[], int off)
	{
		int i = Float.floatToIntBits(v);
		b[off + 3] = (byte)i;
		b[off + 2] = (byte)(i >>> 8);
		b[off + 1] = (byte)(i >>> 16);
		b[off + 0] = (byte)(i >>> 24);
	}

	public static byte[] long2bytes(long v)
	{
		byte ret[] = {
			0, 0, 0, 0, 0, 0, 0, 0
		};
		long2bytes(v, ret);
		return ret;
	}

	public static void long2bytes(long v, byte b[])
	{
		long2bytes(v, b, 0);
	}

	public static void long2bytes(long v, byte b[], int off)
	{
		b[off + 7] = (byte)(int)v;
		b[off + 6] = (byte)(int)(v >>> 8);
		b[off + 5] = (byte)(int)(v >>> 16);
		b[off + 4] = (byte)(int)(v >>> 24);
		b[off + 3] = (byte)(int)(v >>> 32);
		b[off + 2] = (byte)(int)(v >>> 40);
		b[off + 1] = (byte)(int)(v >>> 48);
		b[off + 0] = (byte)(int)(v >>> 56);
	}

	public static byte[] double2bytes(double v)
	{
		byte ret[] = {
			0, 0, 0, 0, 0, 0, 0, 0
		};
		double2bytes(v, ret);
		return ret;
	}

	public static void double2bytes(double v, byte b[])
	{
		double2bytes(v, b, 0);
	}

	public static void double2bytes(double v, byte b[], int off)
	{
		long j = Double.doubleToLongBits(v);
		b[off + 7] = (byte)(int)j;
		b[off + 6] = (byte)(int)(j >>> 8);
		b[off + 5] = (byte)(int)(j >>> 16);
		b[off + 4] = (byte)(int)(j >>> 24);
		b[off + 3] = (byte)(int)(j >>> 32);
		b[off + 2] = (byte)(int)(j >>> 40);
		b[off + 1] = (byte)(int)(j >>> 48);
		b[off + 0] = (byte)(int)(j >>> 56);
	}

	public static short bytes2short(byte b[])
	{
		return bytes2short(b, 0);
	}

	public static short bytes2short(byte b[], int off)
	{
		return (short)(((b[off + 1] & 0xff) << 0) + (b[off + 0] << 8));
	}

	public static int bytes2int(byte b[])
	{
		return bytes2int(b, 0);
	}

	public static int bytes2int(byte b[], int off)
	{
		return ((b[off + 3] & 0xff) << 0) + ((b[off + 2] & 0xff) << 8) + ((b[off + 1] & 0xff) << 16) + (b[off + 0] << 24);
	}

	public static float bytes2float(byte b[])
	{
		return bytes2float(b, 0);
	}

	public static float bytes2float(byte b[], int off)
	{
		int i = ((b[off + 3] & 0xff) << 0) + ((b[off + 2] & 0xff) << 8) + ((b[off + 1] & 0xff) << 16) + (b[off + 0] << 24);
		return Float.intBitsToFloat(i);
	}

	public static long bytes2long(byte b[])
	{
		return bytes2long(b, 0);
	}

	public static long bytes2long(byte b[], int off)
	{
		return (((long)b[off + 7] & 255L) << 0) + (((long)b[off + 6] & 255L) << 8) + (((long)b[off + 5] & 255L) << 16) + (((long)b[off + 4] & 255L) << 24) + (((long)b[off + 3] & 255L) << 32) + (((long)b[off + 2] & 255L) << 40) + (((long)b[off + 1] & 255L) << 48) + ((long)b[off + 0] << 56);
	}

	public static double bytes2double(byte b[])
	{
		return bytes2double(b, 0);
	}

	public static double bytes2double(byte b[], int off)
	{
		long j = (((long)b[off + 7] & 255L) << 0) + (((long)b[off + 6] & 255L) << 8) + (((long)b[off + 5] & 255L) << 16) + (((long)b[off + 4] & 255L) << 24) + (((long)b[off + 3] & 255L) << 32) + (((long)b[off + 2] & 255L) << 40) + (((long)b[off + 1] & 255L) << 48) + ((long)b[off + 0] << 56);
		return Double.longBitsToDouble(j);
	}

	public static String bytes2hex(byte bs[])
	{
		return bytes2hex(bs, 0, bs.length);
	}

	public static String bytes2hex(byte bs[], int off, int len)
	{
		if (off < 0)
			throw new IndexOutOfBoundsException((new StringBuilder()).append("bytes2hex: offset < 0, offset is ").append(off).toString());
		if (len < 0)
			throw new IndexOutOfBoundsException((new StringBuilder()).append("bytes2hex: length < 0, length is ").append(len).toString());
		if (off + len > bs.length)
			throw new IndexOutOfBoundsException("bytes2hex: offset + length > array length.");
		int r = off;
		int w = 0;
		char cs[] = new char[len * 2];
		for (int i = 0; i < len; i++)
		{
			byte b = bs[r++];
			cs[w++] = BASE16[b >> 4 & 0xf];
			cs[w++] = BASE16[b & 0xf];
		}

		return new String(cs);
	}

	public static byte[] hex2bytes(String str)
	{
		return hex2bytes(str, 0, str.length());
	}

	public static byte[] hex2bytes(String str, int off, int len)
	{
		if ((len & 1) == 1)
			throw new IllegalArgumentException("hex2bytes: ( len & 1 ) == 1.");
		if (off < 0)
			throw new IndexOutOfBoundsException((new StringBuilder()).append("hex2bytes: offset < 0, offset is ").append(off).toString());
		if (len < 0)
			throw new IndexOutOfBoundsException((new StringBuilder()).append("hex2bytes: length < 0, length is ").append(len).toString());
		if (off + len > str.length())
			throw new IndexOutOfBoundsException("hex2bytes: offset + length > array length.");
		int num = len / 2;
		int r = off;
		int w = 0;
		byte b[] = new byte[num];
		for (int i = 0; i < num; i++)
			b[w++] = (byte)(hex(str.charAt(r++)) << 4 | hex(str.charAt(r++)));

		return b;
	}

	public static String bytes2base64(byte b[])
	{
		return bytes2base64(b, 0, b.length, BASE64);
	}

	public static String bytes2base64(byte b[], int offset, int length)
	{
		return bytes2base64(b, offset, length, BASE64);
	}

	public static String bytes2base64(byte b[], String code)
	{
		return bytes2base64(b, 0, b.length, code);
	}

	public static String bytes2base64(byte b[], int offset, int length, String code)
	{
		if (code.length() < 64)
			throw new IllegalArgumentException("Base64 code length < 64.");
		else
			return bytes2base64(b, offset, length, code.toCharArray());
	}

	public static String bytes2base64(byte b[], char code[])
	{
		return bytes2base64(b, 0, b.length, code);
	}

	public static String bytes2base64(byte bs[], int off, int len, char code[])
	{
		if (off < 0)
			throw new IndexOutOfBoundsException((new StringBuilder()).append("bytes2base64: offset < 0, offset is ").append(off).toString());
		if (len < 0)
			throw new IndexOutOfBoundsException((new StringBuilder()).append("bytes2base64: length < 0, length is ").append(len).toString());
		if (off + len > bs.length)
			throw new IndexOutOfBoundsException("bytes2base64: offset + length > array length.");
		if (code.length < 64)
			throw new IllegalArgumentException("Base64 code length < 64.");
		boolean pad = code.length > 64;
		int num = len / 3;
		int rem = len % 3;
		int r = off;
		int w = 0;
		char cs[] = new char[num * 4 + (rem != 0 ? pad ? 4 : rem + 1 : 0)];
		for (int i = 0; i < num; i++)
		{
			int b1 = bs[r++] & 0xff;
			int b2 = bs[r++] & 0xff;
			int b3 = bs[r++] & 0xff;
			cs[w++] = code[b1 >> 2];
			cs[w++] = code[b1 << 4 & 0x3f | b2 >> 4];
			cs[w++] = code[b2 << 2 & 0x3f | b3 >> 6];
			cs[w++] = code[b3 & 0x3f];
		}

		if (rem == 1)
		{
			int b1 = bs[r++] & 0xff;
			cs[w++] = code[b1 >> 2];
			cs[w++] = code[b1 << 4 & 0x3f];
			if (pad)
			{
				cs[w++] = code[64];
				cs[w++] = code[64];
			}
		} else
		if (rem == 2)
		{
			int b1 = bs[r++] & 0xff;
			int b2 = bs[r++] & 0xff;
			cs[w++] = code[b1 >> 2];
			cs[w++] = code[b1 << 4 & 0x3f | b2 >> 4];
			cs[w++] = code[b2 << 2 & 0x3f];
			if (pad)
				cs[w++] = code[64];
		}
		return new String(cs);
	}

	public static byte[] base642bytes(String str)
	{
		return base642bytes(str, 0, str.length());
	}

	public static byte[] base642bytes(String str, int offset, int length)
	{
		return base642bytes(str, offset, length, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=");
	}

	public static byte[] base642bytes(String str, String code)
	{
		return base642bytes(str, 0, str.length(), code);
	}

	public static byte[] base642bytes(String str, int off, int len, String code)
	{
		if (off < 0)
			throw new IndexOutOfBoundsException((new StringBuilder()).append("base642bytes: offset < 0, offset is ").append(off).toString());
		if (len < 0)
			throw new IndexOutOfBoundsException((new StringBuilder()).append("base642bytes: length < 0, length is ").append(len).toString());
		if (off + len > str.length())
			throw new IndexOutOfBoundsException("base642bytes: offset + length > string length.");
		if (code.length() < 64)
			throw new IllegalArgumentException("Base64 code length < 64.");
		int rem = len % 4;
		if (rem == 1)
			throw new IllegalArgumentException("base642bytes: base64 string length % 4 == 1.");
		int num = len / 4;
		int size = num * 3;
		if (code.length() > 64)
		{
			if (rem != 0)
				throw new IllegalArgumentException("base642bytes: base64 string length error.");
			char pc = code.charAt(64);
			if (str.charAt((off + len) - 2) == pc)
			{
				size -= 2;
				num--;
				rem = 2;
			} else
			if (str.charAt((off + len) - 1) == pc)
			{
				size--;
				num--;
				rem = 3;
			}
		} else
		if (rem == 2)
			size++;
		else
		if (rem == 3)
			size += 2;
		int r = off;
		int w = 0;
		byte b[] = new byte[size];
		byte t[] = decodeTable(code);
		for (int i = 0; i < num; i++)
		{
			int c1 = t[str.charAt(r++)];
			int c2 = t[str.charAt(r++)];
			int c3 = t[str.charAt(r++)];
			int c4 = t[str.charAt(r++)];
			b[w++] = (byte)(c1 << 2 | c2 >> 4);
			b[w++] = (byte)(c2 << 4 | c3 >> 2);
			b[w++] = (byte)(c3 << 6 | c4);
		}

		if (rem == 2)
		{
			int c1 = t[str.charAt(r++)];
			int c2 = t[str.charAt(r++)];
			b[w++] = (byte)(c1 << 2 | c2 >> 4);
		} else
		if (rem == 3)
		{
			int c1 = t[str.charAt(r++)];
			int c2 = t[str.charAt(r++)];
			int c3 = t[str.charAt(r++)];
			b[w++] = (byte)(c1 << 2 | c2 >> 4);
			b[w++] = (byte)(c2 << 4 | c3 >> 2);
		}
		return b;
	}

	public static byte[] base642bytes(String str, char code[])
	{
		return base642bytes(str, 0, str.length(), code);
	}

	public static byte[] base642bytes(String str, int off, int len, char code[])
	{
		if (off < 0)
			throw new IndexOutOfBoundsException((new StringBuilder()).append("base642bytes: offset < 0, offset is ").append(off).toString());
		if (len < 0)
			throw new IndexOutOfBoundsException((new StringBuilder()).append("base642bytes: length < 0, length is ").append(len).toString());
		if (off + len > str.length())
			throw new IndexOutOfBoundsException("base642bytes: offset + length > string length.");
		if (code.length < 64)
			throw new IllegalArgumentException("Base64 code length < 64.");
		int rem = len % 4;
		if (rem == 1)
			throw new IllegalArgumentException("base642bytes: base64 string length % 4 == 1.");
		int num = len / 4;
		int size = num * 3;
		if (code.length > 64)
		{
			if (rem != 0)
				throw new IllegalArgumentException("base642bytes: base64 string length error.");
			char pc = code[64];
			if (str.charAt((off + len) - 2) == pc)
				size -= 2;
			else
			if (str.charAt((off + len) - 1) == pc)
				size--;
		} else
		if (rem == 2)
			size++;
		else
		if (rem == 3)
			size += 2;
		int r = off;
		int w = 0;
		byte b[] = new byte[size];
		for (int i = 0; i < num; i++)
		{
			int c1 = indexOf(code, str.charAt(r++));
			int c2 = indexOf(code, str.charAt(r++));
			int c3 = indexOf(code, str.charAt(r++));
			int c4 = indexOf(code, str.charAt(r++));
			b[w++] = (byte)(c1 << 2 | c2 >> 4);
			b[w++] = (byte)(c2 << 4 | c3 >> 2);
			b[w++] = (byte)(c3 << 6 | c4);
		}

		if (rem == 2)
		{
			int c1 = indexOf(code, str.charAt(r++));
			int c2 = indexOf(code, str.charAt(r++));
			b[w++] = (byte)(c1 << 2 | c2 >> 4);
		} else
		if (rem == 3)
		{
			int c1 = indexOf(code, str.charAt(r++));
			int c2 = indexOf(code, str.charAt(r++));
			int c3 = indexOf(code, str.charAt(r++));
			b[w++] = (byte)(c1 << 2 | c2 >> 4);
			b[w++] = (byte)(c2 << 4 | c3 >> 2);
		}
		return b;
	}

	public static byte[] zip(byte bytes[])
		throws IOException
	{
		UnsafeByteArrayOutputStream bos;
		OutputStream os;
		bos = new UnsafeByteArrayOutputStream();
		os = new DeflaterOutputStream(bos);
		os.write(bytes);
		os.close();
		bos.close();
		break MISSING_BLOCK_LABEL_44;
		Exception exception;
		exception;
		os.close();
		bos.close();
		throw exception;
		return bos.toByteArray();
	}

	public static byte[] unzip(byte bytes[])
		throws IOException
	{
		UnsafeByteArrayInputStream bis;
		UnsafeByteArrayOutputStream bos;
		InputStream is;
		bis = new UnsafeByteArrayInputStream(bytes);
		bos = new UnsafeByteArrayOutputStream();
		is = new InflaterInputStream(bis);
		byte abyte0[];
		IOUtils.write(is, bos);
		abyte0 = bos.toByteArray();
		is.close();
		bis.close();
		bos.close();
		return abyte0;
		Exception exception;
		exception;
		is.close();
		bis.close();
		bos.close();
		throw exception;
	}

	public static byte[] getMD5(String str)
	{
		return getMD5(str.getBytes());
	}

	public static byte[] getMD5(byte source[])
	{
		MessageDigest md = getMessageDigest();
		return md.digest(source);
	}

	public static byte[] getMD5(File file)
		throws IOException
	{
		InputStream is = new FileInputStream(file);
		byte abyte0[] = getMD5(is);
		is.close();
		return abyte0;
		Exception exception;
		exception;
		is.close();
		throw exception;
	}

	public static byte[] getMD5(InputStream is)
		throws IOException
	{
		return getMD5(is, 8192);
	}

	private static byte hex(char c)
	{
		if (c <= '9')
			return (byte)(c - 48);
		if (c >= 'a' && c <= 'f')
			return (byte)((c - 97) + 10);
		if (c >= 'A' && c <= 'F')
			return (byte)((c - 65) + 10);
		else
			throw new IllegalArgumentException((new StringBuilder()).append("hex string format error [").append(c).append("].").toString());
	}

	private static int indexOf(char cs[], char c)
	{
		int i = 0;
		for (int len = cs.length; i < len; i++)
			if (cs[i] == c)
				return i;

		return -1;
	}

	private static byte[] decodeTable(String code)
	{
		int hash = code.hashCode();
		byte ret[] = (byte[])DECODE_TABLE_MAP.get(Integer.valueOf(hash));
		if (ret == null)
		{
			if (code.length() < 64)
				throw new IllegalArgumentException("Base64 code length < 64.");
			ret = new byte[128];
			for (int i = 0; i < 128; i++)
				ret[i] = -1;

			for (int i = 0; i < 64; i++)
				ret[code.charAt(i)] = (byte)i;

			DECODE_TABLE_MAP.put(Integer.valueOf(hash), ret);
		}
		return ret;
	}

	private static byte[] getMD5(InputStream is, int bs)
		throws IOException
	{
		MessageDigest md = getMessageDigest();
		byte buf[] = new byte[bs];
label0:
		for (; is.available() > 0; md.update(buf))
		{
			int total = 0;
			do
			{
				int read;
				if ((read = is.read(buf, total, bs - total)) <= 0)
					continue label0;
				total += read;
			} while (total < bs);
		}

		return md.digest();
	}

	private static MessageDigest getMessageDigest()
	{
		MessageDigest ret = (MessageDigest)MD.get();
		if (ret == null)
			try
			{
				ret = MessageDigest.getInstance("MD5");
				MD.set(ret);
			}
			catch (NoSuchAlgorithmException e)
			{
				throw new RuntimeException(e);
			}
		return ret;
	}

	private Bytes()
	{
	}

}
