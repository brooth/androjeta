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
import android.app.Fragment;
import android.content.Context;
import org.brooth.jeta.MasterController;
import org.brooth.jeta.metasitory.Metasitory;

/**
 * @author Oleg Khalidov (brooth@gmail.com)
 */
public class FindViewController extends MasterController<Object, FindViewMetacode<Object>> {

    public FindViewController(Metasitory metasitory, Object master) {
        super(metasitory, master, FindView.class);
    }

    public void findViews() {
        if (master instanceof Activity) {
            findViews((Activity) master);
            return;
        }

        throw new IllegalStateException("Master is not an activity, use findViews(Activity activity) instead");
    }

    public void findViews(Activity activity) {
        for (FindViewMetacode<Object> metacode : metacodes)
            metacode.applyFindViews(master, activity);
    }
}
