/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Jonas Fonseca <fonseca@diku.dk>
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
package org.nbgit.ui.browser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.util.NbBundle;
import org.spearce.jgit.lib.ObjectId;
import org.spearce.jgit.lib.PersonIdent;
import org.spearce.jgit.lib.Repository;
import org.spearce.jgit.revplot.PlotWalk;
import org.spearce.jgit.revwalk.RevCommit;
import org.spearce.jgit.revwalk.RevObject;
import org.spearce.jgit.revwalk.RevSort;

/**
 * Control behavior of a repository browser.
 */
public class BrowserController implements ListSelectionListener {

    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final BrowserModel model;

    public BrowserController(BrowserTopComponent browser, BrowserModel model) {
        this.model = model;
        browser.addListSelectionListener(this);
    }

    public void show(final Repository repo, final String revision) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                model.getCommitList().clear();
                PlotWalk walk = null;
                try {
                    ObjectId start = repo.resolve(revision);
                    walk = new PlotWalk(repo);
                    walk.sort(RevSort.BOUNDARY, true);
                    walk.markStart(walk.parseCommit(start));
                    model.getCommitList().source(walk);
                    model.getCommitList().fillTo(Integer.MAX_VALUE);
                } catch (Throwable error) {
                    model.setContent(error.getMessage());
                } finally {
                    if (walk != null) {
                        walk.dispose();
                    }
                }
            }
        });
    }

    public void valueChanged(ListSelectionEvent event) {
        ListSelectionModel listModel = (ListSelectionModel) event.getSource();
        for (int i = event.getFirstIndex(); i <= event.getLastIndex(); i++) {
            if (listModel.isSelectedIndex(i)) {
                RevObject object = model.getCommitList().get(i);
                model.setContentId(object.name());
                if (object instanceof RevCommit) {
                    model.setContent(toString((RevCommit) object));
                }
            }
        }
    }

    private String toString(RevCommit commit) {
        StringBuilder str = new StringBuilder();
        str.append(formatIdent("Author", commit.getAuthorIdent())). // NOI18N
                append("\n"); // NOI18N
        str.append(formatIdent("Committer", commit.getCommitterIdent())). // NOI18N
                append("\n"); // NOI18N
        for (RevCommit parent : commit.getParents()) {
            str.append(NbBundle.getMessage(BrowserController.class, "Parent", // NOI18N
                    parent.name(), parent.getShortMessage())).
                    append("\n"); // NOI18N
            }
        str.append("\n"); // NOI18N
        str.append(commit.getFullMessage());
        return str.toString();
    }

    private String formatIdent(String msgId, PersonIdent ident) {
        String str = ident.getName() + " <" + ident.getEmailAddress() + "> " + // NOI18N
                dateFormat.format(ident.getWhen()) + " " + // NOI18N
                ident.getTimeZone().getDisplayName();
        return NbBundle.getMessage(BrowserController.class, msgId, str);
    }
}
