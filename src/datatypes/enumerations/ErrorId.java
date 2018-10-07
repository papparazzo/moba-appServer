/*
 *  Project:    moba-server
 *
 *  Copyright (C) 2016 Stefan Paproth <pappi-@gmx.de>
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

package datatypes.enumerations;

import java.io.IOException;

import json.JSONException;
import json.JSONToStringI;

public enum ErrorId implements JSONToStringI {
    SAME_ORIGIN_NEEDED,
    INVALID_APP_ID,
    FAULTY_MESSAGE,
    INVALID_DATA_SEND,
    DATASET_LOCKED,
    DATASET_MISSING,
    INVALID_STATUS_CHANGE,
    DATABASE_ERROR,
    UNKNOWN_ERROR;

    protected final int value;

    private ErrorId() {
        this.value = ordinal();
    }

    @Override
    public String toJsonString(boolean formated, int indent)
    throws JSONException, IOException {
        StringBuilder b = new StringBuilder();
        b.append('"');
        b.append(ErrorId.values()[this.value].toString());
        b.append('"');
        return b.toString();
    }
}
