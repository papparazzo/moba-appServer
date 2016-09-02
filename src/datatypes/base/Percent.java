/*
 *  moba-appServer
 *
 *  Copyright (C) 2016 stefan
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/agpl.txt>.
 *
 */

package datatypes.base;

import java.io.IOException;

import json.JSONException;
import json.JSONToStringI;

public class Percent implements JSONToStringI {
    protected int value = 0;

    public Percent() {

    }

    public Percent(int val) {
        this.setValue(val);
    }

    public final void setValue(int val)
    throws IllegalArgumentException {
        if(val > 100 || val < 0) {
            throw new IllegalArgumentException();
        }
        this.value = val;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public String toJsonString(boolean formated, int indent)
    throws JSONException, IOException {
        return String.valueOf(this.value);
    }
}