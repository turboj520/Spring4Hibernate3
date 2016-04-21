// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   GenericDataFlags.java

package com.autohome.turbo.common.serialize.support.dubbo;


public interface GenericDataFlags
{

	public static final byte VARINT = 0;
	public static final byte OBJECT = -128;
	public static final byte VARINT8 = 0;
	public static final byte VARINT16 = 1;
	public static final byte VARINT24 = 2;
	public static final byte VARINT32 = 3;
	public static final byte VARINT40 = 4;
	public static final byte VARINT48 = 5;
	public static final byte VARINT56 = 6;
	public static final byte VARINT64 = 7;
	public static final byte VARINT_NF = 10;
	public static final byte VARINT_NE = 11;
	public static final byte VARINT_ND = 12;
	public static final byte VARINT_NC = 13;
	public static final byte VARINT_NB = 14;
	public static final byte VARINT_NA = 15;
	public static final byte VARINT_N9 = 16;
	public static final byte VARINT_N8 = 17;
	public static final byte VARINT_N7 = 18;
	public static final byte VARINT_N6 = 19;
	public static final byte VARINT_N5 = 20;
	public static final byte VARINT_N4 = 21;
	public static final byte VARINT_N3 = 22;
	public static final byte VARINT_N2 = 23;
	public static final byte VARINT_N1 = 24;
	public static final byte VARINT_0 = 25;
	public static final byte VARINT_1 = 26;
	public static final byte VARINT_2 = 27;
	public static final byte VARINT_3 = 28;
	public static final byte VARINT_4 = 29;
	public static final byte VARINT_5 = 30;
	public static final byte VARINT_6 = 31;
	public static final byte VARINT_7 = 32;
	public static final byte VARINT_8 = 33;
	public static final byte VARINT_9 = 34;
	public static final byte VARINT_A = 35;
	public static final byte VARINT_B = 36;
	public static final byte VARINT_C = 37;
	public static final byte VARINT_D = 38;
	public static final byte VARINT_E = 39;
	public static final byte VARINT_F = 40;
	public static final byte VARINT_10 = 41;
	public static final byte VARINT_11 = 42;
	public static final byte VARINT_12 = 43;
	public static final byte VARINT_13 = 44;
	public static final byte VARINT_14 = 45;
	public static final byte VARINT_15 = 46;
	public static final byte VARINT_16 = 47;
	public static final byte VARINT_17 = 48;
	public static final byte VARINT_18 = 49;
	public static final byte VARINT_19 = 50;
	public static final byte VARINT_1A = 51;
	public static final byte VARINT_1B = 52;
	public static final byte VARINT_1C = 53;
	public static final byte VARINT_1D = 54;
	public static final byte VARINT_1E = 55;
	public static final byte VARINT_1F = 56;
	public static final byte OBJECT_REF = -127;
	public static final byte OBJECT_STREAM = -126;
	public static final byte OBJECT_BYTES = -125;
	public static final byte OBJECT_VALUE = -124;
	public static final byte OBJECT_VALUES = -123;
	public static final byte OBJECT_MAP = -122;
	public static final byte OBJECT_DESC = -118;
	public static final byte OBJECT_DESC_ID = -117;
	public static final byte OBJECT_NULL = -108;
	public static final byte OBJECT_DUMMY = -107;
}
