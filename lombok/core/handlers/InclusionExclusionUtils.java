package lombok.core.handlers;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.LombokNode;
import lombok.core.handlers.HandlerUtil;

public class InclusionExclusionUtils {
    private static List<Integer> createListOfNonExistentFields(List<String> list, LombokNode<?, ?, ?> type, boolean excludeStandard, boolean excludeTransient) {
        boolean[] matched = new boolean[list.size()];
        for (LombokNode child : type.down()) {
            int idx;
            if (list.isEmpty()) break;
            if (child.getKind() != AST.Kind.FIELD || excludeStandard && (child.isStatic() || child.getName().startsWith("$")) || excludeTransient && child.isTransient() || (idx = list.indexOf(child.getName())) <= -1) continue;
            matched[idx] = true;
        }
        ArrayList<Integer> problematic = new ArrayList<Integer>();
        int i = 0;
        while (i < list.size()) {
            if (!matched[i]) {
                problematic.add(i);
            }
            ++i;
        }
        return problematic;
    }

    public static void checkForBogusFieldNames(LombokNode<?, ?, ?> type, AnnotationValues<?> annotation, List<String> excludes, List<String> includes) {
        if (excludes != null && !excludes.isEmpty()) {
            for (int i : InclusionExclusionUtils.createListOfNonExistentFields(excludes, type, true, false)) {
                if (annotation == null) continue;
                annotation.setWarning("exclude", "This field does not exist, or would have been excluded anyway.", i);
            }
        }
        if (includes != null && !includes.isEmpty()) {
            for (int i : InclusionExclusionUtils.createListOfNonExistentFields(includes, type, false, false)) {
                if (annotation == null) continue;
                annotation.setWarning("of", "This field does not exist.", i);
            }
        }
    }

    private static String innerAnnName(Class<? extends Annotation> type) {
        String name = type.getSimpleName();
        Class<?> c = type.getEnclosingClass();
        while (c != null) {
            name = String.valueOf(c.getSimpleName()) + "." + name;
            c = c.getEnclosingClass();
        }
        return name;
    }

    private static <A extends AST<A, L, N>, L extends LombokNode<A, L, N>, N, I extends Annotation> List<Included<L, I>> handleIncludeExcludeMarking(Class<I> inclType, String replaceName, Class<? extends Annotation> exclType, LombokNode<A, L, N> typeNode, AnnotationValues<?> annotation, LombokNode<A, L, N> annotationNode, boolean includeTransient) {
        boolean onlyExplicitlyIncluded = annotation != null ? annotation.getAsBoolean("onlyExplicitlyIncluded") : false;
        return InclusionExclusionUtils.handleIncludeExcludeMarking(inclType, onlyExplicitlyIncluded, replaceName, exclType, typeNode, annotation, annotationNode, includeTransient);
    }

    private static <A extends AST<A, L, N>, L extends LombokNode<A, L, N>, N, I extends Annotation> List<Included<L, I>> handleIncludeExcludeMarking(Class<I> inclType, boolean onlyExplicitlyIncluded, String replaceName, Class<? extends Annotation> exclType, LombokNode<A, L, N> typeNode, AnnotationValues<?> annotation, LombokNode<A, L, N> annotationNode, boolean includeTransient) {
        List<String> oldExcludes = annotation != null && annotation.isExplicit("exclude") ? annotation.getAsStringList("exclude") : null;
        List<String> oldIncludes = annotation != null && annotation.isExplicit("of") ? annotation.getAsStringList("of") : null;
        boolean memberAnnotationMode = onlyExplicitlyIncluded;
        ArrayList<Included<L, I>> members = new ArrayList<Included<L, I>>();
        ArrayList<String> namesToAutoExclude = new ArrayList<String>();
        if (typeNode == null || typeNode.getKind() != AST.Kind.TYPE) {
            return null;
        }
        InclusionExclusionUtils.checkForBogusFieldNames(typeNode, annotation, oldExcludes, oldIncludes);
        if (oldExcludes != null && oldIncludes != null) {
            oldExcludes = null;
            if (annotation != null) {
                annotation.setWarning("exclude", "exclude and of are mutually exclusive; the 'exclude' parameter will be ignored.");
            }
        }
        for (LombokNode child : typeNode.down()) {
            boolean markExclude = child.getKind() == AST.Kind.FIELD && child.hasAnnotation(exclType);
            AnnotationValues<I> markInclude = null;
            if (child.getKind() == AST.Kind.FIELD || child.getKind() == AST.Kind.METHOD) {
                markInclude = child.findAnnotation(inclType);
            }
            if (markExclude || markInclude != null) {
                memberAnnotationMode = true;
            }
            if (markInclude != null && markExclude) {
                child.addError("@" + InclusionExclusionUtils.innerAnnName(exclType) + " and @" + InclusionExclusionUtils.innerAnnName(inclType) + " are mutually exclusive; the @Include annotation will be ignored");
                markInclude = null;
            }
            String name = child.getName();
            if (markExclude) {
                if (onlyExplicitlyIncluded) {
                    child.addWarning("The @Exclude annotation is not needed; 'onlyExplicitlyIncluded' is set, so this member would be excluded anyway");
                    continue;
                }
                if (child.isStatic()) {
                    child.addWarning("The @Exclude annotation is not needed; static fields aren't included anyway");
                    continue;
                }
                if (!name.startsWith("$")) continue;
                child.addWarning("The @Exclude annotation is not needed; fields that start with $ aren't included anyway");
                continue;
            }
            if (oldExcludes != null && oldExcludes.contains(name)) continue;
            if (markInclude != null) {
                I inc = markInclude.getInstance();
                if (child.getKind() == AST.Kind.METHOD) {
                    String n;
                    if (child.countMethodParameters() > 0) {
                        child.addError("Methods included with @" + InclusionExclusionUtils.innerAnnName(inclType) + " must have no arguments; it will not be included");
                        continue;
                    }
                    String string = n = replaceName != null ? markInclude.getAsString(replaceName) : "";
                    if (n.isEmpty()) {
                        n = name;
                    }
                    namesToAutoExclude.add(n);
                }
                members.add(new Included<LombokNode, I>(child, inc, false, markInclude.isExplicit("rank")));
                continue;
            }
            if (onlyExplicitlyIncluded) continue;
            if (oldIncludes != null) {
                if (child.getKind() != AST.Kind.FIELD || !oldIncludes.contains(name)) continue;
                members.add(new Included<LombokNode, Object>(child, null, false, false));
                continue;
            }
            if (child.getKind() != AST.Kind.FIELD || child.isStatic() || child.isTransient() && !includeTransient || name.startsWith("$") || child.isEnumMember()) continue;
            members.add(new Included<LombokNode, Object>(child, null, true, false));
        }
        Iterator it = members.iterator();
        while (it.hasNext()) {
            Included m = (Included)it.next();
            if (!m.isDefaultInclude() || !namesToAutoExclude.contains(((LombokNode)m.getNode()).getName())) continue;
            it.remove();
        }
        if (annotation == null || !annotation.isExplicit("exclude")) {
            oldExcludes = null;
        }
        if (annotation == null || !annotation.isExplicit("of")) {
            oldIncludes = null;
        }
        if (memberAnnotationMode && (oldExcludes != null || oldIncludes != null)) {
            annotationNode.addError("The old-style 'exclude/of' parameter cannot be used together with the new-style @Include / @Exclude annotations.");
            return null;
        }
        return members;
    }

    public static <A extends AST<A, L, N>, L extends LombokNode<A, L, N>, N> List<Included<L, ToString.Include>> handleToStringMarking(LombokNode<A, L, N> typeNode, boolean onlyExplicitlyIncluded, AnnotationValues<ToString> annotation, LombokNode<A, L, N> annotationNode) {
        List<Included<L, ToString.Include>> members = InclusionExclusionUtils.handleIncludeExcludeMarking(ToString.Include.class, onlyExplicitlyIncluded, "name", ToString.Exclude.class, typeNode, annotation, annotationNode, true);
        Collections.sort(members, new Comparator<Included<L, ToString.Include>>(){

            @Override
            public int compare(Included<L, ToString.Include> a, Included<L, ToString.Include> b) {
                int ra = a.getInc() == null ? 0 : a.getInc().rank();
                int rb = b.getInc() == null ? 0 : b.getInc().rank();
                return InclusionExclusionUtils.compareRankOrPosition(ra, rb, (LombokNode)a.getNode(), (LombokNode)b.getNode());
            }
        });
        return members;
    }

    public static <A extends AST<A, L, N>, L extends LombokNode<A, L, N>, N> List<Included<L, EqualsAndHashCode.Include>> handleEqualsAndHashCodeMarking(LombokNode<A, L, N> typeNode, AnnotationValues<EqualsAndHashCode> annotation, LombokNode<A, L, N> annotationNode) {
        List<Included<L, EqualsAndHashCode.Include>> members = InclusionExclusionUtils.handleIncludeExcludeMarking(EqualsAndHashCode.Include.class, "replaces", EqualsAndHashCode.Exclude.class, typeNode, annotation, annotationNode, false);
        Collections.sort(members, new Comparator<Included<L, EqualsAndHashCode.Include>>(){

            @Override
            public int compare(Included<L, EqualsAndHashCode.Include> a, Included<L, EqualsAndHashCode.Include> b) {
                int ra = a.hasExplicitRank() ? a.getInc().rank() : HandlerUtil.defaultEqualsAndHashcodeIncludeRank(((LombokNode)a.node).fieldOrMethodBaseType());
                int rb = b.hasExplicitRank() ? b.getInc().rank() : HandlerUtil.defaultEqualsAndHashcodeIncludeRank(((LombokNode)b.node).fieldOrMethodBaseType());
                return InclusionExclusionUtils.compareRankOrPosition(ra, rb, (LombokNode)a.getNode(), (LombokNode)b.getNode());
            }
        });
        return members;
    }

    private static <A extends AST<A, L, N>, L extends LombokNode<A, L, N>, N> int compareRankOrPosition(int ra, int rb, LombokNode<A, L, N> nodeA, LombokNode<A, L, N> nodeB) {
        int pb;
        if (ra < rb) {
            return 1;
        }
        if (ra > rb) {
            return -1;
        }
        int pa = nodeA.getStartPos();
        if (pa < (pb = nodeB.getStartPos())) {
            return -1;
        }
        if (pa > pb) {
            return 1;
        }
        return 0;
    }

    public static class Included<L, I extends Annotation> {
        private final L node;
        private final I inc;
        private final boolean defaultInclude;
        private final boolean explicitRank;

        public Included(L node, I inc, boolean defaultInclude, boolean explicitRank) {
            this.node = node;
            this.inc = inc;
            this.defaultInclude = defaultInclude;
            this.explicitRank = explicitRank;
        }

        public L getNode() {
            return this.node;
        }

        public I getInc() {
            return this.inc;
        }

        public boolean isDefaultInclude() {
            return this.defaultInclude;
        }

        public boolean hasExplicitRank() {
            return this.explicitRank;
        }
    }
}
