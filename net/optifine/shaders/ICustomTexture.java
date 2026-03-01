package net.optifine.shaders;

public interface ICustomTexture {
    public int getTextureId();

    public int getTextureUnit();

    public void deleteTexture();

    default public int getTarget() {
        return 3553;
    }
}
