/*
 * RHQ Management Platform
 * Copyright (C) 2005-2011 Red Hat, Inc.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.rhq.pocket;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import org.codehaus.jackson.JsonNode;

/**
 * Handling of the Preferences
 * @author Heiko W. Rupp
 */
public class Preferences extends PreferenceActivity  implements SharedPreferences.OnSharedPreferenceChangeListener {

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
//        getPreferences(MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this);
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        setSummaries(sharedPreferences,"host");
        setSummaries(sharedPreferences,"port");
        setSummaries(sharedPreferences,"username");
        setPasswordSummary(sharedPreferences,findPreference("password"));
    }



    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, String key) {

        final Preference p = setSummaries(sharedPreferences, key);

        if (key.equals("password")) {

            new TalkToServerTask(this,new FinishCallback() {
                @Override
                public void onSuccess(JsonNode result) {
                    setPasswordSummary(sharedPreferences, p);
                }

                @Override
                public void onFailure(Exception e) {
                    p.setSummary(R.string.invalid);
                }
            },"/status").execute();


        }
    }

    private Preference setSummaries(SharedPreferences sharedPreferences, String key) {
        final Preference p = findPreference(key);
        if (key.equals("host")) {
            p.setSummary(sharedPreferences.getString("host",""));
        }

        if (key.equals("port")) {
            p.setSummary(sharedPreferences.getString("port",""));
        }

        if (key.equals("username")) {
            p.setSummary(sharedPreferences.getString("username",""));
        }
        return p;
    }

    private void setPasswordSummary(SharedPreferences sharedPreferences, Preference p) {
        String ril_password = sharedPreferences.getString("password", "");
        if (ril_password.equals(""))
            p.setSummary(R.string.unset);
        else
            p.setSummary(R.string.set);
    }
}
