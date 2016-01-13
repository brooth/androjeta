/*
 * Copyright 2015 Oleg Khalidov
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.brooth.androjeta.retain;

import android.os.Bundle;
import com.google.common.base.Preconditions;
import org.brooth.jeta.MasterController;
import org.brooth.jeta.metasitory.Metasitory;

/**
 * @author Oleg Khalidov (brooth@gmail.com)
 */
public class RetainController extends MasterController<Object, RetainMetacode<Object>> {

    public RetainController(Metasitory metasitory, Object master) {
        super(metasitory, master, Retain.class);
    }

    public void save(Bundle outState) {
        Preconditions.checkNotNull(outState);

        for (RetainMetacode<Object> metacode : metacodes)
            metacode.applySaveRetains(master, outState);
    }

    public void restore(Bundle savedInstanceState) {
        Preconditions.checkNotNull(savedInstanceState);

        for (RetainMetacode<Object> metacode : metacodes)
            metacode.applyRestoreRetains(master, savedInstanceState);
    }
}
