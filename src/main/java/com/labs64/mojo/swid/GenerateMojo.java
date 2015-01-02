/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.labs64.mojo.swid;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.labs64.utils.swid.support.SwidUtils;

/**
 * Goal which generates SWID Tag for a given POM.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class GenerateMojo extends AbstractMojo {

    /**
     * Encoding for the generated .swidtag files.
     */
    @Parameter(property = "encoding", required = false, defaultValue = "${project.build.sourceEncoding}")
    private String encoding;

    /**
     * Location of the generated SWID tags.
     */
    @Parameter(property = "outputDirectory", required = true, defaultValue = "${project.build.directory}")
    private File outputDirectory;

    public void execute() throws MojoExecutionException {
        getLog().info("Generate SWID Tag...");

        File dir = outputDirectory;

        if (!dir.exists()) {
            dir.mkdirs();
        }

        final String regId = SwidUtils.generateRegId("2010-04", "com.labs64");
        final String fileName = SwidUtils.generateSwidFileName(regId, "NetLicensing", "NLIC-210");

        File touch = new File(dir, fileName);

        FileWriter w = null;
        try {
            w = new FileWriter(touch);
            w.write(regId);
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + touch, e);
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

}
