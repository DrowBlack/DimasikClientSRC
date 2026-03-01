package dimasik.helpers.interfaces;

import dimasik.helpers.visual.ShaderHelpers;

public interface IShaderAccess {
    public static final ShaderHelpers FONT_SUBSTRING = new ShaderHelpers("main/shaders/fragment/FontSubstring.fsh", "main/shaders/vertex/Vertex.vert", true);
    public static final ShaderHelpers FONT = new ShaderHelpers("main/shaders/fragment/Font.fsh", "main/shaders/vertex/Vertex.vert", true);
    public static final ShaderHelpers ROUNDED_TEXTURE = new ShaderHelpers("main/shaders/fragment/RoundedTexture.fsh", "main/shaders/vertex/Vertex.vert", true);
    public static final ShaderHelpers ROUNDED_GRADIENT = new ShaderHelpers("main/shaders/fragment/RoundedGradient.fsh", "main/shaders/vertex/Vertex.vert", true);
    public static final ShaderHelpers ROUNDED_VECTOR_GRADIENT = new ShaderHelpers("main/shaders/fragment/RoundedVectorGradient.fsh", "main/shaders/vertex/Vertex.vert", true);
    public static final ShaderHelpers ROUNDED_OUTLINE = new ShaderHelpers("main/shaders/fragment/RoundedOutline.fsh", "main/shaders/vertex/Vertex.vert", true);
    public static final ShaderHelpers ROUNDED_HEAD = new ShaderHelpers("main/shaders/fragment/RoundedHead.fsh", "main/shaders/vertex/Vertex.vert", true);
    public static final ShaderHelpers PROGRESS_BAR = new ShaderHelpers("main/shaders/fragment/ProgressBar.fsh", "main/shaders/vertex/Vertex.vert", true);
    public static final ShaderHelpers BLUR = new ShaderHelpers("main/shaders/fragment/Blur.fsh", "main/shaders/vertex/Vertex.vert", true);
    public static final ShaderHelpers BLOOM = new ShaderHelpers("main/shaders/fragment/Bloom.fsh", "main/shaders/vertex/Vertex.vert", true);
}
