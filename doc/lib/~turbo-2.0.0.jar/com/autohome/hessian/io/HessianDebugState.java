// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianDebugState.java

package com.autohome.hessian.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

// Referenced classes of package com.autohome.hessian.io:
//			Hessian2Constants

public class HessianDebugState
	implements Hessian2Constants
{
	static class ObjectDef
	{

		private String _type;
		private ArrayList _fields;

		String getType()
		{
			return _type;
		}

		ArrayList getFields()
		{
			return _fields;
		}

		ObjectDef(String type, ArrayList fields)
		{
			_type = type;
			_fields = fields;
		}
	}

	class StreamingState extends State
	{

		private int _digit;
		private int _code;
		private int _length;
		private boolean _isLast;
		private boolean _isFirst;
		private State _childState;
		final HessianDebugState this$0;

		State next(int ch)
		{
			if (_digit >= 0)
			{
				if (_digit == 0)
				{
					_code = ch;
					_digit = 1;
					_length = 0;
					return this;
				}
				if ((ch & 0x80) == 128)
				{
					_length = 128 * _length + (ch & 0x7f);
					return this;
				}
				_length = 128 * _length + (ch & 0x7f);
				_digit = -1;
				if (_isFirst)
					println(-1, (new StringBuilder()).append("packet-start(").append(_length).append(")").toString());
				_isFirst = false;
				if (_length == 0)
				{
					_isFirst = true;
					println(-1, "");
					println(-1, "packet-end");
					_refId = 0;
					_digit = 0;
				}
				return this;
			}
			if (_length == 0)
			{
				_digit = 0;
				return this;
			}
			_childState = _childState.next(ch);
			_length--;
			if (_length <= 0)
			{
				_digit = 0;
				_length = 0;
				return this;
			} else
			{
				return this;
			}
		}

		StreamingState(State next, boolean isLast)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_isFirst = true;
			_isLast = isLast;
			_childState = new InitialState();
		}
	}

	class RemoteState extends State
	{

		private static final int TYPE = 0;
		private static final int VALUE = 1;
		private static final int END = 2;
		private int _state;
		private int _major;
		private int _minor;
		final HessianDebugState this$0;

		State next(int ch)
		{
			switch (_state)
			{
			case 0: // '\0'
				println(-1, "remote");
				if (ch == 116)
				{
					_state = 1;
					return new StringState(this, 't', false);
				} else
				{
					_state = 2;
					return nextObject(ch);
				}

			case 1: // '\001'
				_state = 2;
				return _next.nextObject(ch);

			case 2: // '\002'
				return _next.next(ch);
			}
			throw new IllegalStateException();
		}

		RemoteState(State next)
		{
			this$0 = HessianDebugState.this;
			super(next);
		}
	}

	class IndirectState extends State
	{

		final HessianDebugState this$0;

		boolean isShift(Object object)
		{
			return _next.isShift(object);
		}

		State shift(Object object)
		{
			return _next.shift(object);
		}

		State next(int ch)
		{
			return nextObject(ch);
		}

		IndirectState(State next)
		{
			this$0 = HessianDebugState.this;
			super(next);
		}
	}

	class Fault2State extends State
	{

		final HessianDebugState this$0;

		int depth()
		{
			return _next.depth() + 2;
		}

		State next(int ch)
		{
			return nextObject(ch);
		}

		Fault2State(State next)
		{
			this$0 = HessianDebugState.this;
			super(next);
			println(-2, "Fault");
		}
	}

	class Reply2State extends State
	{

		final HessianDebugState this$0;

		int depth()
		{
			return _next.depth() + 2;
		}

		State next(int ch)
		{
			if (ch < 0)
			{
				println();
				return _next;
			} else
			{
				return nextObject(ch);
			}
		}

		Reply2State(State next)
		{
			this$0 = HessianDebugState.this;
			super(next);
			println(-2, "Reply");
		}
	}

	class ReplyState1 extends State1
	{

		private static final int MAJOR = 0;
		private static final int MINOR = 1;
		private static final int HEADER = 2;
		private static final int VALUE = 3;
		private static final int END = 4;
		private int _state;
		private int _major;
		private int _minor;
		final HessianDebugState this$0;

		int depth()
		{
			return _next.depth() + 2;
		}

		State next(int ch)
		{
			switch (_state)
			{
			case 0: // '\0'
				if (ch == 116 || ch == 83)
				{
					return (new RemoteState(this)).next(ch);
				} else
				{
					_major = ch;
					_state = 1;
					return this;
				}

			case 1: // '\001'
				_minor = ch;
				_state = 2;
				println(-2, (new StringBuilder()).append("reply ").append(_major).append(".").append(_minor).toString());
				return this;

			case 2: // '\002'
				if (ch == 72)
				{
					_state = 3;
					return new StringState(this, 'H', true);
				}
				if (ch == 102)
				{
					print("fault ");
					_isObject = false;
					_state = 4;
					return new MapState(this, 0);
				} else
				{
					_state = 4;
					return nextObject(ch);
				}

			case 3: // '\003'
				_state = 2;
				return nextObject(ch);

			case 4: // '\004'
				println();
				if (ch == 122)
					return _next;
				else
					return _next.next(ch);
			}
			throw new IllegalStateException();
		}

		ReplyState1(State next)
		{
			this$0 = HessianDebugState.this;
			super();
			_next = next;
		}
	}

	class Call2State extends State
	{

		private static final int METHOD = 0;
		private static final int COUNT = 1;
		private static final int ARG = 2;
		private int _state;
		private int _i;
		private int _count;
		final HessianDebugState this$0;

		int depth()
		{
			return _next.depth() + 5;
		}

		boolean isShift(Object value)
		{
			return _state != 2;
		}

		State shift(Object object)
		{
			if (_state == 0)
			{
				println(-5, (new StringBuilder()).append("Call ").append(object).toString());
				_state = 1;
				return this;
			}
			if (_state == 1)
			{
				Integer count = (Integer)object;
				_count = count.intValue();
				_state = 2;
				if (_count == 0)
					return _next;
				else
					return this;
			} else
			{
				return this;
			}
		}

		State next(int ch)
		{
			switch (_state)
			{
			case 1: // '\001'
				return nextObject(ch);

			case 0: // '\0'
				return nextObject(ch);

			case 2: // '\002'
				if (_count <= _i)
				{
					println();
					return _next.next(ch);
				} else
				{
					println();
					print(-3, (new StringBuilder()).append(_i++).append(": ").toString());
					return nextObject(ch);
				}
			}
			throw new IllegalStateException();
		}

		Call2State(State next)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_state = 0;
		}
	}

	class CallState1 extends State1
	{

		private static final int MAJOR = 0;
		private static final int MINOR = 1;
		private static final int HEADER = 2;
		private static final int METHOD = 3;
		private static final int VALUE = 4;
		private static final int ARG = 5;
		private int _state;
		private int _major;
		private int _minor;
		final HessianDebugState this$0;

		int depth()
		{
			return _next.depth() + 2;
		}

		State next(int ch)
		{
			switch (_state)
			{
			case 0: // '\0'
				_major = ch;
				_state = 1;
				return this;

			case 1: // '\001'
				_minor = ch;
				_state = 2;
				println(-2, (new StringBuilder()).append("call ").append(_major).append(".").append(_minor).toString());
				return this;

			case 2: // '\002'
				if (ch == 72)
				{
					println();
					print("header ");
					_isObject = false;
					_state = 4;
					return new StringState(this, 'H', true);
				}
				if (ch == 109)
				{
					println();
					print("method ");
					_isObject = false;
					_state = 5;
					return new StringState(this, 'm', true);
				} else
				{
					println((new StringBuilder()).append((char)ch).append(": unexpected char").toString());
					return popStack();
				}

			case 4: // '\004'
				print(" => ");
				_isObject = false;
				_state = 2;
				return nextObject(ch);

			case 5: // '\005'
				if (ch == 122)
				{
					println();
					return _next;
				} else
				{
					return nextObject(ch);
				}

			case 3: // '\003'
			default:
				throw new IllegalStateException();
			}
		}

		CallState1(State next)
		{
			this$0 = HessianDebugState.this;
			super(next);
		}
	}

	class Hessian2State extends State
	{

		private static final int MAJOR = 0;
		private static final int MINOR = 1;
		private int _state;
		private int _major;
		private int _minor;
		final HessianDebugState this$0;

		int depth()
		{
			return _next.depth() + 2;
		}

		State next(int ch)
		{
			switch (_state)
			{
			case 0: // '\0'
				_major = ch;
				_state = 1;
				return this;

			case 1: // '\001'
				_minor = ch;
				println(-2, (new StringBuilder()).append("Hessian ").append(_major).append(".").append(_minor).toString());
				return _next;
			}
			throw new IllegalStateException();
		}

		Hessian2State(State next)
		{
			this$0 = HessianDebugState.this;
			super(next);
		}
	}

	class CompactListState extends State
	{

		private static final int TYPE = 0;
		private static final int LENGTH = 1;
		private static final int VALUE = 2;
		private int _refId;
		private boolean _isTyped;
		private boolean _isLength;
		private int _state;
		private boolean _hasData;
		private int _length;
		private int _count;
		private int _valueDepth;
		final HessianDebugState this$0;

		boolean isShift(Object value)
		{
			return _state == 0 || _state == 1;
		}

		State shift(Object object)
		{
			if (_state == 0)
			{
				Object type = object;
				if (object instanceof Integer)
				{
					int index = ((Integer)object).intValue();
					if (index >= 0 && index < _typeDefList.size())
						type = _typeDefList.get(index);
					else
						type = (new StringBuilder()).append("type-unknown(").append(index).append(")").toString();
				} else
				if (object instanceof String)
					_typeDefList.add((String)object);
				printObject((new StringBuilder()).append("list ").append(type).append(" (#").append(_refId).append(")").toString());
				if (_isLength)
				{
					_state = 2;
					if (_length == 0)
						return _next;
				} else
				{
					_state = 1;
				}
				return this;
			}
			if (_state == 1)
			{
				_length = ((Integer)object).intValue();
				if (!_isTyped)
					printObject((new StringBuilder()).append("list (#").append(_refId).append(")").toString());
				_state = 2;
				if (_length == 0)
					return _next;
				else
					return this;
			} else
			{
				return this;
			}
		}

		int depth()
		{
			if (_state <= 1)
				return _next.depth();
			if (_state == 2)
				return _valueDepth;
			else
				return _next.depth() + 2;
		}

		State next(int ch)
		{
			switch (_state)
			{
			case 0: // '\0'
				return nextObject(ch);

			case 1: // '\001'
				return nextObject(ch);

			case 2: // '\002'
				if (_length <= _count)
				{
					return _next.next(ch);
				} else
				{
					_valueDepth = _next.depth() + 2;
					println();
					printObject((new StringBuilder()).append(_count++).append(": ").toString());
					_valueDepth = _column;
					_isObject = false;
					return nextObject(ch);
				}
			}
			throw new IllegalStateException();
		}

		CompactListState(State next, int refId, boolean isTyped)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_isTyped = isTyped;
			_refId = refId;
			if (isTyped)
				_state = 0;
			else
				_state = 1;
		}

		CompactListState(State next, int refId, boolean isTyped, int length)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_isTyped = isTyped;
			_refId = refId;
			_length = length;
			_isLength = true;
			if (isTyped)
			{
				_state = 0;
			} else
			{
				printObject((new StringBuilder()).append("list (#").append(_refId).append(")").toString());
				_state = 2;
			}
		}
	}

	class ListState extends State
	{

		private static final int TYPE = 0;
		private static final int LENGTH = 1;
		private static final int VALUE = 2;
		private int _refId;
		private int _state;
		private boolean _hasData;
		private int _count;
		private int _valueDepth;
		final HessianDebugState this$0;

		boolean isShift(Object value)
		{
			return _state == 0 || _state == 1;
		}

		State shift(Object object)
		{
			if (_state == 0)
			{
				Object type = object;
				if (type instanceof String)
					_typeDefList.add((String)type);
				else
				if (object instanceof Integer)
				{
					int index = ((Integer)object).intValue();
					if (index >= 0 && index < _typeDefList.size())
						type = _typeDefList.get(index);
					else
						type = (new StringBuilder()).append("type-unknown(").append(index).append(")").toString();
				}
				printObject((new StringBuilder()).append("list ").append(type).append("(#").append(_refId).append(")").toString());
				_state = 2;
				return this;
			}
			if (_state == 1)
			{
				_state = 2;
				return this;
			} else
			{
				return this;
			}
		}

		int depth()
		{
			if (_state <= 1)
				return _next.depth();
			if (_state == 2)
				return _valueDepth;
			else
				return _next.depth() + 2;
		}

		State next(int ch)
		{
			switch (_state)
			{
			case 0: // '\0'
				return nextObject(ch);

			case 2: // '\002'
				if (ch == 90)
				{
					if (_count > 0)
						println();
					return _next;
				} else
				{
					_valueDepth = _next.depth() + 2;
					println();
					printObject((new StringBuilder()).append(_count++).append(": ").toString());
					_valueDepth = _column;
					_isObject = false;
					return nextObject(ch);
				}
			}
			throw new IllegalStateException();
		}

		ListState(State next, int refId, boolean isType)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_refId = refId;
			if (isType)
			{
				_state = 0;
			} else
			{
				printObject((new StringBuilder()).append("list (#").append(_refId).append(")").toString());
				_state = 2;
			}
		}
	}

	class ListState1 extends State1
	{

		private static final int TYPE = 0;
		private static final int LENGTH = 1;
		private static final int VALUE = 2;
		private int _refId;
		private int _state;
		private boolean _hasData;
		private int _count;
		private int _valueDepth;
		final HessianDebugState this$0;

		boolean isShift(Object value)
		{
			return _state == 0 || _state == 1;
		}

		State shift(Object object)
		{
			if (_state == 0)
			{
				Object type = object;
				if (type instanceof String)
					_typeDefList.add((String)type);
				else
				if (object instanceof Integer)
				{
					int index = ((Integer)object).intValue();
					if (index >= 0 && index < _typeDefList.size())
						type = _typeDefList.get(index);
					else
						type = (new StringBuilder()).append("type-unknown(").append(index).append(")").toString();
				}
				printObject((new StringBuilder()).append("list ").append(type).append("(#").append(_refId).append(")").toString());
				_state = 2;
				return this;
			}
			if (_state == 1)
			{
				_state = 2;
				return this;
			} else
			{
				return this;
			}
		}

		int depth()
		{
			if (_state <= 1)
				return _next.depth();
			if (_state == 2)
				return _valueDepth;
			else
				return _next.depth() + 2;
		}

		State next(int ch)
		{
			switch (_state)
			{
			case 0: // '\0'
				if (ch == 122)
				{
					printObject((new StringBuilder()).append("list (#").append(_refId).append(")").toString());
					return _next;
				}
				if (ch == 116)
				{
					return new StringState(this, 't', true);
				} else
				{
					printObject((new StringBuilder()).append("list (#").append(_refId).append(")").toString());
					printObject((new StringBuilder()).append("  ").append(_count++).append(": ").toString());
					_valueDepth = _column;
					_isObject = false;
					_state = 2;
					return nextObject(ch);
				}

			case 2: // '\002'
				if (ch == 122)
				{
					if (_count > 0)
						println();
					return _next;
				} else
				{
					_valueDepth = _next.depth() + 2;
					println();
					printObject((new StringBuilder()).append(_count++).append(": ").toString());
					_valueDepth = _column;
					_isObject = false;
					return nextObject(ch);
				}
			}
			throw new IllegalStateException();
		}

		ListState1(State next, int refId)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_refId = refId;
			_state = 0;
		}
	}

	class ObjectState extends State
	{

		private static final int TYPE = 0;
		private static final int FIELD = 1;
		private int _refId;
		private int _state;
		private ObjectDef _def;
		private int _count;
		private int _fieldDepth;
		final HessianDebugState this$0;

		boolean isShift(Object value)
		{
			return _state == 0;
		}

		State shift(Object object)
		{
			if (_state == 0)
			{
				int def = ((Integer)object).intValue();
				_def = (ObjectDef)_objectDefList.get(def);
				println((new StringBuilder()).append("object ").append(_def.getType()).append(" (#").append(_refId).append(")").toString());
				_state = 1;
				if (_def.getFields().size() == 0)
					return _next;
			}
			return this;
		}

		int depth()
		{
			if (_state <= 0)
				return _next.depth();
			else
				return _fieldDepth;
		}

		State next(int ch)
		{
			switch (_state)
			{
			case 0: // '\0'
				return nextObject(ch);

			case 1: // '\001'
				if (_def.getFields().size() <= _count)
				{
					return _next.next(ch);
				} else
				{
					_fieldDepth = _next.depth() + 2;
					println();
					print((new StringBuilder()).append((String)_def.getFields().get(_count++)).append(": ").toString());
					_fieldDepth = _column;
					_isObject = false;
					return nextObject(ch);
				}
			}
			throw new IllegalStateException();
		}

		ObjectState(State next, int refId)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_refId = refId;
			_state = 0;
		}

		ObjectState(State next, int refId, int def)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_refId = refId;
			_state = 1;
			if (def < 0 || _objectDefList.size() <= def)
			{
				throw new IllegalStateException((new StringBuilder()).append(def).append(" is an unknown object type").toString());
			} else
			{
				_def = (ObjectDef)_objectDefList.get(def);
				println((new StringBuilder()).append("object ").append(_def.getType()).append(" (#").append(_refId).append(")").toString());
				return;
			}
		}
	}

	class ObjectDefState extends State
	{

		private static final int TYPE = 1;
		private static final int COUNT = 2;
		private static final int FIELD = 3;
		private static final int COMPLETE = 4;
		private int _refId;
		private int _state;
		private boolean _hasData;
		private int _count;
		private String _type;
		private ArrayList _fields;
		final HessianDebugState this$0;

		boolean isShift(Object value)
		{
			return true;
		}

		State shift(Object object)
		{
			if (_state == 1)
			{
				_type = (String)object;
				print((new StringBuilder()).append("/* defun ").append(_type).append(" [").toString());
				_objectDefList.add(new ObjectDef(_type, _fields));
				_state = 2;
			} else
			if (_state == 2)
			{
				_count = ((Integer)object).intValue();
				_state = 3;
			} else
			if (_state == 3)
			{
				String field = (String)object;
				_count--;
				_fields.add(field);
				if (_fields.size() == 1)
					print(field);
				else
					print((new StringBuilder()).append(", ").append(field).toString());
			} else
			{
				throw new UnsupportedOperationException();
			}
			return this;
		}

		int depth()
		{
			if (_state <= 1)
				return _next.depth();
			else
				return _next.depth() + 2;
		}

		State next(int ch)
		{
			switch (_state)
			{
			case 1: // '\001'
				return nextObject(ch);

			case 2: // '\002'
				return nextObject(ch);

			case 3: // '\003'
				if (_count == 0)
				{
					println("] */");
					_next.printIndent(0);
					return _next.nextObject(ch);
				} else
				{
					return nextObject(ch);
				}
			}
			throw new IllegalStateException();
		}

		ObjectDefState(State next)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_fields = new ArrayList();
			_state = 1;
		}
	}

	class MapState1 extends State1
	{

		private static final int TYPE = 0;
		private static final int KEY = 1;
		private static final int VALUE = 2;
		private int _refId;
		private int _state;
		private int _valueDepth;
		private boolean _hasData;
		final HessianDebugState this$0;

		boolean isShift(Object value)
		{
			return _state == 0;
		}

		State shift(Object type)
		{
			if (_state == 0)
			{
				if (type instanceof String)
					_typeDefList.add((String)type);
				else
				if (type instanceof Integer)
				{
					int iValue = ((Integer)type).intValue();
					if (iValue >= 0 && iValue < _typeDefList.size())
						type = _typeDefList.get(iValue);
				}
				printObject((new StringBuilder()).append("map ").append(type).append(" (#").append(_refId).append(")").toString());
				_state = 2;
				return this;
			} else
			{
				throw new IllegalStateException();
			}
		}

		int depth()
		{
			if (_state == 0)
				return _next.depth();
			if (_state == 1)
				return _next.depth() + 2;
			else
				return _valueDepth;
		}

		State next(int ch)
		{
			switch (_state)
			{
			case 0: // '\0'
				if (ch == 116)
					return new StringState(this, 't', true);
				if (ch == 122)
				{
					println((new StringBuilder()).append("map (#").append(_refId).append(")").toString());
					return _next;
				} else
				{
					println((new StringBuilder()).append("map (#").append(_refId).append(")").toString());
					_hasData = true;
					_state = 1;
					return nextObject(ch);
				}

			case 2: // '\002'
				if (ch == 122)
				{
					if (_hasData)
						println();
					return _next;
				}
				if (_hasData)
					println();
				_hasData = true;
				_state = 1;
				return nextObject(ch);

			case 1: // '\001'
				print(" => ");
				_isObject = false;
				_valueDepth = _column;
				_state = 2;
				return nextObject(ch);
			}
			throw new IllegalStateException();
		}

		MapState1(State next, int refId)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_refId = refId;
			_state = 0;
		}

		MapState1(State next, int refId, boolean isType)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_refId = refId;
			if (isType)
			{
				_state = 0;
			} else
			{
				printObject((new StringBuilder()).append("map (#").append(_refId).append(")").toString());
				_state = 2;
			}
		}
	}

	class MapState extends State
	{

		private static final int TYPE = 0;
		private static final int KEY = 1;
		private static final int VALUE = 2;
		private int _refId;
		private int _state;
		private int _valueDepth;
		private boolean _hasData;
		final HessianDebugState this$0;

		boolean isShift(Object value)
		{
			return _state == 0;
		}

		State shift(Object type)
		{
			if (_state == 0)
			{
				if (type instanceof String)
					_typeDefList.add((String)type);
				else
				if (type instanceof Integer)
				{
					int iValue = ((Integer)type).intValue();
					if (iValue >= 0 && iValue < _typeDefList.size())
						type = _typeDefList.get(iValue);
				}
				printObject((new StringBuilder()).append("map ").append(type).append(" (#").append(_refId).append(")").toString());
				_state = 2;
				return this;
			} else
			{
				throw new IllegalStateException();
			}
		}

		int depth()
		{
			if (_state == 0)
				return _next.depth();
			if (_state == 1)
				return _next.depth() + 2;
			else
				return _valueDepth;
		}

		State next(int ch)
		{
			switch (_state)
			{
			case 0: // '\0'
				return nextObject(ch);

			case 2: // '\002'
				if (ch == 90)
				{
					if (_hasData)
						println();
					return _next;
				}
				if (_hasData)
					println();
				_hasData = true;
				_state = 1;
				return nextObject(ch);

			case 1: // '\001'
				print(" => ");
				_isObject = false;
				_valueDepth = _column;
				_state = 2;
				return nextObject(ch);
			}
			throw new IllegalStateException();
		}

		MapState(State next, int refId)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_refId = refId;
			_state = 0;
		}

		MapState(State next, int refId, boolean isType)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_refId = refId;
			if (isType)
			{
				_state = 0;
			} else
			{
				printObject((new StringBuilder()).append("map (#").append(_refId).append(")").toString());
				_state = 2;
			}
		}
	}

	class BinaryState extends State
	{

		char _typeCode;
		int _totalLength;
		int _lengthIndex;
		int _length;
		boolean _isLastChunk;
		final HessianDebugState this$0;

		State next(int ch)
		{
			if (_lengthIndex < 2)
			{
				_length = 256 * _length + (ch & 0xff);
				if (++_lengthIndex == 2 && _length == 0 && _isLastChunk)
				{
					String value = (new StringBuilder()).append("binary(").append(_totalLength).append(")").toString();
					if (_next.isShift(value))
					{
						return _next.shift(value);
					} else
					{
						printObject(value);
						return _next;
					}
				} else
				{
					return this;
				}
			}
			if (_length == 0)
			{
				if (ch == 98)
				{
					_isLastChunk = false;
					_lengthIndex = 0;
					return this;
				}
				if (ch == 66)
				{
					_isLastChunk = true;
					_lengthIndex = 0;
					return this;
				}
				if (ch == 32)
				{
					String value = (new StringBuilder()).append("binary(").append(_totalLength).append(")").toString();
					if (_next.isShift(value))
					{
						return _next.shift(value);
					} else
					{
						printObject(value);
						return _next;
					}
				}
				if (32 <= ch && ch < 48)
				{
					_isLastChunk = true;
					_lengthIndex = 2;
					_length = (ch & 0xff) - 32;
					return this;
				} else
				{
					println((new StringBuilder()).append(String.valueOf((char)ch)).append(": unexpected character").toString());
					return _next;
				}
			}
			_length--;
			_totalLength++;
			if (_length == 0 && _isLastChunk)
			{
				String value = (new StringBuilder()).append("binary(").append(_totalLength).append(")").toString();
				if (_next.isShift(value))
				{
					return _next.shift(value);
				} else
				{
					printObject(value);
					return _next;
				}
			} else
			{
				return this;
			}
		}

		BinaryState(State next, char typeCode, boolean isLastChunk)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_typeCode = typeCode;
			_isLastChunk = isLastChunk;
		}

		BinaryState(State next, char typeCode, int length)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_typeCode = typeCode;
			_isLastChunk = true;
			_length = length;
			_lengthIndex = 2;
		}

		BinaryState(State next, char typeCode, int length, boolean isLastChunk)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_typeCode = typeCode;
			_isLastChunk = isLastChunk;
			_length = length;
			_lengthIndex = 1;
		}
	}

	class StringState extends State
	{

		private static final int TOP = 0;
		private static final int UTF_2_1 = 1;
		private static final int UTF_3_1 = 2;
		private static final int UTF_3_2 = 3;
		char _typeCode;
		StringBuilder _value;
		int _lengthIndex;
		int _length;
		boolean _isLastChunk;
		int _utfState;
		char _ch;
		final HessianDebugState this$0;

		State next(int ch)
		{
			if (_lengthIndex < 2)
			{
				_length = 256 * _length + (ch & 0xff);
				if (++_lengthIndex == 2 && _length == 0 && _isLastChunk)
				{
					if (_next.isShift(_value.toString()))
					{
						return _next.shift(_value.toString());
					} else
					{
						printObject((new StringBuilder()).append("\"").append(_value).append("\"").toString());
						return _next;
					}
				} else
				{
					return this;
				}
			}
			if (_length == 0)
			{
				if (ch == 115 || ch == 120)
				{
					_isLastChunk = false;
					_lengthIndex = 0;
					return this;
				}
				if (ch == 83 || ch == 88)
				{
					_isLastChunk = true;
					_lengthIndex = 0;
					return this;
				}
				if (ch == 0)
					if (_next.isShift(_value.toString()))
					{
						return _next.shift(_value.toString());
					} else
					{
						printObject((new StringBuilder()).append("\"").append(_value).append("\"").toString());
						return _next;
					}
				if (0 <= ch && ch < 32)
				{
					_isLastChunk = true;
					_lengthIndex = 2;
					_length = ch & 0xff;
					return this;
				}
				if (48 <= ch && ch < 52)
				{
					_isLastChunk = true;
					_lengthIndex = 1;
					_length = ch - 48;
					return this;
				} else
				{
					println((new StringBuilder()).append(String.valueOf((char)ch)).append(": unexpected character").toString());
					return _next;
				}
			}
			switch (_utfState)
			{
			case 0: // '\0'
				if (ch < 128)
				{
					_length--;
					_value.append((char)ch);
				} else
				if (ch < 224)
				{
					_ch = (char)((ch & 0x1f) << 6);
					_utfState = 1;
				} else
				{
					_ch = (char)((ch & 0xf) << 12);
					_utfState = 2;
				}
				break;

			case 1: // '\001'
			case 3: // '\003'
				_ch += ch & 0x3f;
				_value.append(_ch);
				_length--;
				_utfState = 0;
				break;

			case 2: // '\002'
				_ch += (char)((ch & 0x3f) << 6);
				_utfState = 3;
				break;
			}
			if (_length == 0 && _isLastChunk)
			{
				if (_next.isShift(_value.toString()))
				{
					return _next.shift(_value.toString());
				} else
				{
					printObject((new StringBuilder()).append("\"").append(_value).append("\"").toString());
					return _next;
				}
			} else
			{
				return this;
			}
		}

		StringState(State next, char typeCode, boolean isLastChunk)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_value = new StringBuilder();
			_typeCode = typeCode;
			_isLastChunk = isLastChunk;
		}

		StringState(State next, char typeCode, int length)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_value = new StringBuilder();
			_typeCode = typeCode;
			_isLastChunk = true;
			_length = length;
			_lengthIndex = 2;
		}

		StringState(State next, char typeCode, int length, boolean isLastChunk)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_value = new StringBuilder();
			_typeCode = typeCode;
			_isLastChunk = isLastChunk;
			_length = length;
			_lengthIndex = 1;
		}
	}

	class MillsState extends State
	{

		int _length;
		int _value;
		final HessianDebugState this$0;

		State next(int ch)
		{
			_value = 256 * _value + (ch & 0xff);
			if (++_length == 4)
			{
				Double value = Double.valueOf(0.001D * (double)_value);
				if (_next.isShift(value))
				{
					return _next.shift(value);
				} else
				{
					printObject(value.toString());
					return _next;
				}
			} else
			{
				return this;
			}
		}

		MillsState(State next)
		{
			this$0 = HessianDebugState.this;
			super(next);
		}
	}

	class DoubleState extends State
	{

		int _length;
		long _value;
		final HessianDebugState this$0;

		State next(int ch)
		{
			_value = 256L * _value + (long)(ch & 0xff);
			if (++_length == 8)
			{
				Double value = Double.valueOf(Double.longBitsToDouble(_value));
				if (_next.isShift(value))
				{
					return _next.shift(value);
				} else
				{
					printObject(value.toString());
					return _next;
				}
			} else
			{
				return this;
			}
		}

		DoubleState(State next)
		{
			this$0 = HessianDebugState.this;
			super(next);
		}
	}

	class DateState extends State
	{

		int _length;
		long _value;
		boolean _isMinute;
		final HessianDebugState this$0;

		State next(int ch)
		{
			_value = 256L * _value + (long)(ch & 0xff);
			if (++_length == 8)
			{
				Date value;
				if (_isMinute)
					value = new Date(_value * 60000L);
				else
					value = new Date(_value);
				if (_next.isShift(value))
				{
					return _next.shift(value);
				} else
				{
					printObject(value.toString());
					return _next;
				}
			} else
			{
				return this;
			}
		}

		DateState(State next)
		{
			this$0 = HessianDebugState.this;
			super(next);
		}

		DateState(State next, boolean isMinute)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_length = 4;
			_isMinute = isMinute;
		}
	}

	class RefState1 extends State
	{

		String _typeCode;
		final HessianDebugState this$0;

		boolean isShift(Object o)
		{
			return true;
		}

		State shift(Object o)
		{
			println((new StringBuilder()).append("ref #").append(o).toString());
			return _next;
		}

		State next(int ch)
		{
			return nextObject(ch);
		}

		RefState1(State next)
		{
			this$0 = HessianDebugState.this;
			super(next);
		}
	}

	class RefState extends State
	{

		String _typeCode;
		int _length;
		int _value;
		final HessianDebugState this$0;

		boolean isShift(Object o)
		{
			return true;
		}

		State shift(Object o)
		{
			println((new StringBuilder()).append("ref #").append(o).toString());
			return _next;
		}

		State next(int ch)
		{
			return nextObject(ch);
		}

		RefState(State next)
		{
			this$0 = HessianDebugState.this;
			super(next);
		}

		RefState(State next, String typeCode)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_typeCode = typeCode;
		}

		RefState(State next, String typeCode, int value, int length)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_typeCode = typeCode;
			_value = value;
			_length = length;
		}
	}

	class DoubleIntegerState extends State
	{

		int _length;
		int _value;
		boolean _isFirst;
		final HessianDebugState this$0;

		State next(int ch)
		{
			if (_isFirst)
				_value = (byte)ch;
			else
				_value = 256 * _value + (ch & 0xff);
			_isFirst = false;
			if (++_length == 4)
			{
				Double value = new Double(_value);
				if (_next.isShift(value))
				{
					return _next.shift(value);
				} else
				{
					printObject(value.toString());
					return _next;
				}
			} else
			{
				return this;
			}
		}

		DoubleIntegerState(State next, int length)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_isFirst = true;
			_length = length;
		}
	}

	class LongState extends State
	{

		String _typeCode;
		int _length;
		long _value;
		final HessianDebugState this$0;

		State next(int ch)
		{
			_value = 256L * _value + (long)(ch & 0xff);
			if (++_length == 8)
			{
				Long value = new Long(_value);
				if (_next.isShift(value))
				{
					return _next.shift(value);
				} else
				{
					printObject((new StringBuilder()).append(value.toString()).append("L").toString());
					return _next;
				}
			} else
			{
				return this;
			}
		}

		LongState(State next, String typeCode)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_typeCode = typeCode;
		}

		LongState(State next, String typeCode, long value, int length)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_typeCode = typeCode;
			_value = value;
			_length = length;
		}
	}

	class IntegerState extends State
	{

		String _typeCode;
		int _length;
		int _value;
		final HessianDebugState this$0;

		State next(int ch)
		{
			_value = 256 * _value + (ch & 0xff);
			if (++_length == 4)
			{
				Integer value = new Integer(_value);
				if (_next.isShift(value))
				{
					return _next.shift(value);
				} else
				{
					printObject(value.toString());
					return _next;
				}
			} else
			{
				return this;
			}
		}

		IntegerState(State next, String typeCode)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_typeCode = typeCode;
		}

		IntegerState(State next, String typeCode, int value, int length)
		{
			this$0 = HessianDebugState.this;
			super(next);
			_typeCode = typeCode;
			_value = value;
			_length = length;
		}
	}

	class Top2State extends State
	{

		final HessianDebugState this$0;

		State next(int ch)
		{
			println();
			if (ch == 82)
				return new Reply2State(this);
			if (ch == 70)
				return new Fault2State(this);
			if (ch == 67)
				return new Call2State(this);
			if (ch == 72)
				return new Hessian2State(this);
			if (ch == 114)
				return new ReplyState1(this);
			if (ch == 99)
				return new CallState1(this);
			else
				return nextObject(ch);
		}

		Top2State()
		{
			this$0 = HessianDebugState.this;
			super();
		}
	}

	class Top1State extends State1
	{

		final HessianDebugState this$0;

		State next(int ch)
		{
			println();
			if (ch == 114)
				return new ReplyState1(this);
			if (ch == 99)
				return new CallState1(this);
			else
				return nextObject(ch);
		}

		Top1State()
		{
			this$0 = HessianDebugState.this;
			super();
		}
	}

	class InitialState1 extends State1
	{

		final HessianDebugState this$0;

		State next(int ch)
		{
			return nextObject(ch);
		}

		InitialState1()
		{
			this$0 = HessianDebugState.this;
			super();
		}
	}

	class InitialState extends State
	{

		final HessianDebugState this$0;

		State next(int ch)
		{
			return nextObject(ch);
		}

		InitialState()
		{
			this$0 = HessianDebugState.this;
			super();
		}
	}

	abstract class State1 extends State
	{

		final HessianDebugState this$0;

		protected State nextObject(int ch)
		{
			switch (ch)
			{
			case -1: 
				println();
				return this;

			case 78: // 'N'
				if (isShift(null))
				{
					return shift(null);
				} else
				{
					printObject("null");
					return this;
				}

			case 84: // 'T'
				if (isShift(Boolean.TRUE))
				{
					return shift(Boolean.TRUE);
				} else
				{
					printObject("true");
					return this;
				}

			case 70: // 'F'
				if (isShift(Boolean.FALSE))
				{
					return shift(Boolean.FALSE);
				} else
				{
					printObject("false");
					return this;
				}

			case 73: // 'I'
				return new IntegerState(this, "int");

			case 76: // 'L'
				return new LongState(this, "long");

			case 68: // 'D'
				return new DoubleState(this);

			case 81: // 'Q'
				return new RefState(this);

			case 100: // 'd'
				return new DateState(this);

			case 115: // 's'
				return new StringState(this, 'S', false);

			case 83: // 'S'
				return new StringState(this, 'S', true);

			case 98: // 'b'
				return new BinaryState(this, 'B', false);

			case 66: // 'B'
				return new BinaryState(this, 'B', true);

			case 77: // 'M'
				return new MapState1(this, _refId++);

			case 86: // 'V'
				return new ListState1(this, _refId++);

			case 82: // 'R'
				return new IntegerState(new RefState1(this), "ref");
			}
			printObject((new StringBuilder()).append("x").append(String.format("%02x", new Object[] {
				Integer.valueOf(ch)
			})).toString());
			return this;
		}

		State1()
		{
			this$0 = HessianDebugState.this;
			super();
		}

		State1(State next)
		{
			this$0 = HessianDebugState.this;
			super(next);
		}
	}

	abstract class State
	{

		State _next;
		final HessianDebugState this$0;

		abstract State next(int i);

		boolean isShift(Object value)
		{
			return false;
		}

		State shift(Object value)
		{
			return this;
		}

		int depth()
		{
			if (_next != null)
				return _next.depth();
			else
				return getDepth();
		}

		void printIndent(int depth)
		{
			if (_isNewline)
			{
				for (int i = _column; i < depth() + depth; i++)
				{
					_dbg.print(" ");
					_column++;
				}

			}
		}

		void print(String string)
		{
			print(0, string);
		}

		void print(int depth, String string)
		{
			printIndent(depth);
			_dbg.print(string);
			_isNewline = false;
			_isObject = false;
			int p = string.lastIndexOf('\n');
			if (p > 0)
				_column = string.length() - p - 1;
			else
				_column+= = string.length();
		}

		void println(String string)
		{
			println(0, string);
		}

		void println(int depth, String string)
		{
			printIndent(depth);
			_dbg.println(string);
			_dbg.flush();
			_isNewline = true;
			_isObject = false;
			_column = 0;
		}

		void println()
		{
			if (!_isNewline)
			{
				_dbg.println();
				_dbg.flush();
			}
			_isNewline = true;
			_isObject = false;
			_column = 0;
		}

		void printObject(String string)
		{
			if (_isObject)
				println();
			printIndent(0);
			_dbg.print(string);
			_dbg.flush();
			_column+= = string.length();
			_isNewline = false;
			_isObject = true;
		}

		protected State nextObject(int ch)
		{
			switch (ch)
			{
			case -1: 
			{
				println();
				return this;
			}

			case 78: // 'N'
			{
				if (isShift(null))
				{
					return shift(null);
				} else
				{
					printObject("null");
					return this;
				}
			}

			case 84: // 'T'
			{
				if (isShift(Boolean.TRUE))
				{
					return shift(Boolean.TRUE);
				} else
				{
					printObject("true");
					return this;
				}
			}

			case 70: // 'F'
			{
				if (isShift(Boolean.FALSE))
				{
					return shift(Boolean.FALSE);
				} else
				{
					printObject("false");
					return this;
				}
			}

			case 128: 
			case 129: 
			case 130: 
			case 131: 
			case 132: 
			case 133: 
			case 134: 
			case 135: 
			case 136: 
			case 137: 
			case 138: 
			case 139: 
			case 140: 
			case 141: 
			case 142: 
			case 143: 
			case 144: 
			case 145: 
			case 146: 
			case 147: 
			case 148: 
			case 149: 
			case 150: 
			case 151: 
			case 152: 
			case 153: 
			case 154: 
			case 155: 
			case 156: 
			case 157: 
			case 158: 
			case 159: 
			case 160: 
			case 161: 
			case 162: 
			case 163: 
			case 164: 
			case 165: 
			case 166: 
			case 167: 
			case 168: 
			case 169: 
			case 170: 
			case 171: 
			case 172: 
			case 173: 
			case 174: 
			case 175: 
			case 176: 
			case 177: 
			case 178: 
			case 179: 
			case 180: 
			case 181: 
			case 182: 
			case 183: 
			case 184: 
			case 185: 
			case 186: 
			case 187: 
			case 188: 
			case 189: 
			case 190: 
			case 191: 
			{
				Integer value = new Integer(ch - 144);
				if (isShift(value))
				{
					return shift(value);
				} else
				{
					printObject(value.toString());
					return this;
				}
			}

			case 192: 
			case 193: 
			case 194: 
			case 195: 
			case 196: 
			case 197: 
			case 198: 
			case 199: 
			case 200: 
			case 201: 
			case 202: 
			case 203: 
			case 204: 
			case 205: 
			case 206: 
			case 207: 
			{
				return new IntegerState(this, "int", ch - 200, 3);
			}

			case 208: 
			case 209: 
			case 210: 
			case 211: 
			case 212: 
			case 213: 
			case 214: 
			case 215: 
			{
				return new IntegerState(this, "int", ch - 212, 2);
			}

			case 73: // 'I'
			{
				return new IntegerState(this, "int");
			}

			case 216: 
			case 217: 
			case 218: 
			case 219: 
			case 220: 
			case 221: 
			case 222: 
			case 223: 
			case 224: 
			case 225: 
			case 226: 
			case 227: 
			case 228: 
			case 229: 
			case 230: 
			case 231: 
			case 232: 
			case 233: 
			case 234: 
			case 235: 
			case 236: 
			case 237: 
			case 238: 
			case 239: 
			{
				Long value = new Long(ch - 224);
				if (isShift(value))
				{
					return shift(value);
				} else
				{
					printObject((new StringBuilder()).append(value.toString()).append("L").toString());
					return this;
				}
			}

			case 240: 
			case 241: 
			case 242: 
			case 243: 
			case 244: 
			case 245: 
			case 246: 
			case 247: 
			case 248: 
			case 249: 
			case 250: 
			case 251: 
			case 252: 
			case 253: 
			case 254: 
			case 255: 
			{
				return new LongState(this, "long", ch - 248, 7);
			}

			case 56: // '8'
			case 57: // '9'
			case 58: // ':'
			case 59: // ';'
			case 60: // '<'
			case 61: // '='
			case 62: // '>'
			case 63: // '?'
			{
				return new LongState(this, "long", ch - 60, 6);
			}

			case 89: // 'Y'
			{
				return new LongState(this, "long", 0L, 4);
			}

			case 76: // 'L'
			{
				return new LongState(this, "long");
			}

			case 91: // '['
			case 92: // '\\'
			{
				Double value = new Double(ch - 91);
				if (isShift(value))
				{
					return shift(value);
				} else
				{
					printObject(value.toString());
					return this;
				}
			}

			case 93: // ']'
			{
				return new DoubleIntegerState(this, 3);
			}

			case 94: // '^'
			{
				return new DoubleIntegerState(this, 2);
			}

			case 95: // '_'
			{
				return new MillsState(this);
			}

			case 68: // 'D'
			{
				return new DoubleState(this);
			}

			case 81: // 'Q'
			{
				return new RefState(this);
			}

			case 74: // 'J'
			{
				return new DateState(this);
			}

			case 75: // 'K'
			{
				return new DateState(this, true);
			}

			case 0: // '\0'
			{
				String value = "\"\"";
				if (isShift(value))
				{
					return shift(value);
				} else
				{
					printObject(value.toString());
					return this;
				}
			}

			case 1: // '\001'
			case 2: // '\002'
			case 3: // '\003'
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
			case 16: // '\020'
			case 17: // '\021'
			case 18: // '\022'
			case 19: // '\023'
			case 20: // '\024'
			case 21: // '\025'
			case 22: // '\026'
			case 23: // '\027'
			case 24: // '\030'
			case 25: // '\031'
			case 26: // '\032'
			case 27: // '\033'
			case 28: // '\034'
			case 29: // '\035'
			case 30: // '\036'
			case 31: // '\037'
			{
				return new StringState(this, 'S', ch);
			}

			case 48: // '0'
			case 49: // '1'
			case 50: // '2'
			case 51: // '3'
			{
				return new StringState(this, 'S', ch - 48, true);
			}

			case 82: // 'R'
			{
				return new StringState(this, 'S', false);
			}

			case 83: // 'S'
			{
				return new StringState(this, 'S', true);
			}

			case 32: // ' '
			{
				String value = "binary(0)";
				if (isShift(value))
				{
					return shift(value);
				} else
				{
					printObject(value.toString());
					return this;
				}
			}

			case 33: // '!'
			case 34: // '"'
			case 35: // '#'
			case 36: // '$'
			case 37: // '%'
			case 38: // '&'
			case 39: // '\''
			case 40: // '('
			case 41: // ')'
			case 42: // '*'
			case 43: // '+'
			case 44: // ','
			case 45: // '-'
			case 46: // '.'
			case 47: // '/'
			{
				return new BinaryState(this, 'B', ch - 32);
			}

			case 52: // '4'
			case 53: // '5'
			case 54: // '6'
			case 55: // '7'
			{
				return new BinaryState(this, 'B', ch - 52, true);
			}

			case 65: // 'A'
			{
				return new BinaryState(this, 'B', false);
			}

			case 66: // 'B'
			{
				return new BinaryState(this, 'B', true);
			}

			case 77: // 'M'
			{
				return new MapState(this, _refId++);
			}

			case 72: // 'H'
			{
				return new MapState(this, _refId++, false);
			}

			case 85: // 'U'
			{
				return new ListState(this, _refId++, true);
			}

			case 87: // 'W'
			{
				return new ListState(this, _refId++, false);
			}

			case 86: // 'V'
			{
				return new CompactListState(this, _refId++, true);
			}

			case 88: // 'X'
			{
				return new CompactListState(this, _refId++, false);
			}

			case 112: // 'p'
			case 113: // 'q'
			case 114: // 'r'
			case 115: // 's'
			case 116: // 't'
			case 117: // 'u'
			case 118: // 'v'
			case 119: // 'w'
			{
				return new CompactListState(this, _refId++, true, ch - 112);
			}

			case 120: // 'x'
			case 121: // 'y'
			case 122: // 'z'
			case 123: // '{'
			case 124: // '|'
			case 125: // '}'
			case 126: // '~'
			case 127: // '\177'
			{
				return new CompactListState(this, _refId++, false, ch - 120);
			}

			case 67: // 'C'
			{
				return new ObjectDefState(this);
			}

			case 96: // '`'
			case 97: // 'a'
			case 98: // 'b'
			case 99: // 'c'
			case 100: // 'd'
			case 101: // 'e'
			case 102: // 'f'
			case 103: // 'g'
			case 104: // 'h'
			case 105: // 'i'
			case 106: // 'j'
			case 107: // 'k'
			case 108: // 'l'
			case 109: // 'm'
			case 110: // 'n'
			case 111: // 'o'
			{
				return new ObjectState(this, _refId++, ch - 96);
			}

			case 79: // 'O'
			{
				return new ObjectState(this, _refId++);
			}

			case 64: // '@'
			case 69: // 'E'
			case 71: // 'G'
			case 80: // 'P'
			case 90: // 'Z'
			default:
			{
				return this;
			}
			}
		}

		State()
		{
			this$0 = HessianDebugState.this;
			super();
		}

		State(State next)
		{
			this$0 = HessianDebugState.this;
			super();
			_next = next;
		}
	}


	private PrintWriter _dbg;
	private State _state;
	private ArrayList _stateStack;
	private ArrayList _objectDefList;
	private ArrayList _typeDefList;
	private int _refId;
	private boolean _isNewline;
	private boolean _isObject;
	private int _column;
	private int _depth;

	public HessianDebugState(PrintWriter dbg)
	{
		_stateStack = new ArrayList();
		_objectDefList = new ArrayList();
		_typeDefList = new ArrayList();
		_isNewline = true;
		_isObject = false;
		_depth = 0;
		_dbg = dbg;
		_state = new InitialState();
	}

	public void startTop2()
	{
		_state = new Top2State();
	}

	public void startData1()
	{
		_state = new InitialState1();
	}

	public void startStreaming()
	{
		_state = new StreamingState(new InitialState(), false);
	}

	public void next(int ch)
		throws IOException
	{
		_state = _state.next(ch);
	}

	void pushStack(State state)
	{
		_stateStack.add(state);
	}

	State popStack()
	{
		return (State)_stateStack.remove(_stateStack.size() - 1);
	}

	public void setDepth(int depth)
	{
		_depth = depth;
	}

	public int getDepth()
	{
		return _depth;
	}

	void println()
	{
		if (!_isNewline)
		{
			_dbg.println();
			_dbg.flush();
		}
		_isNewline = true;
		_column = 0;
	}

	static boolean isString(int ch)
	{
		switch (ch)
		{
		case 0: // '\0'
		case 1: // '\001'
		case 2: // '\002'
		case 3: // '\003'
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
		case 16: // '\020'
		case 17: // '\021'
		case 18: // '\022'
		case 19: // '\023'
		case 20: // '\024'
		case 21: // '\025'
		case 22: // '\026'
		case 23: // '\027'
		case 24: // '\030'
		case 25: // '\031'
		case 26: // '\032'
		case 27: // '\033'
		case 28: // '\034'
		case 29: // '\035'
		case 30: // '\036'
		case 31: // '\037'
		case 48: // '0'
		case 49: // '1'
		case 50: // '2'
		case 51: // '3'
		case 82: // 'R'
		case 83: // 'S'
			return true;

		case 32: // ' '
		case 33: // '!'
		case 34: // '"'
		case 35: // '#'
		case 36: // '$'
		case 37: // '%'
		case 38: // '&'
		case 39: // '\''
		case 40: // '('
		case 41: // ')'
		case 42: // '*'
		case 43: // '+'
		case 44: // ','
		case 45: // '-'
		case 46: // '.'
		case 47: // '/'
		case 52: // '4'
		case 53: // '5'
		case 54: // '6'
		case 55: // '7'
		case 56: // '8'
		case 57: // '9'
		case 58: // ':'
		case 59: // ';'
		case 60: // '<'
		case 61: // '='
		case 62: // '>'
		case 63: // '?'
		case 64: // '@'
		case 65: // 'A'
		case 66: // 'B'
		case 67: // 'C'
		case 68: // 'D'
		case 69: // 'E'
		case 70: // 'F'
		case 71: // 'G'
		case 72: // 'H'
		case 73: // 'I'
		case 74: // 'J'
		case 75: // 'K'
		case 76: // 'L'
		case 77: // 'M'
		case 78: // 'N'
		case 79: // 'O'
		case 80: // 'P'
		case 81: // 'Q'
		default:
			return false;
		}
	}

	static boolean isInteger(int ch)
	{
		switch (ch)
		{
		case 73: // 'I'
		case 128: 
		case 129: 
		case 130: 
		case 131: 
		case 132: 
		case 133: 
		case 134: 
		case 135: 
		case 136: 
		case 137: 
		case 138: 
		case 139: 
		case 140: 
		case 141: 
		case 142: 
		case 143: 
		case 144: 
		case 145: 
		case 146: 
		case 147: 
		case 148: 
		case 149: 
		case 150: 
		case 151: 
		case 152: 
		case 153: 
		case 154: 
		case 155: 
		case 156: 
		case 157: 
		case 158: 
		case 159: 
		case 160: 
		case 161: 
		case 162: 
		case 163: 
		case 164: 
		case 165: 
		case 166: 
		case 167: 
		case 168: 
		case 169: 
		case 170: 
		case 171: 
		case 172: 
		case 173: 
		case 174: 
		case 175: 
		case 176: 
		case 177: 
		case 178: 
		case 179: 
		case 180: 
		case 181: 
		case 182: 
		case 183: 
		case 184: 
		case 185: 
		case 186: 
		case 187: 
		case 188: 
		case 189: 
		case 190: 
		case 191: 
		case 192: 
		case 193: 
		case 194: 
		case 195: 
		case 196: 
		case 197: 
		case 198: 
		case 199: 
		case 200: 
		case 201: 
		case 202: 
		case 203: 
		case 204: 
		case 205: 
		case 206: 
		case 207: 
		case 208: 
		case 209: 
		case 210: 
		case 211: 
		case 212: 
		case 213: 
		case 214: 
		case 215: 
			return true;

		case 74: // 'J'
		case 75: // 'K'
		case 76: // 'L'
		case 77: // 'M'
		case 78: // 'N'
		case 79: // 'O'
		case 80: // 'P'
		case 81: // 'Q'
		case 82: // 'R'
		case 83: // 'S'
		case 84: // 'T'
		case 85: // 'U'
		case 86: // 'V'
		case 87: // 'W'
		case 88: // 'X'
		case 89: // 'Y'
		case 90: // 'Z'
		case 91: // '['
		case 92: // '\\'
		case 93: // ']'
		case 94: // '^'
		case 95: // '_'
		case 96: // '`'
		case 97: // 'a'
		case 98: // 'b'
		case 99: // 'c'
		case 100: // 'd'
		case 101: // 'e'
		case 102: // 'f'
		case 103: // 'g'
		case 104: // 'h'
		case 105: // 'i'
		case 106: // 'j'
		case 107: // 'k'
		case 108: // 'l'
		case 109: // 'm'
		case 110: // 'n'
		case 111: // 'o'
		case 112: // 'p'
		case 113: // 'q'
		case 114: // 'r'
		case 115: // 's'
		case 116: // 't'
		case 117: // 'u'
		case 118: // 'v'
		case 119: // 'w'
		case 120: // 'x'
		case 121: // 'y'
		case 122: // 'z'
		case 123: // '{'
		case 124: // '|'
		case 125: // '}'
		case 126: // '~'
		case 127: // '\177'
		default:
			return false;
		}
	}













}
