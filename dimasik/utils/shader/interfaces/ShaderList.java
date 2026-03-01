package dimasik.utils.shader.interfaces;

import dimasik.utils.shader.AbstractShader;
import dimasik.utils.shader.list.BlackRectShader;
import dimasik.utils.shader.list.BloomShader;
import dimasik.utils.shader.list.BlurShader;
import dimasik.utils.shader.list.WhiteRectShader;

public interface ShaderList {
    public static final AbstractShader BLUR_SHADER = new BlurShader();
    public static final AbstractShader BLOOM_SHADER = new BloomShader();
    public static final AbstractShader WHITE_RECT_SHADER = new WhiteRectShader();
    public static final AbstractShader BLACK_RECT_SHADER = new BlackRectShader();
}
