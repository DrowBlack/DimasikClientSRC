package net.minecraftforge.forgespi.language;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModLanguageProvider;
import org.objectweb.asm.Type;

public class ModFileScanData {
    private final Set<AnnotationData> annotations = new LinkedHashSet<AnnotationData>();
    private final Set<ClassData> classes = new LinkedHashSet<ClassData>();
    private Map<String, ? extends IModLanguageProvider.IModLanguageLoader> modTargets;
    private Map<String, ?> functionalScanners;
    private List<IModFileInfo> modFiles = new ArrayList<IModFileInfo>();

    public static Predicate<Type> interestingAnnotations() {
        return t -> true;
    }

    public Set<ClassData> getClasses() {
        return this.classes;
    }

    public Set<AnnotationData> getAnnotations() {
        return this.annotations;
    }

    public void addLanguageLoader(Map<String, ? extends IModLanguageProvider.IModLanguageLoader> modTargetMap) {
        this.modTargets = modTargetMap;
    }

    public void addModFileInfo(IModFileInfo info) {
        this.modFiles.add(info);
    }

    public Map<String, ? extends IModLanguageProvider.IModLanguageLoader> getTargets() {
        return this.modTargets;
    }

    public List<IModFileInfo> getIModInfoData() {
        return this.modFiles;
    }

    public static class AnnotationData {
        private final Type annotationType;
        private final ElementType targetType;
        private final Type clazz;
        private final String memberName;
        private final Map<String, Object> annotationData;

        public AnnotationData(Type annotationType, ElementType targetType, Type clazz, String memberName, Map<String, Object> annotationData) {
            this.annotationType = annotationType;
            this.targetType = targetType;
            this.clazz = clazz;
            this.memberName = memberName;
            this.annotationData = annotationData;
        }

        public Type getAnnotationType() {
            return this.annotationType;
        }

        public ElementType getTargetType() {
            return this.targetType;
        }

        public Type getClassType() {
            return this.clazz;
        }

        public String getMemberName() {
            return this.memberName;
        }

        public Map<String, Object> getAnnotationData() {
            return this.annotationData;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != this.getClass()) {
                return false;
            }
            AnnotationData dat = (AnnotationData)obj;
            return Objects.equals(this.annotationType, dat.annotationType) && Objects.equals((Object)this.targetType, (Object)dat.targetType) && Objects.equals(this.clazz, dat.clazz) && Objects.equals(this.memberName, dat.memberName) && Objects.equals(this.annotationData, dat.annotationData);
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.annotationType, this.targetType, this.clazz, this.memberName, this.annotationData});
        }
    }

    public static class ClassData {
        private final Type clazz;
        private final Type parent;
        private final Set<Type> interfaces;

        public ClassData(Type clazz, Type parent, Set<Type> interfaces) {
            this.clazz = clazz;
            this.parent = parent;
            this.interfaces = interfaces;
        }

        public boolean equals(Object obj) {
            try {
                return !Objects.isNull(obj) && Objects.equals(this.clazz, ((ClassData)obj).clazz);
            }
            catch (ClassCastException e) {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hashCode(this.clazz);
        }
    }
}
