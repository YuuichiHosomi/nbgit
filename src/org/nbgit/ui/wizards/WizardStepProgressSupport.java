/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2009 Sun
 * Microsystems, Inc. All Rights Reserved.
 * Portions Copyright 2009 Alexander Coles (Ikonoklastik Productions).
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
package org.nbgit.ui.wizards;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.nbgit.GitProgressSupport;
import org.openide.util.Cancellable;
import org.openide.util.NbBundle;
import static org.jdesktop.layout.GroupLayout.LEADING;
import static org.jdesktop.layout.LayoutStyle.RELATED;

/**
 *
 * @author Tomas Stupka
 * @author Marian Petras
 */
public abstract class WizardStepProgressSupport extends GitProgressSupport implements Runnable, Cancellable {

    private JComponent progressComponent;
    private JLabel progressLabel;
    private JComponent progressBar;
    private JButton stopButton;
    private JComponent progressLine;

    public WizardStepProgressSupport() {
    }

    public abstract void setEditable(boolean bl);

    @Override
    public JComponent getProgressComponent() {
        if (progressComponent == null) {
            progressComponent = createProgressComponent();
        }
        return progressComponent;
    }

    private JComponent createProgressComponent() {
        progressLabel = new JLabel(getDisplayName());

        progressBar = super.getProgressComponent();

        stopButton = new JButton(NbBundle.getMessage(WizardStepProgressSupport.class, "BK2022")); // NOI18N
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });

        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);

        progressLine = new JPanel();
        progressLine.add(progressBar);
        progressLine.add(Box.createHorizontalStrut(
                                LayoutStyle.getSharedInstance()
                                .getPreferredGap(progressBar,
                                                 stopButton,
                                                 RELATED,
                                                 SwingConstants.EAST,
                                                 progressLine)));
        progressLine.add(stopButton);

        progressLine.setLayout(new BoxLayout(progressLine, BoxLayout.X_AXIS));
        progressBar.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        stopButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        layout.setHorizontalGroup(
                layout.createParallelGroup(LEADING)
                .add(progressLabel)
                .add(progressLine));
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .add(progressLabel)
                .addPreferredGap(RELATED)
                .add(progressLine));
        panel.setLayout(layout);

        layout.setHonorsVisibility(false);   //hiding should not affect prefsize

        progressLabel.setVisible(false);
        progressLine.setVisible(false);

        return panel;
    }

    @Override
    public void startProgress() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressLabel.setVisible(true);
                progressLine.setVisible(true);

                WizardStepProgressSupport.super.startProgress();
            }
        });
    }

    @Override
    protected void finnishProgress() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                WizardStepProgressSupport.super.finnishProgress();

                progressLabel.setVisible(false);
                progressLine.setVisible(false);

                setEditable(true);
            }
        });
    }

    @Override
    public void setDisplayName(String displayName) {
        if(progressLabel != null) progressLabel.setText(displayName);
        super.setDisplayName(displayName);
    }

    @Override
    public synchronized boolean cancel() {
        if(stopButton!=null) stopButton.setEnabled(false);
        setDisplayName(org.openide.util.NbBundle.getMessage(WizardStepProgressSupport.class, "MSG_Progress_Terminating"));
        return super.cancel();
    }

}
