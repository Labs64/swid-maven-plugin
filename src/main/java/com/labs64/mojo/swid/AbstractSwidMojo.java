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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Abstract base for SWID Mojos.
 */
public abstract class AbstractSwidMojo extends AbstractMojo {

    @Parameter(required = true, readonly = true, defaultValue = "${project}")
    private org.apache.maven.project.MavenProject project;

    @Parameter(required = true, readonly = true, defaultValue = "${mojoExecution}")
    private MojoExecution mojoExecution;

    /**
     * Get the maven project instance.
     * 
     * @return the project instance
     */
    protected final MavenProject getProject() {
        return project;
    }

    /**
     * Get the MOJO execution instance.
     * 
     * @return the MOJO execution instance
     */
    public MojoExecution getMojoExecution() {
        return mojoExecution;
    }

}
