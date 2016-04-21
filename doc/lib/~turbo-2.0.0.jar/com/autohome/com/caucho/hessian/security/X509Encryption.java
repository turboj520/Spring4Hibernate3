// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   X509Encryption.java

package com.autohome.com.caucho.hessian.security;

import com.autohome.com.caucho.hessian.io.*;
import java.io.*;
import java.security.*;
import java.security.cert.X509Certificate;
import javax.crypto.*;

public class X509Encryption extends HessianEnvelope
{
	class EncryptInputStream extends InputStream
	{

		private Hessian2Input _in;
		private Cipher _cipher;
		private InputStream _bodyIn;
		private CipherInputStream _cipherIn;
		final X509Encryption this$0;

		public int read()
			throws IOException
		{
			return _cipherIn.read();
		}

		public int read(byte buffer[], int offset, int length)
			throws IOException
		{
			return _cipherIn.read(buffer, offset, length);
		}

		public void close()
			throws IOException
		{
			Hessian2Input in = _in;
			_in = null;
			if (in != null)
			{
				_cipherIn.close();
				_bodyIn.close();
				int len = in.readInt();
				if (len != 0)
					throw new IOException("Unexpected footer");
				in.completeEnvelope();
				in.close();
			}
		}

		EncryptInputStream(Hessian2Input in)
			throws IOException
		{
			this$0 = X509Encryption.this;
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
				keyCipher.init(4, _privateKey);
				java.security.Key key = keyCipher.unwrap(encKey, algorithm, 3);
				_bodyIn = _in.readInputStream();
				_cipher = Cipher.getInstance(algorithm);
				_cipher.init(2, key);
				_cipherIn = new CipherInputStream(_bodyIn, _cipher);
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

	class EncryptOutputStream extends OutputStream
	{

		private Hessian2Output _out;
		private Cipher _cipher;
		private OutputStream _bodyOut;
		private CipherOutputStream _cipherOut;
		final X509Encryption this$0;

		public void write(int ch)
			throws IOException
		{
			_cipherOut.write(ch);
		}

		public void write(byte buffer[], int offset, int length)
			throws IOException
		{
			_cipherOut.write(buffer, offset, length);
		}

		public void close()
			throws IOException
		{
			Hessian2Output out = _out;
			_out = null;
			if (out != null)
			{
				_cipherOut.close();
				_bodyOut.close();
				out.writeInt(0);
				out.completeEnvelope();
				out.close();
			}
		}

		EncryptOutputStream(Hessian2Output out)
			throws IOException
		{
			this$0 = X509Encryption.this;
			super();
			try
			{
				_out = out;
				KeyGenerator keyGen = KeyGenerator.getInstance(_algorithm);
				if (_secureRandom != null)
					keyGen.init(_secureRandom);
				SecretKey sharedKey = keyGen.generateKey();
				_out = out;
				_out.startEnvelope(com/autohome/com/caucho/hessian/security/X509Encryption.getName());
				PublicKey publicKey = _cert.getPublicKey();
				byte encoded[] = publicKey.getEncoded();
				MessageDigest md = MessageDigest.getInstance("SHA1");
				md.update(encoded);
				byte fingerprint[] = md.digest();
				String keyAlgorithm = publicKey.getAlgorithm();
				Cipher keyCipher = Cipher.getInstance(keyAlgorithm);
				if (_secureRandom != null)
					keyCipher.init(3, _cert, _secureRandom);
				else
					keyCipher.init(3, _cert);
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
				_bodyOut = _out.getBytesOutputStream();
				_cipher = Cipher.getInstance(_algorithm);
				if (_secureRandom != null)
					_cipher.init(1, sharedKey, _secureRandom);
				else
					_cipher.init(1, sharedKey);
				_cipherOut = new CipherOutputStream(_bodyOut, _cipher);
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

	public X509Encryption()
	{
		_algorithm = "AES";
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

	public void setPrivateKey(PrivateKey privateKey)
	{
		_privateKey = privateKey;
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
		if (_cert == null)
		{
			throw new IOException("X509Encryption.wrap requires a certificate");
		} else
		{
			OutputStream os = new EncryptOutputStream(out);
			Hessian2Output filterOut = new Hessian2Output(os);
			filterOut.setCloseStreamOnClose(true);
			return filterOut;
		}
	}

	public Hessian2Input unwrap(Hessian2Input in)
		throws IOException
	{
		if (_privateKey == null)
			throw new IOException("X509Encryption.unwrap requires a private key");
		if (_cert == null)
			throw new IOException("X509Encryption.unwrap requires a certificate");
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
		if (_privateKey == null)
			throw new IOException("X509Encryption.unwrap requires a private key");
		if (_cert == null)
		{
			throw new IOException("X509Encryption.unwrap requires a certificate");
		} else
		{
			InputStream is = new EncryptInputStream(in);
			Hessian2Input filter = new Hessian2Input(is);
			filter.setCloseStreamOnClose(true);
			return filter;
		}
	}




}
