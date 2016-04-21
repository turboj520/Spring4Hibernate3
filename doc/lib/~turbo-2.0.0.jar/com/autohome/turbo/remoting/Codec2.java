// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Codec2.java

package com.autohome.turbo.remoting;

import com.autohome.turbo.remoting.buffer.ChannelBuffer;
import java.io.IOException;

// Referenced classes of package com.autohome.turbo.remoting:
//			Channel

public interface Codec2
{
	public static final class DecodeResult extends Enum
	{

		public static final DecodeResult NEED_MORE_INPUT;
		public static final DecodeResult SKIP_SOME_INPUT;
		private static final DecodeResult $VALUES[];

		public static DecodeResult[] values()
		{
			return (DecodeResult[])$VALUES.clone();
		}

		public static DecodeResult valueOf(String name)
		{
			return (DecodeResult)Enum.valueOf(com/autohome/turbo/remoting/Codec2$DecodeResult, name);
		}

		static 
		{
			NEED_MORE_INPUT = new DecodeResult("NEED_MORE_INPUT", 0);
			SKIP_SOME_INPUT = new DecodeResult("SKIP_SOME_INPUT", 1);
			$VALUES = (new DecodeResult[] {
				NEED_MORE_INPUT, SKIP_SOME_INPUT
			});
		}

		private DecodeResult(String s, int i)
		{
			super(s, i);
		}
	}


	public abstract void encode(Channel channel, ChannelBuffer channelbuffer, Object obj)
		throws IOException;

	public abstract Object decode(Channel channel, ChannelBuffer channelbuffer)
		throws IOException;
}
