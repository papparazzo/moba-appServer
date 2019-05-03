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

package messages;

import datatypes.enumerations.HardwareState;

public abstract class MessageHandlerA {
    public abstract void handleMsg(Message msg);

    public void freeResources(long appId) {
    }

    public void init()
    throws ExceptionInInitializerError {
    }

    public void shutdown() {
    }

    public void reset() {
        shutdown();
        init();
    }

    public void hardwareStateChanged(HardwareState state) {
    }
}
