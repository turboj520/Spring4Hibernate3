// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   X509Signature.java

package com.autohome.hessian.security;

import com.autohome.hessian.io.*;
import java.io.*;
import java.security.*;
import java.security.cert.X509Certificate;
import javax.crypto.*;

public class X509Signature extends HessianEnvelope
{
	class SignatureInputStream extends InputStream
	{

		private Hessian2Input _in;
		private Mac _mac;
		private InputStream _bodyIn;
		private CipherInputStream _cipherIn;
		final X509Signature this$0;

		public int read()
			throws IOException
		{
			int ch = _bodyIn.read();
			if (ch < 0)
			{
				return ch;
			} else
			{
				_mac.update((byte)ch);
				return ch;
			}
		}

		public int read(byte buffer[], int offset, int length)
			throws IOException
		{
			int len = _bodyIn.read(buffer, offset, length);
			if (len < 0)
			{
				return len;
			} else
			{
				_mac.update(buffer, offset, len);
				return len;
			}
		}

		public void close()
			throws IOException
		{
			Hessian2Input in = _in;
			_in = null;
			if (in != null)
			{
				_bodyIn.close();
				int len = in.readInt();
				byte signature[] = null;
				for (int i = 0; i < len; i++)
				{
					String header = in.readString();
					if ("signature".equals(header))
						signature = in.readBytes();
				}

				in.completeEnvelope();
				in.close();
				if (signature == null)
					throw new IOException("Expected signature");
				byte sig[] = _mac.doFinal();
				if (sig.length != signature.length)
					throw new IOException("mismatched signature");
				for (int i = 0; i < sig.length; i++)
					if (signature[i] != sig[i])
						throw new IOException("mismatched signature");

			}
		}

		SignatureInputStream(Hessian2Input in)
			throws IOException
		{
			this$0 = X509Signature.this;
			super();
			try
			{
				_in = in;
				byte fingerprint[] = null;
				String keyAlgorithm = null;
				String algorithm = null;
				byte encKey[] = null;
				int len = in.readInt();
				for (int i = 0; i < len; i++)
				{
					String header = in.readString();
					if ("fingerprint".equals(header))
					{
						fingerprint = in.readBytes();
						continue;
					}
					if ("key-algorithm".equals(header))
					{
						keyAlgorithm = in.readString();
						continue;
					}
					if ("algorithm".equals(header))
					{
						algorithm = in.readString();
						continue;
					}
					if ("key".equals(header))
						encKey = in.readBytes();
					else
						throw new IOException((new StringBuilder()).append("'").append(header).append("' is an unexpected header").toString());
				}

				Cipher keyCipher = Cipher.getInstance(keyAlgorithm);
				keyCipher.init(4, _cert);
				java.security.Key key = keyCipher.unwrap(encKey, algorithm, 3);
				_bodyIn = _in.readInputStream();
				_mac = Mac.getInstance(algorithm);
				_mac.init(key);
			}
			catch (RuntimeException e)
			{
				throw e;
			}
			catch (IOException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	class SignatureOutputStream extends OutputStream
	{

		private Hessian2Output _out;
		private OutputStream _bodyOut;
		private Mac _mac;
		final X509Signature this$0;

		public void write(int ch)
			throws IOException
		{
			_bodyOut.write(ch);
			_mac.update((byte)ch);
		}

		public void write(byte buffer[], int offset, int length)
			throws IOException
		{
			_bodyOut.write(buffer, offset, length);
			_mac.update(buffer, offset, length);
		}

		public void close()
			throws IOException
		{
			Hessian2Output out = _out;
			_out = null;
			if (out == null)
			{
				return;
			} else
			{
				_bodyOut.close();
				byte sig[] = _mac.doFinal();
				out.writeInt(1);
				out.writeString("signature");
				out.writeBytes(sig);
				out.completeEnvelope();
				out.close();
				return;
			}
		}

		SignatureOutputStream(Hessian2Output out)
			throws IOException
		{
			this$0 = X509Signature.this;
			super();
			try
			{
				KeyGenerator keyGen = KeyGenerator.getInstance(_algorithm);
				if (_secureRandom != null)
					keyGen.init(_secureRandom);
				javax.crypto.SecretKey sharedKey = keyGen.generateKey();
				_out = out;
				_out.startEnvelope(com/autohome/hessian/security/X509Signature.getName());
				PublicKey publicKey = _cert.getPublicKey();
				byte encoded[] = publicKey.getEncoded();
				MessageDigest md = MessageDigest.getInstance("SHA1");
				md.update(encoded);
				byte fingerprint[] = md.digest();
				String keyAlgorithm = _privateKey.getAlgorithm();
				Cipher keyCipher = Cipher.getInstance(keyAlgorithm);
				keyCipher.init(3, _privateKey);
				byte encKey[] = keyCipher.wrap(sharedKey);
				_out.writeInt(4);
				_out.writeString("algorithm");
				_out.writeString(_algorithm);
				_out.writeString("fingerprint");
				_out.writeBytes(fingerprint);
				_out.writeString("key-algorithm");
				_out.writeString(keyAlgorithm);
				_out.writeString("key");
				_out.writeBytes(encKey);
				_mac = Mac.getInstance(_algorithm);
				_mac.init(sharedKey);
				_bodyOut = _out.getBytesOutputStream();
			}
			catch (RuntimeException e)
			{
				throw e;
			}
			catch (IOException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}


	private String _algorithm;
	private X509Certificate _cert;
	private PrivateKey _privateKey;
	private SecureRandom _secureRandom;

	public X509Signature()
	{
		_algorithm = "HmacSHA256";
	}

	public void setAlgorithm(String algorithm)
	{
		if (algorithm == null)
		{
			throw new NullPointerException();
		} else
		{
			_algorithm = algorithm;
			return;
		}
	}

	public String getAlgorithm()
	{
		return _algorithm;
	}

	public X509Certificate getCertificate()
	{
		return _cert;
	}

	public void setCertificate(X509Certificate cert)
	{
		_cert = cert;
	}

	public PrivateKey getPrivateKey()
	{
		return _privateKey;
	}

	public void setPrivateKey(PrivateKey key)
	{
		_privateKey = key;
	}

	public SecureRandom getSecureRandom()
	{
		return _secureRandom;
	}

	public void setSecureRandom(SecureRandom random)
	{
		_secureRandom = random;
	}

	public Hessian2Output wrap(Hessian2Output out)
		throws IOException
	{
		if (_privateKey == null)
			throw new IOException("X509Signature.wrap requires a private key");
		if (_cert == null)
		{
			throw new IOException("X509Signature.wrap requires a certificate");
		} else
		{
			OutputStream os = new SignatureOutputStream(out);
			Hessian2Output filterOut = new Hessian2Output(os);
			filterOut.setCloseStreamOnClose(true);
			return filterOut;
		}
	}

	public Hessian2Input unwrap(Hessian2Input in)
		throws IOException
	{
		if (_cert == null)
			throw new IOException("X509Signature.unwrap requires a certificate");
		int version = in.readEnvelope();
		String method = in.readMethod();
		if (!method.equals(getClass().getName()))
			throw new IOException((new StringBuilder()).append("expected hessian Envelope method '").append(getClass().getName()).append("' at '").append(method).append("'").toString());
		else
			return unwrapHeaders(in);
	}

	public Hessian2Input unwrapHeaders(Hessian2Input in)
		throws IOException
	{
		if (_cert == null)
		{
			throw new IOException("X509Signature.unwrap requires a certificate");
		} else
		{
			InputStream is = new SignatureInputStream(in);
			Hessian2Input filter = new Hessian2Input(is);
			filter.setCloseStreamOnClose(true);
			return filter;
		}
	}




}
