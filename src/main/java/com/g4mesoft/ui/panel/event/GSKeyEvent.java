package com.g4mesoft.ui.panel.event;

import org.lwjgl.input.Keyboard;

public final class GSKeyEvent extends GSEvent {

	public static final int KEY_PRESSED_TYPE  = 200;
	public static final int KEY_REPEATED_TYPE = 201;
	public static final int KEY_RELEASED_TYPE = 202;
	public static final int KEY_TYPED_TYPE    = 203;
	
	public static final int FIRST_TYPE = KEY_PRESSED_TYPE;
	public static final int LAST_TYPE  = KEY_TYPED_TYPE;
	
	public static final int UNKNOWN_KEY = Keyboard.KEY_NONE;
	public static final int UNKNOWN_CODE_POINT = '\0'; /* NULL char */
	
	/** Printable keys. */
	public static final int KEY_SPACE         = Keyboard.KEY_SPACE;
	public static final int KEY_APOSTROPHE    = Keyboard.KEY_APOSTROPHE;
	public static final int KEY_COMMA         = Keyboard.KEY_COMMA;
	public static final int KEY_MINUS         = Keyboard.KEY_MINUS;
	public static final int KEY_PERIOD        = Keyboard.KEY_PERIOD;
	public static final int KEY_SLASH         = Keyboard.KEY_SLASH;
	public static final int KEY_0             = Keyboard.KEY_0;
	public static final int KEY_1             = Keyboard.KEY_1;
	public static final int KEY_2             = Keyboard.KEY_2;
	public static final int KEY_3             = Keyboard.KEY_3;
	public static final int KEY_4             = Keyboard.KEY_4;
	public static final int KEY_5             = Keyboard.KEY_5;
	public static final int KEY_6             = Keyboard.KEY_6;
	public static final int KEY_7             = Keyboard.KEY_7;
	public static final int KEY_8             = Keyboard.KEY_8;
	public static final int KEY_9             = Keyboard.KEY_9;
	public static final int KEY_SEMICOLON     = Keyboard.KEY_SEMICOLON;
	public static final int KEY_EQUAL         = Keyboard.KEY_EQUALS;
	public static final int KEY_A             = Keyboard.KEY_A;
	public static final int KEY_B             = Keyboard.KEY_B;
	public static final int KEY_C             = Keyboard.KEY_C;
	public static final int KEY_D             = Keyboard.KEY_D;
	public static final int KEY_E             = Keyboard.KEY_E;
	public static final int KEY_F             = Keyboard.KEY_F;
	public static final int KEY_G             = Keyboard.KEY_G;
	public static final int KEY_H             = Keyboard.KEY_H;
	public static final int KEY_I             = Keyboard.KEY_I;
	public static final int KEY_J             = Keyboard.KEY_J;
	public static final int KEY_K             = Keyboard.KEY_K;
	public static final int KEY_L             = Keyboard.KEY_L;
	public static final int KEY_M             = Keyboard.KEY_M;
	public static final int KEY_N             = Keyboard.KEY_N;
	public static final int KEY_O             = Keyboard.KEY_O;
	public static final int KEY_P             = Keyboard.KEY_P;
	public static final int KEY_Q             = Keyboard.KEY_Q;
	public static final int KEY_R             = Keyboard.KEY_R;
	public static final int KEY_S             = Keyboard.KEY_S;
	public static final int KEY_T             = Keyboard.KEY_T;
	public static final int KEY_U             = Keyboard.KEY_U;
	public static final int KEY_V             = Keyboard.KEY_V;
	public static final int KEY_W             = Keyboard.KEY_W;
	public static final int KEY_X             = Keyboard.KEY_X;
	public static final int KEY_Y             = Keyboard.KEY_Y;
	public static final int KEY_Z             = Keyboard.KEY_Z;
	public static final int KEY_LEFT_BRACKET  = Keyboard.KEY_LBRACKET;
	public static final int KEY_BACKSLASH     = Keyboard.KEY_BACKSLASH;
	public static final int KEY_RIGHT_BRACKET = Keyboard.KEY_RBRACKET;
	public static final int KEY_GRAVE_ACCENT  = Keyboard.KEY_GRAVE;
	
	/** Function keys. */
	public static final int KEY_ESCAPE        = Keyboard.KEY_ESCAPE;
	public static final int KEY_ENTER         = Keyboard.KEY_RETURN;
	public static final int KEY_TAB           = Keyboard.KEY_TAB;
	public static final int KEY_BACKSPACE     = Keyboard.KEY_BACK;
	public static final int KEY_INSERT        = Keyboard.KEY_INSERT;
	public static final int KEY_DELETE        = Keyboard.KEY_DELETE;
	public static final int KEY_RIGHT         = Keyboard.KEY_RIGHT;
	public static final int KEY_LEFT          = Keyboard.KEY_LEFT;
	public static final int KEY_DOWN          = Keyboard.KEY_DOWN;
	public static final int KEY_UP            = Keyboard.KEY_UP;
	public static final int KEY_PAGE_UP       = Keyboard.KEY_PRIOR;
	public static final int KEY_PAGE_DOWN     = Keyboard.KEY_NEXT;
	public static final int KEY_HOME          = Keyboard.KEY_HOME;
	public static final int KEY_END           = Keyboard.KEY_END;
	public static final int KEY_CAPS_LOCK     = Keyboard.KEY_CAPITAL;
	public static final int KEY_SCROLL_LOCK   = Keyboard.KEY_SCROLL;
	public static final int KEY_NUM_LOCK      = Keyboard.KEY_NUMLOCK;
	public static final int KEY_PRINT_SCREEN  = Keyboard.KEY_SYSRQ;
	public static final int KEY_PAUSE         = Keyboard.KEY_PAUSE;
	public static final int KEY_F1            = Keyboard.KEY_F1;
	public static final int KEY_F2            = Keyboard.KEY_F2;
	public static final int KEY_F3            = Keyboard.KEY_F3;
	public static final int KEY_F4            = Keyboard.KEY_F4;
	public static final int KEY_F5            = Keyboard.KEY_F5;
	public static final int KEY_F6            = Keyboard.KEY_F6;
	public static final int KEY_F7            = Keyboard.KEY_F7;
	public static final int KEY_F8            = Keyboard.KEY_F8;
	public static final int KEY_F9            = Keyboard.KEY_F9;
	public static final int KEY_F10           = Keyboard.KEY_F10;
	public static final int KEY_F11           = Keyboard.KEY_F11;
	public static final int KEY_F12           = Keyboard.KEY_F12;
	public static final int KEY_F13           = Keyboard.KEY_F13;
	public static final int KEY_F14           = Keyboard.KEY_F14;
	public static final int KEY_F15           = Keyboard.KEY_F15;
	public static final int KEY_F16           = Keyboard.KEY_F16;
	public static final int KEY_F17           = Keyboard.KEY_F17;
	public static final int KEY_F18           = Keyboard.KEY_F18;
	public static final int KEY_F19           = Keyboard.KEY_F19;
	public static final int KEY_KP_0          = Keyboard.KEY_NUMPAD0;
	public static final int KEY_KP_1          = Keyboard.KEY_NUMPAD1;
	public static final int KEY_KP_2          = Keyboard.KEY_NUMPAD2;
	public static final int KEY_KP_3          = Keyboard.KEY_NUMPAD3;
	public static final int KEY_KP_4          = Keyboard.KEY_NUMPAD4;
	public static final int KEY_KP_5          = Keyboard.KEY_NUMPAD5;
	public static final int KEY_KP_6          = Keyboard.KEY_NUMPAD6;
	public static final int KEY_KP_7          = Keyboard.KEY_NUMPAD7;
	public static final int KEY_KP_8          = Keyboard.KEY_NUMPAD8;
	public static final int KEY_KP_9          = Keyboard.KEY_NUMPAD9;
	public static final int KEY_KP_DECIMAL    = Keyboard.KEY_DECIMAL;
	public static final int KEY_KP_DIVIDE     = Keyboard.KEY_DIVIDE;
	public static final int KEY_KP_MULTIPLY   = Keyboard.KEY_MULTIPLY;
	public static final int KEY_KP_SUBTRACT   = Keyboard.KEY_SUBTRACT;
	public static final int KEY_KP_ADD        = Keyboard.KEY_ADD;
	public static final int KEY_KP_ENTER      = Keyboard.KEY_NUMPADENTER;
	public static final int KEY_KP_EQUAL      = Keyboard.KEY_NUMPADEQUALS;
	public static final int KEY_LEFT_SHIFT    = Keyboard.KEY_LSHIFT;
	public static final int KEY_LEFT_CONTROL  = Keyboard.KEY_LCONTROL;
	public static final int KEY_LEFT_ALT      = Keyboard.KEY_LMENU;
	public static final int KEY_LEFT_SUPER    = Keyboard.KEY_LMETA;
	public static final int KEY_RIGHT_SHIFT   = Keyboard.KEY_RSHIFT;
	public static final int KEY_RIGHT_CONTROL = Keyboard.KEY_RCONTROL;
	public static final int KEY_RIGHT_ALT     = Keyboard.KEY_RMENU;
	public static final int KEY_RIGHT_SUPER   = Keyboard.KEY_RMETA;
	public static final int KEY_MENU          = Keyboard.KEY_APPS;
	public static final int KEY_LAST          = Keyboard.KEYBOARD_SIZE;

	public static final int UNKNOWN_SCANCODE  = -1;
	
	private final int type;
	
	/* The Key Code for PRESSED, REPEATED, and RELEASED. The CodePoint for TYPED. */
	private final int keyCode;
	/* The Scan Code used when keyCode is UNKNOWN_KEY for PRESSED, REPEATED, and RELEASED. */
	private final int scanCode;
	/* The Modifiers for PRESSED, REPREATED, and RELEASED. NO_MODIFIERS for TYPED. */
	private final int modifiers;
	
	public GSKeyEvent(int type, int keyCode, int scanCode, int modifiers) {
		if (type < FIRST_TYPE || type > LAST_TYPE)
			type = UNKNOWN_TYPE;
		
		this.type = type;
		
		this.keyCode = keyCode;
		this.scanCode = scanCode;
		this.modifiers = modifiers & ALL_MODIFIERS;
	}
	
	@Override
	public int getType() {
		return type;
	}
	
	public int getKeyCode() {
		return (type != KEY_TYPED_TYPE) ? keyCode : UNKNOWN_KEY;
	}

	public int getScanCode() {
		return (type != KEY_TYPED_TYPE) ? scanCode : UNKNOWN_SCANCODE;
	}

	public int getCodePoint() {
		return (type == KEY_TYPED_TYPE) ? keyCode : UNKNOWN_CODE_POINT;
	}
	
	public int getModifiers() {
		return modifiers;
	}

	public boolean isModifierHeld(int modifier) {
		return (modifiers & modifier) == modifier;
	}

	public boolean isAnyModifierHeld(int modifier) {
		return (modifiers & modifier) != NO_MODIFIERS;
	}
	
	public boolean isRepeating() {
		return (type == KEY_REPEATED_TYPE);
	}
	
	public boolean isPrintableKey() {
		if (type == KEY_TYPED_TYPE)
			return true;
		
		if (keyCode == UNKNOWN_KEY)
			return false;

		switch (keyCode) {
		// Space and printable ASCII symbols
		case KEY_SPACE:
		case KEY_APOSTROPHE:
		case KEY_COMMA:
		case KEY_MINUS:
		case KEY_PERIOD:
		case KEY_SLASH:
		case KEY_SEMICOLON:
		case KEY_EQUAL:
		case KEY_LEFT_BRACKET:
		case KEY_BACKSLASH:
		case KEY_RIGHT_BRACKET:
		case KEY_GRAVE_ACCENT:
		
		// Number keys (top row)
		case KEY_0:
		case KEY_1:
		case KEY_2:
		case KEY_3:
		case KEY_4:
		case KEY_5:
		case KEY_6:
		case KEY_7:
		case KEY_8:
		case KEY_9:
		
		// Alphabet A-Z
		case KEY_A:
		case KEY_B:
		case KEY_C:
		case KEY_D:
		case KEY_E:
		case KEY_F:
		case KEY_G:
		case KEY_H:
		case KEY_I:
		case KEY_J:
		case KEY_K:
		case KEY_L:
		case KEY_M:
		case KEY_N:
		case KEY_O:
		case KEY_P:
		case KEY_Q:
		case KEY_R:
		case KEY_S:
		case KEY_T:
		case KEY_U:
		case KEY_V:
		case KEY_W:
		case KEY_X:
		case KEY_Y:
		case KEY_Z:

		// Keypad digits and symbols
		case KEY_KP_0:
		case KEY_KP_1:
		case KEY_KP_2:
		case KEY_KP_3:
		case KEY_KP_4:
		case KEY_KP_5:
		case KEY_KP_6:
		case KEY_KP_7:
		case KEY_KP_8:
		case KEY_KP_9:
		case KEY_KP_DECIMAL:
		case KEY_KP_DIVIDE:
		case KEY_KP_MULTIPLY:
		case KEY_KP_SUBTRACT:
		case KEY_KP_ADD:
		case KEY_KP_EQUAL:
			return true;

		default:
			// Assume not printable.
			return false;
		}
	}
	
	public static GSKeyEvent createKeyPressedEvent(int keyCode, int scanCode, int modifiers) {
		return new GSKeyEvent(KEY_PRESSED_TYPE, keyCode, scanCode, modifiers);
	}

	public static GSKeyEvent createKeyRepeatedEvent(int keyCode, int scanCode, int modifiers) {
		return new GSKeyEvent(KEY_REPEATED_TYPE, keyCode, scanCode, modifiers);
	}

	public static GSKeyEvent createKeyReleasedEvent(int keyCode, int scanCode, int modifiers) {
		return new GSKeyEvent(KEY_RELEASED_TYPE, keyCode, scanCode, modifiers);
	}

	public static GSKeyEvent createKeyTypedEvent(int codePoint) {
		return new GSKeyEvent(KEY_TYPED_TYPE, codePoint, UNKNOWN_SCANCODE, NO_MODIFIERS);
	}
}
