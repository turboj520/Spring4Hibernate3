// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JSON.java

package com.autohome.turbo.common.json;

import com.autohome.turbo.common.bytecode.Wrapper;
import com.autohome.turbo.common.utils.Stack;
import java.io.*;

// Referenced classes of package com.autohome.turbo.common.json:
//			JSONWriter, ParseException, J2oVisitor, JSONReader, 
//			JSONArray, JSONObject, GenericJSONConverter, JSONVisitor, 
//			JSONToken, JSONConverter

public class JSON
{
	private static class Entry
	{

		byte state;
		Object value;

		Entry(byte s, Object v)
		{
			state = s;
			value = v;
		}
	}


	public static final char LBRACE = 123;
	public static final char RBRACE = 125;
	public static final char LSQUARE = 91;
	public static final char RSQUARE = 93;
	public static final char COMMA = 44;
	public static final char COLON = 58;
	public static final char QUOTE = 34;
	public static final String NULL = "null";
	static final JSONConverter DEFAULT_CONVERTER = new GenericJSONConverter();
	public static final byte END = 0;
	public static final byte START = 1;
	public static final byte OBJECT_ITEM = 2;
	public static final byte OBJECT_VALUE = 3;
	public static final byte ARRAY_ITEM = 4;

	private JSON()
	{
	}

	public static String json(Object obj)
		throws IOException
	{
		StringWriter sw;
		if (obj == null)
			return "null";
		sw = new StringWriter();
		String s;
		json(obj, ((Writer) (sw)));
		s = sw.getBuffer().toString();
		sw.close();
		return s;
		Exception exception;
		exception;
		sw.close();
		throw exception;
	}

	public static void json(Object obj, Writer writer)
		throws IOException
	{
		json(obj, writer, false);
	}

	public static void json(Object obj, Writer writer, boolean writeClass)
		throws IOException
	{
		if (obj == null)
			writer.write("null");
		else
			json(obj, new JSONWriter(writer), writeClass);
	}

	public static String json(Object obj, String properties[])
		throws IOException
	{
		StringWriter sw;
		if (obj == null)
			return "null";
		sw = new StringWriter();
		String s;
		json(obj, properties, ((Writer) (sw)));
		s = sw.getBuffer().toString();
		sw.close();
		return s;
		Exception exception;
		exception;
		sw.close();
		throw exception;
	}

	public static void json(Object obj, String properties[], Writer writer)
		throws IOException
	{
		json(obj, properties, writer, false);
	}

	public static void json(Object obj, String properties[], Writer writer, boolean writeClass)
		throws IOException
	{
		if (obj == null)
			writer.write("null");
		else
			json(obj, properties, new JSONWriter(writer), writeClass);
	}

	private static void json(Object obj, JSONWriter jb, boolean writeClass)
		throws IOException
	{
		if (obj == null)
			jb.valueNull();
		else
			DEFAULT_CONVERTER.writeValue(obj, jb, writeClass);
	}

	private static void json(Object obj, String properties[], JSONWriter jb, boolean writeClass)
		throws IOException
	{
		if (obj == null)
		{
			jb.valueNull();
		} else
		{
			Wrapper wrapper = Wrapper.getWrapper(obj.getClass());
			jb.objectBegin();
			String arr$[] = properties;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				String prop = arr$[i$];
				jb.objectItem(prop);
				Object value = wrapper.getPropertyValue(obj, prop);
				if (value == null)
					jb.valueNull();
				else
					DEFAULT_CONVERTER.writeValue(value, jb, writeClass);
			}

			jb.objectEnd();
		}
	}

	public static Object parse(String json)
		throws ParseException
	{
		Exception exception;
		StringReader reader = new StringReader(json);
		Object obj;
		try
		{
			obj = parse(((Reader) (reader)));
		}
		catch (IOException e)
		{
			throw new ParseException(e.getMessage());
		}
		finally
		{
			reader.close();
		}
		reader.close();
		return obj;
		throw exception;
	}

	public static Object parse(Reader reader)
		throws IOException, ParseException
	{
		return parse(reader, 0);
	}

	public static Object parse(String json, Class type)
		throws ParseException
	{
		Exception exception;
		StringReader reader = new StringReader(json);
		Object obj;
		try
		{
			obj = parse(((Reader) (reader)), type);
		}
		catch (IOException e)
		{
			throw new ParseException(e.getMessage());
		}
		finally
		{
			reader.close();
		}
		reader.close();
		return obj;
		throw exception;
	}

	public static Object parse(Reader reader, Class type)
		throws IOException, ParseException
	{
		return parse(reader, ((JSONVisitor) (new J2oVisitor(type, DEFAULT_CONVERTER))), 0);
	}

	public static Object[] parse(String json, Class types[])
		throws ParseException
	{
		Exception exception;
		StringReader reader = new StringReader(json);
		Object aobj[];
		try
		{
			aobj = (Object[])parse(((Reader) (reader)), types);
		}
		catch (IOException e)
		{
			throw new ParseException(e.getMessage());
		}
		finally
		{
			reader.close();
		}
		reader.close();
		return aobj;
		throw exception;
	}

	public static Object[] parse(Reader reader, Class types[])
		throws IOException, ParseException
	{
		return (Object[])(Object[])parse(reader, ((JSONVisitor) (new J2oVisitor(types, DEFAULT_CONVERTER))), 3);
	}

	public static Object parse(String json, JSONVisitor handler)
		throws ParseException
	{
		Exception exception;
		StringReader reader = new StringReader(json);
		Object obj;
		try
		{
			obj = parse(((Reader) (reader)), handler);
		}
		catch (IOException e)
		{
			throw new ParseException(e.getMessage());
		}
		finally
		{
			reader.close();
		}
		reader.close();
		return obj;
		throw exception;
	}

	public static Object parse(Reader reader, JSONVisitor handler)
		throws IOException, ParseException
	{
		return parse(reader, handler, 0);
	}

	private static Object parse(Reader reader, int expect)
		throws IOException, ParseException
	{
		JSONReader jr = new JSONReader(reader);
		JSONToken token = jr.nextToken(expect);
		byte state = 1;
		Object value = null;
		Stack stack = new Stack();
		do
			switch (state)
			{
			case 0: // '\0'
				throw new ParseException("JSON source format error.");

			case 1: // '\001'
				switch (token.type)
				{
				case 16: // '\020'
				case 17: // '\021'
				case 18: // '\022'
				case 19: // '\023'
				case 20: // '\024'
					state = 0;
					value = token.value;
					break;

				case 3: // '\003'
					state = 4;
					value = new JSONArray();
					break;

				case 2: // '\002'
					state = 2;
					value = new JSONObject();
					break;

				case 4: // '\004'
				case 5: // '\005'
				case 6: // '\006'
				case 7: // '\007'
				case 8: // '\b'
				case 9: // '\t'
				case 10: // '\n'
				case 11: // '\013'
				case 12: // '\f'
				case 13: // '\r'
				case 14: // '\016'
				case 15: // '\017'
				default:
					throw new ParseException((new StringBuilder()).append("Unexcepted token expect [ VALUE or '[' or '{' ] get '").append(JSONToken.token2string(token.type)).append("'").toString());
				}
				break;

			case 4: // '\004'
				switch (token.type)
				{
				case 16: // '\020'
				case 17: // '\021'
				case 18: // '\022'
				case 19: // '\023'
				case 20: // '\024'
				{
					((JSONArray)value).add(token.value);
					break;
				}

				case 5: // '\005'
				{
					if (stack.isEmpty())
					{
						state = 0;
					} else
					{
						Entry entry = (Entry)stack.pop();
						state = entry.state;
						value = entry.value;
					}
					break;
				}

				case 3: // '\003'
				{
					Object tmp = new JSONArray();
					((JSONArray)value).add(tmp);
					stack.push(new Entry(state, value));
					state = 4;
					value = tmp;
					break;
				}

				case 2: // '\002'
				{
					Object tmp = new JSONObject();
					((JSONArray)value).add(tmp);
					stack.push(new Entry(state, value));
					state = 2;
					value = tmp;
					break;
				}

				case 4: // '\004'
				case 7: // '\007'
				case 8: // '\b'
				case 9: // '\t'
				case 10: // '\n'
				case 11: // '\013'
				case 12: // '\f'
				case 13: // '\r'
				case 14: // '\016'
				case 15: // '\017'
				default:
				{
					throw new ParseException((new StringBuilder()).append("Unexcepted token expect [ VALUE or ',' or ']' or '[' or '{' ] get '").append(JSONToken.token2string(token.type)).append("'").toString());
				}

				case 6: // '\006'
					break;
				}
				break;

			case 2: // '\002'
				switch (token.type)
				{
				case 1: // '\001'
					stack.push(new Entry((byte)2, (String)token.value));
					state = 3;
					break;

				case 16: // '\020'
					stack.push(new Entry((byte)2, "null"));
					state = 3;
					break;

				case 17: // '\021'
				case 18: // '\022'
				case 19: // '\023'
				case 20: // '\024'
					stack.push(new Entry((byte)2, token.value.toString()));
					state = 3;
					break;

				case 4: // '\004'
					if (stack.isEmpty())
					{
						state = 0;
					} else
					{
						Entry entry = (Entry)stack.pop();
						state = entry.state;
						value = entry.value;
					}
					break;

				case 2: // '\002'
				case 3: // '\003'
				case 5: // '\005'
				case 7: // '\007'
				case 8: // '\b'
				case 9: // '\t'
				case 10: // '\n'
				case 11: // '\013'
				case 12: // '\f'
				case 13: // '\r'
				case 14: // '\016'
				case 15: // '\017'
				default:
					throw new ParseException((new StringBuilder()).append("Unexcepted token expect [ IDENT or VALUE or ',' or '}' ] get '").append(JSONToken.token2string(token.type)).append("'").toString());

				case 6: // '\006'
					break;
				}
				break;

			case 3: // '\003'
				switch (token.type)
				{
				case 16: // '\020'
				case 17: // '\021'
				case 18: // '\022'
				case 19: // '\023'
				case 20: // '\024'
				{
					((JSONObject)value).put((String)((Entry)stack.pop()).value, token.value);
					state = 2;
					break;
				}

				case 3: // '\003'
				{
					Object tmp = new JSONArray();
					((JSONObject)value).put((String)((Entry)stack.pop()).value, tmp);
					stack.push(new Entry((byte)2, value));
					state = 4;
					value = tmp;
					break;
				}

				case 2: // '\002'
				{
					Object tmp = new JSONObject();
					((JSONObject)value).put((String)((Entry)stack.pop()).value, tmp);
					stack.push(new Entry((byte)2, value));
					state = 2;
					value = tmp;
					break;
				}

				case 4: // '\004'
				case 5: // '\005'
				case 6: // '\006'
				case 8: // '\b'
				case 9: // '\t'
				case 10: // '\n'
				case 11: // '\013'
				case 12: // '\f'
				case 13: // '\r'
				case 14: // '\016'
				case 15: // '\017'
				default:
				{
					throw new ParseException((new StringBuilder()).append("Unexcepted token expect [ VALUE or '[' or '{' ] get '").append(JSONToken.token2string(token.type)).append("'").toString());
				}

				case 7: // '\007'
					break;
				}
				break;

			default:
				throw new ParseException("Unexcepted state.");
			}
		while ((token = jr.nextToken()) != null);
		stack.clear();
		return value;
	}

	private static Object parse(Reader reader, JSONVisitor handler, int expect)
		throws IOException, ParseException
	{
		JSONReader jr = new JSONReader(reader);
		JSONToken token = jr.nextToken(expect);
		Object value = null;
		int state = 1;
		int index = 0;
		Stack states = new Stack();
		boolean pv = false;
		handler.begin();
		do
			switch (state)
			{
			case 0: // '\0'
				throw new ParseException("JSON source format error.");

			case 1: // '\001'
				switch (token.type)
				{
				case 16: // '\020'
					value = token.value;
					state = 0;
					pv = true;
					break;

				case 17: // '\021'
					value = token.value;
					state = 0;
					pv = true;
					break;

				case 18: // '\022'
					value = token.value;
					state = 0;
					pv = true;
					break;

				case 19: // '\023'
					value = token.value;
					state = 0;
					pv = true;
					break;

				case 20: // '\024'
					value = token.value;
					state = 0;
					pv = true;
					break;

				case 3: // '\003'
					handler.arrayBegin();
					state = 4;
					break;

				case 2: // '\002'
					handler.objectBegin();
					state = 2;
					break;

				case 4: // '\004'
				case 5: // '\005'
				case 6: // '\006'
				case 7: // '\007'
				case 8: // '\b'
				case 9: // '\t'
				case 10: // '\n'
				case 11: // '\013'
				case 12: // '\f'
				case 13: // '\r'
				case 14: // '\016'
				case 15: // '\017'
				default:
					throw new ParseException((new StringBuilder()).append("Unexcepted token expect [ VALUE or '[' or '{' ] get '").append(JSONToken.token2string(token.type)).append("'").toString());
				}
				break;

			case 4: // '\004'
				switch (token.type)
				{
				case 16: // '\020'
					handler.arrayItem(index++);
					handler.arrayItemValue(index, token.value, true);
					break;

				case 17: // '\021'
					handler.arrayItem(index++);
					handler.arrayItemValue(index, token.value, true);
					break;

				case 18: // '\022'
					handler.arrayItem(index++);
					handler.arrayItemValue(index, token.value, true);
					break;

				case 19: // '\023'
					handler.arrayItem(index++);
					handler.arrayItemValue(index, token.value, true);
					break;

				case 20: // '\024'
					handler.arrayItem(index++);
					handler.arrayItemValue(index, token.value, true);
					break;

				case 3: // '\003'
					handler.arrayItem(index++);
					states.push(new int[] {
						state, index
					});
					index = 0;
					state = 4;
					handler.arrayBegin();
					break;

				case 2: // '\002'
					handler.arrayItem(index++);
					states.push(new int[] {
						state, index
					});
					index = 0;
					state = 2;
					handler.objectBegin();
					break;

				case 5: // '\005'
					if (states.isEmpty())
					{
						value = handler.arrayEnd(index);
						state = 0;
					} else
					{
						value = handler.arrayEnd(index);
						int tmp[] = (int[])states.pop();
						state = tmp[0];
						index = tmp[1];
						switch (state)
						{
						case 4: // '\004'
							handler.arrayItemValue(index, value, false);
							break;

						case 2: // '\002'
							handler.objectItemValue(value, false);
							break;
						}
					}
					break;

				case 4: // '\004'
				case 7: // '\007'
				case 8: // '\b'
				case 9: // '\t'
				case 10: // '\n'
				case 11: // '\013'
				case 12: // '\f'
				case 13: // '\r'
				case 14: // '\016'
				case 15: // '\017'
				default:
					throw new ParseException((new StringBuilder()).append("Unexcepted token expect [ VALUE or ',' or ']' or '[' or '{' ] get '").append(JSONToken.token2string(token.type)).append("'").toString());

				case 6: // '\006'
					break;
				}
				break;

			case 2: // '\002'
				switch (token.type)
				{
				case 1: // '\001'
					handler.objectItem((String)token.value);
					state = 3;
					break;

				case 16: // '\020'
					handler.objectItem("null");
					state = 3;
					break;

				case 17: // '\021'
				case 18: // '\022'
				case 19: // '\023'
				case 20: // '\024'
					handler.objectItem(token.value.toString());
					state = 3;
					break;

				case 4: // '\004'
					if (states.isEmpty())
					{
						value = handler.objectEnd(index);
						state = 0;
					} else
					{
						value = handler.objectEnd(index);
						int tmp[] = (int[])states.pop();
						state = tmp[0];
						index = tmp[1];
						switch (state)
						{
						case 4: // '\004'
							handler.arrayItemValue(index, value, false);
							break;

						case 2: // '\002'
							handler.objectItemValue(value, false);
							break;
						}
					}
					break;

				case 2: // '\002'
				case 3: // '\003'
				case 5: // '\005'
				case 7: // '\007'
				case 8: // '\b'
				case 9: // '\t'
				case 10: // '\n'
				case 11: // '\013'
				case 12: // '\f'
				case 13: // '\r'
				case 14: // '\016'
				case 15: // '\017'
				default:
					throw new ParseException((new StringBuilder()).append("Unexcepted token expect [ IDENT or VALUE or ',' or '}' ] get '").append(JSONToken.token2string(token.type)).append("'").toString());

				case 6: // '\006'
					break;
				}
				break;

			case 3: // '\003'
				switch (token.type)
				{
				case 16: // '\020'
					handler.objectItemValue(token.value, true);
					state = 2;
					break;

				case 17: // '\021'
					handler.objectItemValue(token.value, true);
					state = 2;
					break;

				case 18: // '\022'
					handler.objectItemValue(token.value, true);
					state = 2;
					break;

				case 19: // '\023'
					handler.objectItemValue(token.value, true);
					state = 2;
					break;

				case 20: // '\024'
					handler.objectItemValue(token.value, true);
					state = 2;
					break;

				case 3: // '\003'
					states.push(new int[] {
						2, index
					});
					index = 0;
					state = 4;
					handler.arrayBegin();
					break;

				case 2: // '\002'
					states.push(new int[] {
						2, index
					});
					index = 0;
					state = 2;
					handler.objectBegin();
					break;

				case 4: // '\004'
				case 5: // '\005'
				case 6: // '\006'
				case 8: // '\b'
				case 9: // '\t'
				case 10: // '\n'
				case 11: // '\013'
				case 12: // '\f'
				case 13: // '\r'
				case 14: // '\016'
				case 15: // '\017'
				default:
					throw new ParseException((new StringBuilder()).append("Unexcepted token expect [ VALUE or '[' or '{' ] get '").append(JSONToken.token2string(token.type)).append("'").toString());

				case 7: // '\007'
					break;
				}
				break;

			default:
				throw new ParseException("Unexcepted state.");
			}
		while ((token = jr.nextToken()) != null);
		states.clear();
		return handler.end(value, pv);
	}

}
