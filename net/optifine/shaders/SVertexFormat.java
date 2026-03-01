package net.optifine.shaders;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class SVertexFormat {
    public static final int vertexSizeBlock = 18;
    public static final int offsetMidBlock = 8;
    public static final int offsetMidTexCoord = 9;
    public static final int offsetTangent = 11;
    public static final int offsetEntity = 13;
    public static final int offsetVelocity = 15;
    public static final VertexFormatElement SHADERS_MIDBLOCK_3B = SVertexFormat.makeElement("SHADERS_MIDOFFSET_3B", 0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.PADDING, 3);
    public static final VertexFormatElement PADDING_1B = SVertexFormat.makeElement("PADDING_1B", 0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.PADDING, 1);
    public static final VertexFormatElement SHADERS_MIDTEXCOORD_2F = SVertexFormat.makeElement("SHADERS_MIDTEXCOORD_2F", 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.PADDING, 2);
    public static final VertexFormatElement SHADERS_TANGENT_4S = SVertexFormat.makeElement("SHADERS_TANGENT_4S", 0, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.PADDING, 4);
    public static final VertexFormatElement SHADERS_MC_ENTITY_4S = SVertexFormat.makeElement("SHADERS_MC_ENTITY_4S", 0, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.PADDING, 4);
    public static final VertexFormatElement SHADERS_VELOCITY_3F = SVertexFormat.makeElement("SHADERS_VELOCITY_3F", 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.PADDING, 3);

    public static VertexFormat makeExtendedFormatBlock(VertexFormat blockVanilla) {
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll(blockVanilla.getElements());
        builder.add(SHADERS_MIDBLOCK_3B);
        builder.add(PADDING_1B);
        builder.add(SHADERS_MIDTEXCOORD_2F);
        builder.add(SHADERS_TANGENT_4S);
        builder.add(SHADERS_MC_ENTITY_4S);
        builder.add(SHADERS_VELOCITY_3F);
        return new VertexFormat((ImmutableList<VertexFormatElement>)builder.build());
    }

    public static VertexFormat makeExtendedFormatEntity(VertexFormat entityVanilla) {
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll(entityVanilla.getElements());
        builder.add(SHADERS_MIDTEXCOORD_2F);
        builder.add(SHADERS_TANGENT_4S);
        builder.add(SHADERS_MC_ENTITY_4S);
        builder.add(SHADERS_VELOCITY_3F);
        return new VertexFormat((ImmutableList<VertexFormatElement>)builder.build());
    }

    private static VertexFormatElement makeElement(String name, int indexIn, VertexFormatElement.Type typeIn, VertexFormatElement.Usage usageIn, int count) {
        VertexFormatElement vertexformatelement = new VertexFormatElement(indexIn, typeIn, usageIn, count);
        vertexformatelement.setName(name);
        return vertexformatelement;
    }
}
