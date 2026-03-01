package net.minecraft.client.util;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.util.LazyValue;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWDropCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;

public class InputMappings {
    @Nullable
    private static final MethodHandle GLFW_RAW_MOUSE_SUPPORTED;
    private static final int GLFW_RAW_MOUSE;
    public static final Input INPUT_INVALID;

    public static Input getInputByCode(int keyCode, int scanCode) {
        return keyCode == -1 ? Type.SCANCODE.getOrMakeInput(scanCode) : Type.KEYSYM.getOrMakeInput(keyCode);
    }

    public static Input getInputByName(String name) {
        if (Input.REGISTRY.containsKey(name)) {
            return Input.REGISTRY.get(name);
        }
        for (Type inputmappings$type : Type.values()) {
            if (!name.startsWith(inputmappings$type.name)) continue;
            String s = name.substring(inputmappings$type.name.length() + 1);
            return inputmappings$type.getOrMakeInput(Integer.parseInt(s));
        }
        throw new IllegalArgumentException("Unknown key name: " + name);
    }

    public static boolean isKeyDown(long p_216506_0_, int p_216506_2_) {
        return GLFW.glfwGetKey(p_216506_0_, p_216506_2_) == 1;
    }

    public static void setKeyCallbacks(long p_216505_0_, GLFWKeyCallbackI p_216505_2_, GLFWCharModsCallbackI p_216505_3_) {
        GLFW.glfwSetKeyCallback(p_216505_0_, p_216505_2_);
        GLFW.glfwSetCharModsCallback(p_216505_0_, p_216505_3_);
    }

    public static void setMouseCallbacks(long p_216503_0_, GLFWCursorPosCallbackI p_216503_2_, GLFWMouseButtonCallbackI p_216503_3_, GLFWScrollCallbackI p_216503_4_, GLFWDropCallbackI p_216503_5_) {
        GLFW.glfwSetCursorPosCallback(p_216503_0_, p_216503_2_);
        GLFW.glfwSetMouseButtonCallback(p_216503_0_, p_216503_3_);
        GLFW.glfwSetScrollCallback(p_216503_0_, p_216503_4_);
        GLFW.glfwSetDropCallback(p_216503_0_, p_216503_5_);
    }

    public static void setCursorPosAndMode(long p_216504_0_, int p_216504_2_, double p_216504_3_, double p_216504_5_) {
        GLFW.glfwSetCursorPos(p_216504_0_, p_216504_3_, p_216504_5_);
        GLFW.glfwSetInputMode(p_216504_0_, 208897, p_216504_2_);
    }

    public static boolean func_224790_a() {
        try {
            return GLFW_RAW_MOUSE_SUPPORTED != null && GLFW_RAW_MOUSE_SUPPORTED.invokeExact();
        }
        catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void setRawMouseInput(long p_224791_0_, boolean p_224791_2_) {
        if (InputMappings.func_224790_a()) {
            GLFW.glfwSetInputMode(p_224791_0_, GLFW_RAW_MOUSE, p_224791_2_ ? 1 : 0);
        }
    }

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodType methodtype = MethodType.methodType(Boolean.TYPE);
        MethodHandle methodhandle = null;
        int i = 0;
        try {
            methodhandle = lookup.findStatic(GLFW.class, "glfwRawMouseMotionSupported", methodtype);
            MethodHandle methodhandle1 = lookup.findStaticGetter(GLFW.class, "GLFW_RAW_MOUSE_MOTION", Integer.TYPE);
            i = methodhandle1.invokeExact();
        }
        catch (NoSuchFieldException | NoSuchMethodException methodhandle1) {
        }
        catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
        GLFW_RAW_MOUSE_SUPPORTED = methodhandle;
        GLFW_RAW_MOUSE = i;
        INPUT_INVALID = Type.KEYSYM.getOrMakeInput(-1);
    }

    public static enum Type {
        KEYSYM("key.keyboard", (p_237528_0_, p_237528_1_) -> {
            String s = GLFW.glfwGetKeyName(p_237528_0_, -1);
            return s != null ? new StringTextComponent(s) : new TranslationTextComponent((String)p_237528_1_);
        }),
        SCANCODE("scancode", (p_237527_0_, p_237527_1_) -> {
            String s = GLFW.glfwGetKeyName(-1, p_237527_0_);
            return s != null ? new StringTextComponent(s) : new TranslationTextComponent((String)p_237527_1_);
        }),
        MOUSE("key.mouse", (p_237524_0_, p_237524_1_) -> LanguageMap.getInstance().func_230506_b_((String)p_237524_1_) ? new TranslationTextComponent((String)p_237524_1_) : new TranslationTextComponent("key.mouse", p_237524_0_ + 1));

        private final Int2ObjectMap<Input> inputs = new Int2ObjectOpenHashMap<Input>();
        private final String name;
        private final BiFunction<Integer, String, ITextComponent> field_237522_f_;

        private static void registerInput(Type type, String nameIn, int keyCode) {
            Input inputmappings$input = new Input(nameIn, type, keyCode);
            type.inputs.put(keyCode, inputmappings$input);
        }

        private Type(String p_i232180_3_, BiFunction<Integer, String, ITextComponent> p_i232180_4_) {
            this.name = p_i232180_3_;
            this.field_237522_f_ = p_i232180_4_;
        }

        public Input getOrMakeInput(int keyCode) {
            return this.inputs.computeIfAbsent(keyCode, p_237525_1_ -> {
                int i = p_237525_1_;
                if (this == MOUSE) {
                    i = p_237525_1_ + 1;
                }
                String s = this.name + "." + i;
                return new Input(s, this, p_237525_1_);
            });
        }

        static {
            Type.registerInput(KEYSYM, "key.keyboard.unknown", -1);
            Type.registerInput(MOUSE, "key.mouse.left", 0);
            Type.registerInput(MOUSE, "key.mouse.right", 1);
            Type.registerInput(MOUSE, "key.mouse.middle", 2);
            Type.registerInput(MOUSE, "key.mouse.4", 3);
            Type.registerInput(MOUSE, "key.mouse.5", 4);
            Type.registerInput(MOUSE, "key.mouse.6", 5);
            Type.registerInput(MOUSE, "key.mouse.7", 6);
            Type.registerInput(MOUSE, "key.mouse.8", 7);
            Type.registerInput(KEYSYM, "key.keyboard.0", 48);
            Type.registerInput(KEYSYM, "key.keyboard.1", 49);
            Type.registerInput(KEYSYM, "key.keyboard.2", 50);
            Type.registerInput(KEYSYM, "key.keyboard.3", 51);
            Type.registerInput(KEYSYM, "key.keyboard.4", 52);
            Type.registerInput(KEYSYM, "key.keyboard.5", 53);
            Type.registerInput(KEYSYM, "key.keyboard.6", 54);
            Type.registerInput(KEYSYM, "key.keyboard.7", 55);
            Type.registerInput(KEYSYM, "key.keyboard.8", 56);
            Type.registerInput(KEYSYM, "key.keyboard.9", 57);
            Type.registerInput(KEYSYM, "key.keyboard.a", 65);
            Type.registerInput(KEYSYM, "key.keyboard.b", 66);
            Type.registerInput(KEYSYM, "key.keyboard.c", 67);
            Type.registerInput(KEYSYM, "key.keyboard.d", 68);
            Type.registerInput(KEYSYM, "key.keyboard.e", 69);
            Type.registerInput(KEYSYM, "key.keyboard.f", 70);
            Type.registerInput(KEYSYM, "key.keyboard.g", 71);
            Type.registerInput(KEYSYM, "key.keyboard.h", 72);
            Type.registerInput(KEYSYM, "key.keyboard.i", 73);
            Type.registerInput(KEYSYM, "key.keyboard.j", 74);
            Type.registerInput(KEYSYM, "key.keyboard.k", 75);
            Type.registerInput(KEYSYM, "key.keyboard.l", 76);
            Type.registerInput(KEYSYM, "key.keyboard.m", 77);
            Type.registerInput(KEYSYM, "key.keyboard.n", 78);
            Type.registerInput(KEYSYM, "key.keyboard.o", 79);
            Type.registerInput(KEYSYM, "key.keyboard.p", 80);
            Type.registerInput(KEYSYM, "key.keyboard.q", 81);
            Type.registerInput(KEYSYM, "key.keyboard.r", 82);
            Type.registerInput(KEYSYM, "key.keyboard.s", 83);
            Type.registerInput(KEYSYM, "key.keyboard.t", 84);
            Type.registerInput(KEYSYM, "key.keyboard.u", 85);
            Type.registerInput(KEYSYM, "key.keyboard.v", 86);
            Type.registerInput(KEYSYM, "key.keyboard.w", 87);
            Type.registerInput(KEYSYM, "key.keyboard.x", 88);
            Type.registerInput(KEYSYM, "key.keyboard.y", 89);
            Type.registerInput(KEYSYM, "key.keyboard.z", 90);
            Type.registerInput(KEYSYM, "key.keyboard.f1", 290);
            Type.registerInput(KEYSYM, "key.keyboard.f2", 291);
            Type.registerInput(KEYSYM, "key.keyboard.f3", 292);
            Type.registerInput(KEYSYM, "key.keyboard.f4", 293);
            Type.registerInput(KEYSYM, "key.keyboard.f5", 294);
            Type.registerInput(KEYSYM, "key.keyboard.f6", 295);
            Type.registerInput(KEYSYM, "key.keyboard.f7", 296);
            Type.registerInput(KEYSYM, "key.keyboard.f8", 297);
            Type.registerInput(KEYSYM, "key.keyboard.f9", 298);
            Type.registerInput(KEYSYM, "key.keyboard.f10", 299);
            Type.registerInput(KEYSYM, "key.keyboard.f11", 300);
            Type.registerInput(KEYSYM, "key.keyboard.f12", 301);
            Type.registerInput(KEYSYM, "key.keyboard.f13", 302);
            Type.registerInput(KEYSYM, "key.keyboard.f14", 303);
            Type.registerInput(KEYSYM, "key.keyboard.f15", 304);
            Type.registerInput(KEYSYM, "key.keyboard.f16", 305);
            Type.registerInput(KEYSYM, "key.keyboard.f17", 306);
            Type.registerInput(KEYSYM, "key.keyboard.f18", 307);
            Type.registerInput(KEYSYM, "key.keyboard.f19", 308);
            Type.registerInput(KEYSYM, "key.keyboard.f20", 309);
            Type.registerInput(KEYSYM, "key.keyboard.f21", 310);
            Type.registerInput(KEYSYM, "key.keyboard.f22", 311);
            Type.registerInput(KEYSYM, "key.keyboard.f23", 312);
            Type.registerInput(KEYSYM, "key.keyboard.f24", 313);
            Type.registerInput(KEYSYM, "key.keyboard.f25", 314);
            Type.registerInput(KEYSYM, "key.keyboard.num.lock", 282);
            Type.registerInput(KEYSYM, "key.keyboard.keypad.0", 320);
            Type.registerInput(KEYSYM, "key.keyboard.keypad.1", 321);
            Type.registerInput(KEYSYM, "key.keyboard.keypad.2", 322);
            Type.registerInput(KEYSYM, "key.keyboard.keypad.3", 323);
            Type.registerInput(KEYSYM, "key.keyboard.keypad.4", 324);
            Type.registerInput(KEYSYM, "key.keyboard.keypad.5", 325);
            Type.registerInput(KEYSYM, "key.keyboard.keypad.6", 326);
            Type.registerInput(KEYSYM, "key.keyboard.keypad.7", 327);
            Type.registerInput(KEYSYM, "key.keyboard.keypad.8", 328);
            Type.registerInput(KEYSYM, "key.keyboard.keypad.9", 329);
            Type.registerInput(KEYSYM, "key.keyboard.keypad.add", 334);
            Type.registerInput(KEYSYM, "key.keyboard.keypad.decimal", 330);
            Type.registerInput(KEYSYM, "key.keyboard.keypad.enter", 335);
            Type.registerInput(KEYSYM, "key.keyboard.keypad.equal", 336);
            Type.registerInput(KEYSYM, "key.keyboard.keypad.multiply", 332);
            Type.registerInput(KEYSYM, "key.keyboard.keypad.divide", 331);
            Type.registerInput(KEYSYM, "key.keyboard.keypad.subtract", 333);
            Type.registerInput(KEYSYM, "key.keyboard.down", 264);
            Type.registerInput(KEYSYM, "key.keyboard.left", 263);
            Type.registerInput(KEYSYM, "key.keyboard.right", 262);
            Type.registerInput(KEYSYM, "key.keyboard.up", 265);
            Type.registerInput(KEYSYM, "key.keyboard.apostrophe", 39);
            Type.registerInput(KEYSYM, "key.keyboard.backslash", 92);
            Type.registerInput(KEYSYM, "key.keyboard.comma", 44);
            Type.registerInput(KEYSYM, "key.keyboard.equal", 61);
            Type.registerInput(KEYSYM, "key.keyboard.grave.accent", 96);
            Type.registerInput(KEYSYM, "key.keyboard.left.bracket", 91);
            Type.registerInput(KEYSYM, "key.keyboard.minus", 45);
            Type.registerInput(KEYSYM, "key.keyboard.period", 46);
            Type.registerInput(KEYSYM, "key.keyboard.right.bracket", 93);
            Type.registerInput(KEYSYM, "key.keyboard.semicolon", 59);
            Type.registerInput(KEYSYM, "key.keyboard.slash", 47);
            Type.registerInput(KEYSYM, "key.keyboard.space", 32);
            Type.registerInput(KEYSYM, "key.keyboard.tab", 258);
            Type.registerInput(KEYSYM, "key.keyboard.left.alt", 342);
            Type.registerInput(KEYSYM, "key.keyboard.left.control", 341);
            Type.registerInput(KEYSYM, "key.keyboard.left.shift", 340);
            Type.registerInput(KEYSYM, "key.keyboard.left.win", 343);
            Type.registerInput(KEYSYM, "key.keyboard.right.alt", 346);
            Type.registerInput(KEYSYM, "key.keyboard.right.control", 345);
            Type.registerInput(KEYSYM, "key.keyboard.right.shift", 344);
            Type.registerInput(KEYSYM, "key.keyboard.right.win", 347);
            Type.registerInput(KEYSYM, "key.keyboard.enter", 257);
            Type.registerInput(KEYSYM, "key.keyboard.escape", 256);
            Type.registerInput(KEYSYM, "key.keyboard.backspace", 259);
            Type.registerInput(KEYSYM, "key.keyboard.delete", 261);
            Type.registerInput(KEYSYM, "key.keyboard.end", 269);
            Type.registerInput(KEYSYM, "key.keyboard.home", 268);
            Type.registerInput(KEYSYM, "key.keyboard.insert", 260);
            Type.registerInput(KEYSYM, "key.keyboard.page.down", 267);
            Type.registerInput(KEYSYM, "key.keyboard.page.up", 266);
            Type.registerInput(KEYSYM, "key.keyboard.caps.lock", 280);
            Type.registerInput(KEYSYM, "key.keyboard.pause", 284);
            Type.registerInput(KEYSYM, "key.keyboard.scroll.lock", 281);
            Type.registerInput(KEYSYM, "key.keyboard.menu", 348);
            Type.registerInput(KEYSYM, "key.keyboard.print.screen", 283);
            Type.registerInput(KEYSYM, "key.keyboard.world.1", 161);
            Type.registerInput(KEYSYM, "key.keyboard.world.2", 162);
        }
    }

    public static final class Input {
        private final String name;
        private final Type type;
        private final int keyCode;
        private final LazyValue<ITextComponent> field_237518_d_;
        private static final Map<String, Input> REGISTRY = Maps.newHashMap();

        private Input(String nameIn, Type typeIn, int keyCodeIn) {
            this.name = nameIn;
            this.type = typeIn;
            this.keyCode = keyCodeIn;
            this.field_237518_d_ = new LazyValue<ITextComponent>(() -> typeIn.field_237522_f_.apply(keyCodeIn, nameIn));
            REGISTRY.put(nameIn, this);
        }

        public Type getType() {
            return this.type;
        }

        public int getKeyCode() {
            return this.keyCode;
        }

        public String getTranslationKey() {
            return this.name;
        }

        public ITextComponent func_237520_d_() {
            return this.field_237518_d_.getValue();
        }

        public OptionalInt func_241552_e_() {
            if (this.keyCode >= 48 && this.keyCode <= 57) {
                return OptionalInt.of(this.keyCode - 48);
            }
            return this.keyCode >= 320 && this.keyCode <= 329 ? OptionalInt.of(this.keyCode - 320) : OptionalInt.empty();
        }

        public boolean equals(Object p_equals_1_) {
            if (this == p_equals_1_) {
                return true;
            }
            if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
                Input inputmappings$input = (Input)p_equals_1_;
                return this.keyCode == inputmappings$input.keyCode && this.type == inputmappings$input.type;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.type, this.keyCode});
        }

        public String toString() {
            return this.name;
        }
    }
}
