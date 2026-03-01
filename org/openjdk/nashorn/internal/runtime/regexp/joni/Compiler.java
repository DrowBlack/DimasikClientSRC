package org.openjdk.nashorn.internal.runtime.regexp.joni;

import org.openjdk.nashorn.internal.runtime.regexp.joni.Analyser;
import org.openjdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.openjdk.nashorn.internal.runtime.regexp.joni.ast.AnchorNode;
import org.openjdk.nashorn.internal.runtime.regexp.joni.ast.BackRefNode;
import org.openjdk.nashorn.internal.runtime.regexp.joni.ast.CClassNode;
import org.openjdk.nashorn.internal.runtime.regexp.joni.ast.ConsAltNode;
import org.openjdk.nashorn.internal.runtime.regexp.joni.ast.EncloseNode;
import org.openjdk.nashorn.internal.runtime.regexp.joni.ast.Node;
import org.openjdk.nashorn.internal.runtime.regexp.joni.ast.QuantifierNode;
import org.openjdk.nashorn.internal.runtime.regexp.joni.ast.StringNode;
import org.openjdk.nashorn.internal.runtime.regexp.joni.exception.ErrorMessages;
import org.openjdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;
import org.openjdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;

abstract class Compiler
implements ErrorMessages {
    protected final Analyser analyser;
    protected final Regex regex;

    protected Compiler(Analyser analyser) {
        this.analyser = analyser;
        this.regex = analyser.regex;
    }

    final void compile() {
        this.prepare();
        this.compileTree(this.analyser.root);
        this.finish();
    }

    protected abstract void prepare();

    protected abstract void finish();

    protected abstract void compileAltNode(ConsAltNode var1);

    private void compileStringRawNode(StringNode sn) {
        if (sn.length() <= 0) {
            return;
        }
        this.addCompileString(sn.chars, sn.p, sn.length(), false);
    }

    private void compileStringNode(StringNode node) {
        int prev;
        StringNode sn = node;
        if (sn.length() <= 0) {
            return;
        }
        boolean ambig = sn.isAmbig();
        int p = prev = sn.p;
        int end = sn.end;
        char[] chars = sn.chars;
        ++p;
        int slen = 1;
        while (p < end) {
            ++slen;
            ++p;
        }
        this.addCompileString(chars, prev, slen, ambig);
    }

    protected abstract void addCompileString(char[] var1, int var2, int var3, boolean var4);

    protected abstract void compileCClassNode(CClassNode var1);

    protected abstract void compileAnyCharNode();

    protected abstract void compileBackrefNode(BackRefNode var1);

    protected abstract void compileNonCECQuantifierNode(QuantifierNode var1);

    protected abstract void compileOptionNode(EncloseNode var1);

    protected abstract void compileEncloseNode(EncloseNode var1);

    protected abstract void compileAnchorNode(AnchorNode var1);

    protected final void compileTree(Node node) {
        switch (node.getType()) {
            case 8: {
                ConsAltNode lin = (ConsAltNode)node;
                do {
                    this.compileTree(lin.car);
                } while ((lin = lin.cdr) != null);
                break;
            }
            case 9: {
                this.compileAltNode((ConsAltNode)node);
                break;
            }
            case 0: {
                StringNode sn = (StringNode)node;
                if (sn.isRaw()) {
                    this.compileStringRawNode(sn);
                    break;
                }
                this.compileStringNode(sn);
                break;
            }
            case 1: {
                this.compileCClassNode((CClassNode)node);
                break;
            }
            case 3: {
                this.compileAnyCharNode();
                break;
            }
            case 4: {
                this.compileBackrefNode((BackRefNode)node);
                break;
            }
            case 5: {
                this.compileNonCECQuantifierNode((QuantifierNode)node);
                break;
            }
            case 6: {
                EncloseNode enode = (EncloseNode)node;
                if (enode.isOption()) {
                    this.compileOptionNode(enode);
                    break;
                }
                this.compileEncloseNode(enode);
                break;
            }
            case 7: {
                this.compileAnchorNode((AnchorNode)node);
                break;
            }
            default: {
                this.newInternalException("internal parser error (bug)");
            }
        }
    }

    protected final void compileTreeNTimes(Node node, int n) {
        for (int i = 0; i < n; ++i) {
            this.compileTree(node);
        }
    }

    protected void newSyntaxException(String message) {
        throw new SyntaxException(message);
    }

    protected void newInternalException(String message) {
        throw new InternalException(message);
    }
}
