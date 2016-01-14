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
package org.brooth.androjeta.ui;

import android.app.Activity;
import org.brooth.jeta.MasterController;
import org.brooth.jeta.metasitory.Criteria;
import org.brooth.jeta.metasitory.Metasitory;

/**
 * @author Oleg Khalidov (brooth@gmail.com)
 */
public class OnClickController extends MasterController<Object, OnClickMetacode<Object>> {

    public OnClickController(Metasitory metasitory, Object master) {
        super(metasitory, master);
    }

    protected Criteria criteria() {
        return new Criteria.Builder()
                .masterEqDeep(this.masterClass)
                .usesAny(OnClick.class)
                .usesAny(OnLongClick.class)
                .build();
    }

    public void addListeners() {
        if (master instanceof Activity) {
            addListeners((Activity) master);
            return;
        }

        throw new IllegalStateException("Master is not an activity, use addListeners(Activity activity) instead");
    }

    public void addListeners(Activity activity) {
        for (OnClickMetacode<Object> metacode : metacodes)
            metacode.applyOnClicks(master, activity);
    }
}