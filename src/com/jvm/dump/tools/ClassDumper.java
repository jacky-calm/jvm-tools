package com.jvm.dump.tools;

/**
 * This tool can help dump class files. You need sa-jdi.jar to compile it.
 *
 * Created with IntelliJ IDEA.
 * User: xcheng
 * Date: 13-4-25
 * Time: 下午3:21
 * To change this template use File | Settings | File Templates.
 */

import sun.jvm.hotspot.debugger.AddressException;
import sun.jvm.hotspot.memory.SystemDictionary;
import sun.jvm.hotspot.oops.InstanceKlass;
import sun.jvm.hotspot.oops.Klass;
import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.tools.Tool;
import sun.jvm.hotspot.tools.jcore.ClassWriter;

import java.io.*;

public class ClassDumper extends Tool {
    private String      outputDirectory;
    String pattern = "";
    public void run() {
        // Ready to go with the database...
        try {
            // load class filters
            pattern = System.getProperty("pattern");
            outputDirectory = System.getProperty("out");
            if (outputDirectory == null)
                outputDirectory = ".";

            // walk through the system dictionary
            SystemDictionary dict = VM.getVM().getSystemDictionary();
            dict.classesDo(new SystemDictionary.ClassVisitor() {
                public void visit(Klass k) {
                    if (k instanceof InstanceKlass) {
                        try {
                            dumpKlass((InstanceKlass) k);
                        } catch (Exception e) {
                            System.out.println(k.getName().asString());
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        catch (AddressException e) {
            System.err.println("Error accessing address 0x"
                    + Long.toHexString(e.getAddress()));
            e.printStackTrace();
        }
    }

    public String getName() {
        return "jcore";
    }

    private void dumpKlass(InstanceKlass kls) {
        if (!canInclude(kls)) {
            return;
        }

        String klassName = kls.getName().asString();
        klassName = klassName.replace('/', File.separatorChar);
        int index = klassName.lastIndexOf(File.separatorChar);
        File dir = null;
        if (index != -1) {
            String dirName = klassName.substring(0, index);
            dir =  new File(outputDirectory,  dirName);
        } else {
            dir = new File(outputDirectory);
        }

        dir.mkdirs();
        File f = new File(dir, klassName.substring(klassName.lastIndexOf(File.separatorChar) + 1)
                + ".class");
        try {
            f.createNewFile();
            OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
            try {
                ClassWriter cw = new ClassWriter(kls, os);
                cw.write();
            } finally {
                os.close();
            }
        } catch(IOException exp) {
            exp.printStackTrace();
        }
    }

    private boolean canInclude(InstanceKlass kls) {
        return kls.getName().asString().indexOf(pattern)>-1;
    }

    public static void main(String[] args) {
        ClassDumper cd = new ClassDumper();
        cd.start(args);
        cd.stop();
    }
}

