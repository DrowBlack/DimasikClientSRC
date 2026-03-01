package net.minecraft.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import javax.annotation.Nullable;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTToSNBTConverter
implements IDataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private final DataGenerator generator;

    public NBTToSNBTConverter(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        Path path = this.generator.getOutputFolder();
        for (Path path1 : this.generator.getInputFolders()) {
            Files.walk(path1, new FileVisitOption[0]).filter(path2 -> path2.toString().endsWith(".nbt")).forEach(nbtPath -> NBTToSNBTConverter.convertNBTToSNBT(nbtPath, this.getFileName(path1, (Path)nbtPath), path));
        }
    }

    @Override
    public String getName() {
        return "NBT to SNBT";
    }

    private String getFileName(Path inputFolder, Path fileIn) {
        String s = inputFolder.relativize(fileIn).toString().replaceAll("\\\\", "/");
        return s.substring(0, s.length() - ".nbt".length());
    }

    @Nullable
    public static Path convertNBTToSNBT(Path snbtPath, String name, Path nbtPath) {
        try {
            CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(Files.newInputStream(snbtPath, new OpenOption[0]));
            ITextComponent itextcomponent = compoundnbt.toFormattedComponent("    ", 0);
            String s = itextcomponent.getString() + "\n";
            Path path = nbtPath.resolve(name + ".snbt");
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path, new OpenOption[0]);){
                bufferedwriter.write(s);
            }
            LOGGER.info("Converted {} from NBT to SNBT", (Object)name);
            return path;
        }
        catch (IOException ioexception) {
            LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", (Object)name, (Object)snbtPath, (Object)ioexception);
            return null;
        }
    }
}
