package com.kayo.android.aspectj

import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.GradleException
import org.gradle.api.Project

/**
 * Aspectj 任务配置
 */
@SuppressWarnings("GroovyUnusedDeclaration")
class AspectjWork {
    Project project
    String encoding
    ArrayList<File> inPath = new ArrayList<File>()
    ArrayList<File> aspectPath = new ArrayList<File>()
    ArrayList<File> classPath = new ArrayList<File>()
    String bootClassPath
    String sourceCompatibility
    String targetCompatibility
    String destinationDir
    List<String> ajcArgs

    AspectjWork(Project proj) {
        project = proj
    }

    void doWork() {
        if (project.aspectj.trackLog){
            println "Aspectj Do Work.........."
        }
        final def log = project.logger

        //http://www.eclipse.org/aspectj/doc/released/devguide/ajc-ref.html
        //
        // -sourceRoots:
        //  Find and build all .java or .aj source files under any directory listed in DirPaths. DirPaths, like classpath, is a single argument containing a list of paths to directories, delimited by the platform- specific classpath delimiter. Required by -incremental.
        // -inPath:
        //  Accept as source bytecode any .class files in the .jar files or directories on Path. The output will include these classes, possibly as woven with any applicable aspects. Path is a single argument containing a list of paths to zip files or directories, delimited by the platform-specific path delimiter.
        // -classpath:
        //  Specify where to find user class files. Path is a single argument containing a list of paths to zip files or directories, delimited by the platform-specific path delimiter.
        // -aspectPath:
        //  Weave binary aspects from jar files and directories on path into all sources. The aspects should have been output by the same version of the compiler. When running the output classes, the run classpath should contain all aspectPath entries. Path, like classpath, is a single argument containing a list of paths to jar files, delimited by the platform- specific classpath delimiter.
        // -bootclasspath:
        //  Override location of VM's bootclasspath for purposes of evaluating types when compiling. Path is a single argument containing a list of paths to zip files or directories, delimited by the platform-specific path delimiter.
        // -d:
        //  Specify where to place generated .class files. If not specified, Directory defaults to the current working dir.
        // -preserveAllLocals:
        //  Preserve all local variables during code generation (to facilitate debugging).

        def args = [
                "-showWeaveInfo",
                "-encoding", encoding,
                "-source", sourceCompatibility,
                "-target", targetCompatibility,
                "-d", destinationDir,
                "-classpath", classPath.join(File.pathSeparator),
                "-bootclasspath", bootClassPath
        ]

        if (!getInPath().isEmpty()) {
            args << '-inpath'
            args << getInPath().join(File.pathSeparator)
        }
        if (!getAspectPath().isEmpty()) {
            args << '-aspectpath'
            args << getAspectPath().join(File.pathSeparator)
        }

        if (ajcArgs != null && !ajcArgs.isEmpty()) {
            if (!ajcArgs.contains('-Xlint')) {
                args.add('-Xlint:ignore')
            }
            if (!ajcArgs.contains('-warn')) {
                args.add('-warn:none')
            }

            args.addAll(ajcArgs)
        } else {
            args.add('-Xlint:ignore')
            args.add('-warn:none')
        }

        MessageHandler handler = new MessageHandler(true)
        Main m = new Main()
        m.run(args as String[], handler)
        for (IMessage message : handler.getMessages(null, true)) {
            switch (message.getKind()) {
                case IMessage.ABORT:
                case IMessage.ERROR:
                case IMessage.FAIL:
                    log.error message.message, message.thrown
                    throw new GradleException(message.message, message.thrown)
                case IMessage.WARNING:
                    log.warn message.message, message.thrown
                    break
                case IMessage.INFO:
                    log.info message.message, message.thrown
                    break
                case IMessage.DEBUG:
                    log.debug message.message, message.thrown
                    break
            }
        }

        m.quit()
    }


    ArrayList<File> getInPath() {
        return inPath
    }

    ArrayList<File> getClassPath() {
        return classPath
    }

    ArrayList<File> getAspectPath() {
        return aspectPath
    }

    String getEncoding() {
        return encoding
    }

    String getBootClassPath() {
        return bootClassPath
    }

    String getDestinationDir() {
        return destinationDir
    }

    String getTargetCompatibility() {
        return targetCompatibility
    }

    String getSourceCompatibility() {
        return sourceCompatibility
    }

    void setEncoding(String encoding) {
        this.encoding = encoding
    }

    void setAjcArgs(List<String> ajcArgs) {
        this.ajcArgs = ajcArgs
    }

    void setInPath(ArrayList<File> inPath) {
        this.inPath = inPath
    }

    void setClassPath(ArrayList<File> classPath) {
        this.classPath = classPath
    }

    void setAspectPath(ArrayList<File> aspectPath) {
        this.aspectPath = aspectPath
    }

    void setBootClassPath(String bootClassPath) {
        this.bootClassPath = bootClassPath
    }

    void setDestinationDir(String destinationDir) {
        this.destinationDir = destinationDir
    }

    void setSourceCompatibility(String sourceCompatibility) {
        this.sourceCompatibility = sourceCompatibility
    }

    void setTargetCompatibility(String targetCompatibility) {
        this.targetCompatibility = targetCompatibility
    }
}