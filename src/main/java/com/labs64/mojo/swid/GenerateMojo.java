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
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.iso.standards.iso._19770.__2._2014_dis.schema.SoftwareIdentity;
import org.iso.standards.iso._19770.__2._2014_dis.schema.VersionScheme;

import com.labs64.mojo.swid.configuration.RegId;
import com.labs64.utils.swid.SwidBuilder;
import com.labs64.utils.swid.io.SwidWriter;
import com.labs64.utils.swid.processor.DefaultSwidProcessor;
import com.labs64.utils.swid.support.SwidUtils;

/**
 * A mojo that generates a SWID tag from a given POM.
 * 
 * @see <a href="http://l64.cc/swid">SoftWare IDentification (SWID) Tags Generator</a>
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class GenerateMojo extends AbstractSwidMojo {

    /**
     * Specifies the destination directory where the generated SWID tags files will be saved.
     * 
     * @since 1.0.0
     */
    @Parameter(property = "swid.outputDirectory", required = true, defaultValue = "${project.build.directory}/generated-resources/swid")
    private File outputDirectory;

    /**
     * Specifies the encoding of the generated SWID tags files.
     * 
     * <br/>
     * <b>Fallback value(s):</b> <tt>UTF-8</tt>
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
     * <br/>
     * <b>Fallback value(s):</b> <tt>${project.artifactId}</tt>
     * 
     * @since 1.0.0
     */
    @Parameter(property = "swid.product_title", required = false, defaultValue = "${project.name}")
    private String product_title;

    /**
     * Specifies product version.
     * 
     * @since 1.0.0
     */
    @Parameter(property = "swid.product_version", required = false, defaultValue = "${project.version}")
    private String product_version;

    /**
     * Specifies domain creation date which belongs to the software creator. <br/>
     * Format: <code>'yyyy-MM'</code> <br/>
     * Example: <code>'2010-04'</code><br/>
     * Default value: <tt>current date</tt>
     * 
     * @since 1.0.0
     */
    @Parameter(property = "swid.domain_creation_date", required = false)
    private String domain_creation_date;

    /**
     * Specifies software creator attributes.
     * 
     * <br/>
     * <b>Fallback value(s):</b> name: <tt>${project.groupId}</tt>; regid: <tt>${project.url}, ${project.groupId}</tt><br/>
     * <b>Default value(s):</b>
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
    @Parameter(required = false, defaultValue = "${software_creator}")
    private RegId software_creator;

    /**
     * Specifies software licensor attributes.
     * 
     * <br/>
     * <b>Fallback value(s):</b> name: <tt>${project.groupId}</tt>; regid: <tt>${project.url}, ${project.groupId}</tt><br/>
     * <b>Default value(s):</b>
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
    @Parameter(required = false, defaultValue = "${software_licensor}")
    private RegId software_licensor;

    /**
     * Specifies software identifier attributes.
     * 
     * <br/>
     * <b>Fallback value(s):</b> tag_creator_regid: <tt>${project.url}, ${project.groupId}</tt><br/>
     * <b>Default value(s):</b>
     * 
     * <pre>
     * &lt;software_id&gt;
     *     &lt;unique_id&gt;${project.artifactId}&lt;/unique_id&gt;
     *     &lt;tag_creator_regid&gt;${project.organization.url}&lt;/tag_creator_regid&gt;
     * &lt;/software_id&gt;
     * </pre>
     * 
     * @since 1.0.0
     */
    @Parameter(required = false, defaultValue = "${software_id}")
    private RegId software_id;

    /**
     * Specifies tag creator attributes.
     * 
     * <br/>
     * <b>Fallback value(s):</b> name: <tt>${project.groupId}</tt>; regid: <tt>${project.url}, ${project.groupId}</tt><br/>
     * <b>Default value(s):</b>
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
    @Parameter(required = false, defaultValue = "${tag_creator}")
    private RegId tag_creator;

    public void execute() throws MojoExecutionException {
        getLog().debug("Generate SWID Tag...");

        // prepare mandatory elements
        ArtifactVersion artifactVersion = getArtifactVersion();
        prepareMandatoryElements();

        // prepare SWID Tag processor
        DefaultSwidProcessor processor = new DefaultSwidProcessor();
        //processor.setEntitlementRequiredIndicator(entitlement_required)
        processor.setName(product_title)
        .setVersionScheme(VersionScheme.ALPHANUMERIC)
                .setVersion(artifactVersion.getQualifier());
                /** TODO: use new 2015 spec structure
                .setSoftwareCreator(software_creator.getName(), software_creator.getRegid())
                .setSoftwareLicensor(software_licensor.getName(), software_licensor.getRegid())
                .setSoftwareId(software_id.getUnique_id(), software_id.getTag_creator_regid())
                .setTagCreator(tag_creator.getName(), tag_creator.getRegid());
                 */

        // create builder and pass processor as build param
        SwidBuilder builder = new SwidBuilder();
        SoftwareIdentity swidTag = builder.build(processor);

        // output resulting object
        final String fileName = SwidUtils.generateSwidFileName(
                software_creator.getRegid(),
                software_id.getUnique_id(),
                product_version,
                extension);
        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                throw new MojoExecutionException("Cannot create directory '" + outputDirectory.toString() + "'");
            }
        }
        File swidFile = new File(outputDirectory, fileName);
        SwidWriter writer = new SwidWriter();
        writer.write(swidTag, swidFile);
    }

    private ArtifactVersion getArtifactVersion() {
        if (ArtifactUtils.isSnapshot(product_version)) {
            product_version = StringUtils.substring(product_version, 0, product_version.length()
                    - Artifact.SNAPSHOT_VERSION.length() - 1);
        }
        return new DefaultArtifactVersion(product_version);
    }

    private String getDomainDate() {
        if (StringUtils.isBlank(domain_creation_date)) {
            domain_creation_date = SwidUtils.generateDomainDate(new Date());
        }
        return domain_creation_date;
    }

    private RegId getDefaultRegId() {
        final RegId regid = new RegId();

        regid.setName(getProject().getOrganization() == null ?
                getProject().getGroupId() : getProject().getOrganization().getName());

        final String url = getProject().getOrganization() == null ?
                getProject().getUrl() : getProject().getOrganization().getUrl();
        final String reverseDomainName = StringUtils.isBlank(url) ?
                getProject().getGroupId() : SwidUtils.revertDomainName(url);
        regid.setRegid(SwidUtils.generateRegId(getDomainDate(), reverseDomainName));

        return regid;
    }

    private void prepareMandatoryElements() {
        final RegId defaultRegId = getDefaultRegId();

        // software_creator
        if (software_creator == null) {
            software_creator = new RegId();
        }
        if (StringUtils.isBlank(software_creator.getName())) {
            software_creator.setName(defaultRegId.getName());
        }
        if (StringUtils.isBlank(software_creator.getRegid())) {
            software_creator.setRegid(defaultRegId.getRegid());
        }

        // software_licensor
        if (software_licensor == null) {
            software_licensor = new RegId();
        }
        if (StringUtils.isBlank(software_licensor.getName())) {
            software_licensor.setName(defaultRegId.getName());
        }
        if (StringUtils.isBlank(software_licensor.getRegid())) {
            software_licensor.setRegid(defaultRegId.getRegid());
        }

        // tag_creator
        if (tag_creator == null) {
            tag_creator = new RegId();
        }
        if (StringUtils.isBlank(tag_creator.getName())) {
            tag_creator.setName(defaultRegId.getName());
        }
        if (StringUtils.isBlank(tag_creator.getRegid())) {
            tag_creator.setRegid(defaultRegId.getRegid());
        }

        // software_id
        if (software_id == null) {
            software_id = new RegId();
        }
        if (StringUtils.isBlank(software_id.getUnique_id())) {
            software_id.setUnique_id(getProject().getArtifactId());
        }
        if (StringUtils.isBlank(software_id.getTag_creator_regid())) {
            software_id.setTag_creator_regid(tag_creator.getRegid());
        }
    }

}
