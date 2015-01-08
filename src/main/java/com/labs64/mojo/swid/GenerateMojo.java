/*
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

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
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
 * A mojo that generates a SWID tag from a given POM.
 * 
 * @see <a href="http://l64.cc/swid">SoftWare IDentification (SWID) Tags Generator</a>
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class GenerateMojo extends AbstractMojo {

    @Component
    private org.apache.maven.project.MavenProject project;

    /**
     * Specifies the destination directory where the generated SWID tags files will be saved.
     * 
     * @since 1.0.0
     */
    @Parameter(property = "swid.outputDirectory", required = true, defaultValue = "${project.build.directory}/swid")
    private File outputDirectory;

    /**
     * Specifies the encoding of the generated SWID tags files. If not specified, the encoding value will be UTF-8.
     * 
     * @since 1.0.0
     */
    @Parameter(property = "swid.encoding", required = false, defaultValue = "${project.build.sourceEncoding}")
    private String encoding;

    /**
     * Specifies the extension of the generated SWID tags files.
     * 
     * @since 1.0.0
     */
    @Parameter(property = "swid.extension", required = false, defaultValue = "swidtag")
    private String extension;

    /**
     * Specifies if an entitlement is required to reconcile this product.
     * 
     * @since 1.0.0
     */
    @Parameter(property = "swid.entitlement_required", required = false, defaultValue = "false")
    private Boolean entitlement_required;

    /**
     * Specifies product title.
     * 
     * @since 1.0.0
     */
    @Parameter(property = "swid.product_title", required = false, defaultValue = "${project.name}")
    private String product_title;

    /**
     * Specifies software creator attributes. <br/>
     * <b>Default values:</b>
     * 
     * <pre>
     * &lt;software_creator&gt;
     *     &lt;name&gt;${project.organization.name}&lt;/name&gt;
     *     &lt;regid&gt;${project.organization.url}&lt;/regid&gt;
     * &lt;/software_creator&gt;
     * </pre>
     * 
     * @since 1.0.0
     */
    @Parameter(property = "swid.software_creator", required = false)
    private RegId software_creator;

    /**
     * Specifies software licensor attributes. <br/>
     * <b>Default values:</b>
     * 
     * <pre>
     * &lt;software_licensor&gt;
     *     &lt;name&gt;${project.organization.name}&lt;/name&gt;
     *     &lt;regid&gt;${project.organization.url}&lt;/regid&gt;
     * &lt;/software_licensor&gt;
     * </pre>
     * 
     * @since 1.0.0
     */
    @Parameter(property = "swid.software_licensor", required = false)
    private RegId software_licensor;

    /**
     * Specifies software identifier attributes. <br/>
     * <b>Default values:</b>
     * 
     * <pre>
     * &lt;software_id&gt;
     *     &lt;unique_id&gt;${project.organization.name}&lt;/unique_id&gt;
     *     &lt;tag_creator_regid&gt;${project.organization.url}&lt;/tag_creator_regid&gt;
     * &lt;/software_id&gt;
     * </pre>
     * 
     * @since 1.0.0
     */
    @Parameter(property = "swid.software_id", required = false)
    private RegId software_id;

    /**
     * Specifies tag creator attributes. <br/>
     * <b>Default values:</b>
     * 
     * <pre>
     * &lt;tag_creator&gt;
     *     &lt;name&gt;${project.organization.name}&lt;/name&gt;
     *     &lt;regid&gt;${project.organization.url}&lt;/regid&gt;
     * &lt;/tag_creator&gt;
     * </pre>
     * 
     * @since 1.0.0
     */
    @Parameter(property = "swid.tag_creator", required = false)
    private RegId tag_creator;

    public void execute() throws MojoExecutionException {
        getLog().debug("Generate SWID Tag...");

        File dir = outputDirectory;
        if (!dir.exists()) {
            dir.mkdirs();
        }

        final String regId = SwidUtils.generateRegId("2010-04", "com.labs64");
        // Parse a version String and add the components to a properties object.
        String projectVersion = project.getVersion();
        if (ArtifactUtils.isSnapshot(projectVersion)) {
            projectVersion = StringUtils.substring(projectVersion, 0, projectVersion.length()
                    - Artifact.SNAPSHOT_VERSION.length() - 1);
        }
        ArtifactVersion artifactVersion = new DefaultArtifactVersion(projectVersion);

        final String fileName = SwidUtils.generateSwidFileName(regId,
                project.getArtifactId(),
                projectVersion,
                extension);
        File touch = new File(dir, fileName);

        // prepare SWID Tag processor
        SwidProcessor processor = new DefaultSwidProcessor();
        ((DefaultSwidProcessor) processor).setEntitlementRequiredIndicator(entitlement_required)
                .setProductTitle(product_title)
                // TODO
                .setProductVersion(projectVersion,
                        artifactVersion.getMajorVersion(),
                        artifactVersion.getMinorVersion(),
                        artifactVersion.getIncrementalVersion(),
                        artifactVersion.getBuildNumber())
                .setSoftwareCreator(software_creator.getName(), software_creator.getRegid())
                .setSoftwareLicensor(software_licensor.getName(), software_licensor.getRegid())
                .setSoftwareId(software_id.getUnique_id(), software_id.getTag_creator_regid())
                .setTagCreator(tag_creator.getName(), tag_creator.getRegid());

        // create builder and pass processor as build param
        SwidBuilder builder = new SwidBuilder();
        SoftwareIdentificationTagComplexType swidTag = builder.build(processor);

        // output resulting object
        SwidWriter writer = new SwidWriter();
        writer.write(swidTag, touch);
    }

}
