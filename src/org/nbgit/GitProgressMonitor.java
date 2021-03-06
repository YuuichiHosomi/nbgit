/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Alexander Coles (Ikonoklastik Productions).
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file.
 *
 * This particular file is subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.nbgit;

import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.eclipse.jgit.lib.ProgressMonitor;

/**
 *
 * @author alexbcoles
 */
public class GitProgressMonitor implements ProgressMonitor {

    //private final GitProgressSupport support;
    private final AggregateProgressHandle rootHandle;
    private ProgressContributor contributor;
    private boolean started = false;

    public GitProgressMonitor(/*final GitProgressSupport support*/) {
        //this.support = support;
                                                                   // TODO: make this cancellable
        rootHandle = AggregateProgressFactory.createHandle("Task", null, null, null);
    }

    public void start(int totalTasks) {
        if (!started) {
            rootHandle.start();
            started = true;
        }
    }

    public void beginTask(String title, int total) {
        endTask();

        contributor = AggregateProgressFactory.createProgressContributor(title + new java.util.Random());
        rootHandle.addContributor(contributor);

        if (total == UNKNOWN) {
            contributor.start(1000);
        } else {
            contributor.start(total);
        }
    }

    public void update(int completed) {
        if (contributor == null) {
            return;
        }

        contributor.progress(completed);
    }

    public void endTask() {
        if (contributor != null) {
            try {
                contributor.finish();
            } finally {
                contributor = null;
            }
        }
    }

    public boolean isCancelled() {
        // TODO: figure out how to implement this
        return false;

        //if (contributor != null) {
        //
        //}
    }

}
