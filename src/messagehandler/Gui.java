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

package messagehandler;

import java.util.logging.Logger;

import com.SenderI;
import messages.Message;
import messages.MessageHandlerA;
import messages.MessageType;

public class Gui extends MessageHandlerA {
    protected static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    protected SenderI dispatcher = null;

    public Gui(SenderI dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void handleMsg(Message msg) {
        switch(msg.getMsgType()) {
            case SYSTEM_NOTICE:
                this.sendSystemNotice(msg);
                break;
        }
    }

    public void sendSystemNotice(Message msg) {
        this.dispatcher.dispatch(
            new Message(
                MessageType.SYSTEM_NOTICE,
                msg.getData()
            )
        );
    }
}
