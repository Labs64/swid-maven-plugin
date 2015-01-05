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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.iso.standards.iso._19770.__2._2009.schema.SoftwareIdentificationTagComplexType;

import com.labs64.utils.swid.SwidBuilder;
import com.labs64.utils.swid.io.SwidWriter;
import com.labs64.utils.swid.processor.DefaultSwidProcessor;
import com.labs64.utils.swid.processor.SwidProcessor;
import com.labs64.utils.swid.support.SwidUtils;

/**
 * Goal which generates SWID Tag for a given POM.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class GenerateMojo extends AbstractMojo {

    @Component
    private org.apache.maven.project.MavenProject project;

    /**
     * Location of the generated SWID tags.
     */
    @Parameter(property = "swid.outputDirectory", required = true, defaultValue = "${project.build.directory}")
    private File outputDirectory;

    /**
     * Encoding for the generated .swidtag files.
     */
    @Parameter(property = "swid.encoding", required = false, defaultValue = "${project.build.sourceEncoding}")
    private String encoding;

    /**
     * Encoding for the generated .swidtag files.
     */
    @Parameter(property = "swid.extension", required = false, defaultValue = "swidtag")
    private String extension;

    public void execute() throws MojoExecutionException {
        getLog().debug("Generate SWID Tag...");

        File dir = outputDirectory;
        if (!dir.exists()) {
            dir.mkdirs();
        }

        final String regId = SwidUtils.generateRegId("2010-04", "com.labs64");

        final String fileName = SwidUtils.generateSwidFileName(regId,
                project.getArtifactId(),
                project.getVersion(),
                extension);
        File touch = new File(dir, fileName);

        // prepare SWID Tag processor
        SwidProcessor processor = new DefaultSwidProcessor();
        ((DefaultSwidProcessor) processor).setEntitlementRequiredIndicator(true)
                .setProductTitle(project.getName())
                .setProductVersion(project.getVersion(), 2, 1, 0, 0)
                .setSoftwareCreator("Labs64", regId)
                .setSoftwareLicensor("Labs64", regId)
                .setSoftwareId("NLIC", regId)
                .setTagCreator("Labs64", regId);

        // create builder and pass processor as build param
        SwidBuilder builder = new SwidBuilder();
        SoftwareIdentificationTagComplexType swidTag = builder.build(processor);

        // output resulting object
        SwidWriter writer = new SwidWriter();
        writer.write(swidTag, touch);
    }

}
