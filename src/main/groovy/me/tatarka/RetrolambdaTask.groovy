package me.tatarka
import org.gradle.api.DefaultTask
import org.gradle.api.JavaVersion
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.TaskAction

import static me.tatarka.RetrolambdaPlugin.javaVersionToBytecode
/**
 * Created by evan on 3/4/14.
 */
class RetrolambdaTask extends DefaultTask {
    Object inputDir
    Object outputDir
    FileCollection classpath
    JavaVersion javaVersion = JavaVersion.VERSION_1_6

    @TaskAction
    def run() {
        project.javaexec {
            // Ensure retrolambda runs on java8
            if (!project.retrolambda.onJava8) {
                def java = "${project.retrolambda.tryGetJdk()}/bin/java"
                if (!checkIfExecutableExists(java)) {
                    throw new ProjectConfigurationException("Cannot find executable: $java", null)
                }
                executable java
            }

            def bytecodeVersion = javaVersionToBytecode(javaVersion)

            classpath = project.files(project.configurations.retrolambdaConfig)
            main = 'net.orfjackal.retrolambda.Main'
            jvmArgs = [
                    "-Dretrolambda.inputDir=$inputDir",
                    "-Dretrolambda.outputDir=$outputDir",
                    "-Dretrolambda.classpath=${this.classpath.asPath}",
                    "-Dretrolambda.bytecodeVersion=$bytecodeVersion",
                    "-javaagent:$classpath.asPath"
            ]

            logging.captureStandardOutput(LogLevel.INFO)
        }
    }
}
