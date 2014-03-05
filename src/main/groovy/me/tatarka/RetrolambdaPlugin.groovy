/**
 Copyright 2014 Evan Tatarka

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package me.tatarka

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created with IntelliJ IDEA.
 * User: evan
 * Date: 8/4/13
 * Time: 1:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class RetrolambdaPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.extensions.create('retrolambda', RetrolambdaExtension)

        project.configurations {
            retrolambdaConfig
        }

        project.dependencies {
            retrolambdaConfig project.retrolambda.compile
        }

        project.task('compileRetrolambda', dependsOn: project.tasks.matching {task ->
            !task.name.equals('compileRetrolambda') && task.name.startsWith('compileRetrolambda')
        })  {
            description = "Converts all java 8 class files to java 6 or 7"
        }

        if (project.plugins.hasPlugin('java')) {
            project.apply plugin: RetrolambdaPluginJava
        }

        if (project.plugins.hasPlugin('android') || project.plugins.hasPlugin('android-library')) {
            project.apply plugin: RetrolambdaPluginAndroid
        }

        if (project.plugins.hasPlugin('application')) {
            project.tasks.findByName('run').dependsOn('compileRetrolambda')
        }
    }

    /**
     * Checks if executable file exists, in MS Windows executables has suffix `.exe'
     * @param file
     * @return
     */
    static String checkIfExecutableExists(String file){
        new File(file).exists()||new File(file+'.exe').exists()
    }

    static int javaVersionToBytecode(JavaVersion javaVersion) {
        switch (javaVersion.majorVersion) {
            case '6': return 50
            case '7': return 51
            default:
                throw new RuntimeException("Unknown java version: $javaVersion, " +
                        "only 6 or 7 are accepted")
        }
    }
}
