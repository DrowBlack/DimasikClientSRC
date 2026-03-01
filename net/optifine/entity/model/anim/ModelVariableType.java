package net.optifine.entity.model.anim;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.optifine.Config;

public enum ModelVariableType {
    POS_X("tx"),
    POS_Y("ty"),
    POS_Z("tz"),
    ANGLE_X("rx"),
    ANGLE_Y("ry"),
    ANGLE_Z("rz"),
    SCALE_X("sx"),
    SCALE_Y("sy"),
    SCALE_Z("sz");

    private String name;
    public static ModelVariableType[] VALUES;

    private ModelVariableType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public float getFloat(ModelRenderer mr) {
        switch (this) {
            case POS_X: {
                return mr.rotationPointX;
            }
            case POS_Y: {
                return mr.rotationPointY;
            }
            case POS_Z: {
                return mr.rotationPointZ;
            }
            case ANGLE_X: {
                return mr.rotateAngleX;
            }
            case ANGLE_Y: {
                return mr.rotateAngleY;
            }
            case ANGLE_Z: {
                return mr.rotateAngleZ;
            }
            case SCALE_X: {
                return mr.scaleX;
            }
            case SCALE_Y: {
                return mr.scaleY;
            }
            case SCALE_Z: {
                return mr.scaleZ;
            }
        }
        Config.warn("GetFloat not supported for: " + String.valueOf((Object)this));
        return 0.0f;
    }

    public void setFloat(ModelRenderer mr, float val) {
        switch (this) {
            case POS_X: {
                mr.rotationPointX = val;
                return;
            }
            case POS_Y: {
                mr.rotationPointY = val;
                return;
            }
            case POS_Z: {
                mr.rotationPointZ = val;
                return;
            }
            case ANGLE_X: {
                mr.rotateAngleX = val;
                return;
            }
            case ANGLE_Y: {
                mr.rotateAngleY = val;
                return;
            }
            case ANGLE_Z: {
                mr.rotateAngleZ = val;
                return;
            }
            case SCALE_X: {
                mr.scaleX = val;
                return;
            }
            case SCALE_Y: {
                mr.scaleY = val;
                return;
            }
            case SCALE_Z: {
                mr.scaleZ = val;
                return;
            }
        }
        Config.warn("SetFloat not supported for: " + String.valueOf((Object)this));
    }

    public static ModelVariableType parse(String str) {
        for (int i = 0; i < VALUES.length; ++i) {
            ModelVariableType modelvariabletype = VALUES[i];
            if (!modelvariabletype.getName().equals(str)) continue;
            return modelvariabletype;
        }
        return null;
    }

    static {
        VALUES = ModelVariableType.values();
    }
}
