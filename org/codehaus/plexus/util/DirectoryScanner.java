package org.codehaus.plexus.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import org.codehaus.plexus.util.AbstractScanner;
import org.codehaus.plexus.util.Java7Detector;
import org.codehaus.plexus.util.MatchPattern;
import org.codehaus.plexus.util.NioFiles;

public class DirectoryScanner
extends AbstractScanner {
    protected File basedir;
    protected Vector<String> filesIncluded;
    protected Vector<String> filesNotIncluded;
    protected Vector<String> filesExcluded;
    protected Vector<String> dirsIncluded;
    protected Vector<String> dirsNotIncluded;
    protected Vector<String> dirsExcluded;
    protected Vector<String> filesDeselected;
    protected Vector<String> dirsDeselected;
    protected boolean haveSlowResults = false;
    private boolean followSymlinks = true;
    protected boolean everythingIncluded = true;
    private final String[] tokenizedEmpty = MatchPattern.tokenizePathToString("", File.separator);

    public void setBasedir(String basedir) {
        this.setBasedir(new File(basedir.replace('/', File.separatorChar).replace('\\', File.separatorChar)));
    }

    public void setBasedir(File basedir) {
        this.basedir = basedir;
    }

    @Override
    public File getBasedir() {
        return this.basedir;
    }

    public void setFollowSymlinks(boolean followSymlinks) {
        this.followSymlinks = followSymlinks;
    }

    public boolean isEverythingIncluded() {
        return this.everythingIncluded;
    }

    @Override
    public void scan() throws IllegalStateException {
        if (this.basedir == null) {
            throw new IllegalStateException("No basedir set");
        }
        if (!this.basedir.exists()) {
            throw new IllegalStateException("basedir " + this.basedir + " does not exist");
        }
        if (!this.basedir.isDirectory()) {
            throw new IllegalStateException("basedir " + this.basedir + " is not a directory");
        }
        this.setupDefaultFilters();
        this.setupMatchPatterns();
        this.filesIncluded = new Vector();
        this.filesNotIncluded = new Vector();
        this.filesExcluded = new Vector();
        this.filesDeselected = new Vector();
        this.dirsIncluded = new Vector();
        this.dirsNotIncluded = new Vector();
        this.dirsExcluded = new Vector();
        this.dirsDeselected = new Vector();
        if (this.isIncluded("", this.tokenizedEmpty)) {
            if (!this.isExcluded("", this.tokenizedEmpty)) {
                if (this.isSelected("", this.basedir)) {
                    this.dirsIncluded.addElement("");
                } else {
                    this.dirsDeselected.addElement("");
                }
            } else {
                this.dirsExcluded.addElement("");
            }
        } else {
            this.dirsNotIncluded.addElement("");
        }
        this.scandir(this.basedir, "", true);
    }

    protected void slowScan() {
        if (this.haveSlowResults) {
            return;
        }
        Object[] excl = new String[this.dirsExcluded.size()];
        this.dirsExcluded.copyInto(excl);
        Object[] notIncl = new String[this.dirsNotIncluded.size()];
        this.dirsNotIncluded.copyInto(notIncl);
        for (Object anExcl : excl) {
            if (this.couldHoldIncluded((String)anExcl)) continue;
            this.scandir(new File(this.basedir, (String)anExcl), (String)anExcl + File.separator, false);
        }
        for (Object aNotIncl : notIncl) {
            if (this.couldHoldIncluded((String)aNotIncl)) continue;
            this.scandir(new File(this.basedir, (String)aNotIncl), (String)aNotIncl + File.separator, false);
        }
        this.haveSlowResults = true;
    }

    protected void scandir(File dir, String vpath, boolean fast) {
        File file;
        String[] newfiles = dir.list();
        if (newfiles == null) {
            newfiles = new String[]{};
        }
        if (!this.followSymlinks) {
            ArrayList<String> noLinks = new ArrayList<String>();
            String[] stringArray = newfiles;
            int n = stringArray.length;
            for (int i = 0; i < n; ++i) {
                String newfile = stringArray[i];
                try {
                    if (this.isParentSymbolicLink(dir, newfile)) {
                        String name = vpath + newfile;
                        file = new File(dir, newfile);
                        if (file.isDirectory()) {
                            this.dirsExcluded.addElement(name);
                            continue;
                        }
                        this.filesExcluded.addElement(name);
                        continue;
                    }
                    noLinks.add(newfile);
                    continue;
                }
                catch (IOException ioe) {
                    String msg = "IOException caught while checking for links, couldn't get canonical path!";
                    System.err.println(msg);
                    noLinks.add(newfile);
                }
            }
            newfiles = noLinks.toArray(new String[noLinks.size()]);
        }
        for (String newfile : newfiles) {
            String name = vpath + newfile;
            String[] tokenizedName = MatchPattern.tokenizePathToString(name, File.separator);
            file = new File(dir, newfile);
            if (file.isDirectory()) {
                if (this.isIncluded(name, tokenizedName)) {
                    if (!this.isExcluded(name, tokenizedName)) {
                        if (this.isSelected(name, file)) {
                            this.dirsIncluded.addElement(name);
                            if (fast) {
                                this.scandir(file, name + File.separator, fast);
                            }
                        } else {
                            this.everythingIncluded = false;
                            this.dirsDeselected.addElement(name);
                            if (fast && this.couldHoldIncluded(name)) {
                                this.scandir(file, name + File.separator, fast);
                            }
                        }
                    } else {
                        this.everythingIncluded = false;
                        this.dirsExcluded.addElement(name);
                        if (fast && this.couldHoldIncluded(name)) {
                            this.scandir(file, name + File.separator, fast);
                        }
                    }
                } else {
                    this.everythingIncluded = false;
                    this.dirsNotIncluded.addElement(name);
                    if (fast && this.couldHoldIncluded(name)) {
                        this.scandir(file, name + File.separator, fast);
                    }
                }
                if (fast) continue;
                this.scandir(file, name + File.separator, fast);
                continue;
            }
            if (!file.isFile()) continue;
            if (this.isIncluded(name, tokenizedName)) {
                if (!this.isExcluded(name, tokenizedName)) {
                    if (this.isSelected(name, file)) {
                        this.filesIncluded.addElement(name);
                        continue;
                    }
                    this.everythingIncluded = false;
                    this.filesDeselected.addElement(name);
                    continue;
                }
                this.everythingIncluded = false;
                this.filesExcluded.addElement(name);
                continue;
            }
            this.everythingIncluded = false;
            this.filesNotIncluded.addElement(name);
        }
    }

    protected boolean isSelected(String name, File file) {
        return true;
    }

    @Override
    public String[] getIncludedFiles() {
        Object[] files = new String[this.filesIncluded.size()];
        this.filesIncluded.copyInto(files);
        return files;
    }

    public String[] getNotIncludedFiles() {
        this.slowScan();
        Object[] files = new String[this.filesNotIncluded.size()];
        this.filesNotIncluded.copyInto(files);
        return files;
    }

    public String[] getExcludedFiles() {
        this.slowScan();
        Object[] files = new String[this.filesExcluded.size()];
        this.filesExcluded.copyInto(files);
        return files;
    }

    public String[] getDeselectedFiles() {
        this.slowScan();
        Object[] files = new String[this.filesDeselected.size()];
        this.filesDeselected.copyInto(files);
        return files;
    }

    @Override
    public String[] getIncludedDirectories() {
        Object[] directories = new String[this.dirsIncluded.size()];
        this.dirsIncluded.copyInto(directories);
        return directories;
    }

    public String[] getNotIncludedDirectories() {
        this.slowScan();
        Object[] directories = new String[this.dirsNotIncluded.size()];
        this.dirsNotIncluded.copyInto(directories);
        return directories;
    }

    public String[] getExcludedDirectories() {
        this.slowScan();
        Object[] directories = new String[this.dirsExcluded.size()];
        this.dirsExcluded.copyInto(directories);
        return directories;
    }

    public String[] getDeselectedDirectories() {
        this.slowScan();
        Object[] directories = new String[this.dirsDeselected.size()];
        this.dirsDeselected.copyInto(directories);
        return directories;
    }

    public boolean isSymbolicLink(File parent, String name) throws IOException {
        if (Java7Detector.isJava7()) {
            return NioFiles.isSymbolicLink(new File(parent, name));
        }
        File resolvedParent = new File(parent.getCanonicalPath());
        File toTest = new File(resolvedParent, name);
        return !toTest.getAbsolutePath().equals(toTest.getCanonicalPath());
    }

    public boolean isParentSymbolicLink(File parent, String name) throws IOException {
        if (Java7Detector.isJava7()) {
            return NioFiles.isSymbolicLink(parent);
        }
        File resolvedParent = new File(parent.getCanonicalPath());
        File toTest = new File(resolvedParent, name);
        return !toTest.getAbsolutePath().equals(toTest.getCanonicalPath());
    }
}
