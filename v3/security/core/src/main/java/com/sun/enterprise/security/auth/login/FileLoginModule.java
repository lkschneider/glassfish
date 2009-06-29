/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.enterprise.security.auth.login;

import java.util.*;
import java.util.logging.Level;
import javax.security.auth.*;
import javax.security.auth.callback.*;
import javax.security.auth.login.*;
import javax.security.auth.spi.*;
import com.sun.enterprise.security.auth.realm.file.FileRealm;
import javax.security.auth.login.LoginException;

/**
 * File realm login module.
 *
 * <P>Provides a file-based implementation of a password login module.
 * Processing is delegated to the FileRealm class.
 *
 * @see com.sun.enterprise.security.auth.login.PasswordLoginModule
 * @see com.sun.enterprise.security.auth.realm.file.FileRealm
 *
 */
public class FileLoginModule extends PasswordLoginModule
{

    /**
     * Perform file authentication. Delegates to FileRealm.
     *
     * @throws LoginException If login fails (JAAS login() behavior).
     *
     */
    protected void authenticate()
        throws LoginException
    {
        if (!(_currentRealm instanceof FileRealm)) {
            String msg = sm.getString("filelm.badrealm");
            throw new LoginException(msg);
        }
        FileRealm fileRealm = (FileRealm)_currentRealm;

        String[] grpList = fileRealm.authenticate(_username, _password);

        if (grpList == null) {  // JAAS behavior
            String msg = sm.getString("filelm.faillogin", _username);
            throw new LoginException(msg);
        }

        if (_logger.isLoggable(Level.FINE)) {
            _logger.log(Level.FINE, "File login succeeded for: " + _username);
        }
        //make a copy of groupList to pass to LoginModule. This copy is the one
        // that will be made null there. DO NOT PASS the grpList as is - as 
        // it will get overwritten. Resulting in logins passing only once.
        String[] groupListToForward = new String[grpList.length];
        for (int i = 0; i< grpList.length; i++){
            groupListToForward[i] = grpList[i];
        }
        commitAuthentication(_username, _password,
                             _currentRealm, groupListToForward);
    }
}
