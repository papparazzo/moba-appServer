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

import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Logger;

import com.SenderI;
import datatypes.enumerations.HardwareState;
import datatypes.enumerations.NoticeType;
import datatypes.objects.NoticeData;
import messages.Message;
import messages.MessageHandlerA;
import messages.MessageType;

public class Systems extends MessageHandlerA {
    protected static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    protected HardwareState status = HardwareState.ERROR;
    protected SenderI dispatcher = null;
    protected boolean emergencyStop = false;
    protected PriorityBlockingQueue<Message> in = null;

    public Systems(SenderI dispatcher, PriorityBlockingQueue<Message> in) {
        this.dispatcher = dispatcher;
        this.in = in;
    }

    @Override
    public void handleMsg(Message msg) {
        switch(msg.getMsgType()) {
            case EMERGENCY_STOP:
                this.emergencyStop();
                break;

            case EMERGENCY_STOP_CLEARING:
                this.emergencyStopClearing();
                break;

            case GET_HARDWARE_STATE:
                this.dispatcher.dispatch(
                    new Message(
                        MessageType.SET_HARDWARE_STATE,
                        this.status.toString(),
                        msg.getEndpoint()
                    )
                );
                break;

            case SET_HARDWARE_STATE:
                if(this.setHardwareState(
                    HardwareState.valueOf((String)msg.getData())
                )) {
                    this.dispatcher.dispatch(
                        new Message(
                            MessageType.HARDWARE_STATE_CHANGED,
                            this.status.toString()
                        )
                    );
                }
                break;

            case HARDWARE_SHUTDOWN:
                this.in.add(new Message(MessageType.SERVER_SHUTDOWN));
                break;

            case HARDWARE_RESET:
                this.in.add(new Message(MessageType.SERVER_RESET));
                break;

            case HARDWARE_SWITCH_STANDBY:
                this.setHardwareSwitchStandby();
                break;

            default:
                throw new UnsupportedOperationException(
                    "unknow msg <" + msg.getMsgType().toString() + ">."
                );
        }
    }

    public HardwareState getHardwareState() {
        return this.status;
    }

    public boolean getEmergencyStopStatus() {
        return this.emergencyStop;
    }

    protected boolean setHardwareState(HardwareState state) {
        switch(state) {
            case READY:
                if(this.emergencyStop) {
                    return false;
                }
            case ERROR:
            case STANDBY:
            case POWER_OFF:
                if(this.status == state) {
                    return false;
                }
                this.status = state;
                return true;

            default:
                throw new UnsupportedOperationException(
                    "unknow state <" + state.toString() + ">."
                );
        }
    }

    protected void setHardwareSwitchStandby() {
        switch(this.status) {
            case ERROR:
                return;

            case READY:
            case POWER_OFF:
                this.status = HardwareState.STANDBY;
                break;

            case STANDBY:
                if(this.emergencyStop) {
                    this.status = HardwareState.POWER_OFF;
                    break;
                }
                this.status = HardwareState.READY;
                break;
        }
        this.dispatcher.dispatch(
            new Message(
                MessageType.HARDWARE_STATE_CHANGED,
                this.status.toString()
            )
        );
    }

    public void emergencyStop() {
        if(this.emergencyStop) {
            return;
        }
        this.dispatcher.dispatch(new Message(
                MessageType.SYSTEM_NOTICE,
                new NoticeData(
                    NoticeType.WARNING,
                    "Nothalt gedrückt",
                    "Es wurde ein Nothalt ausgelöst"
                )
            )
        );
        this.emergencyStop = true;
        if(this.status == HardwareState.READY) {
            this.status = HardwareState.POWER_OFF;
            this.dispatcher.dispatch(
                new Message(
                    MessageType.HARDWARE_STATE_CHANGED,
                    this.status.toString()
                )
            );
        }
    }

    public void emergencyStopClearing() {
        if(!this.emergencyStop) {
            return;
        }
        this.dispatcher.dispatch(new Message(
                MessageType.SYSTEM_NOTICE,
                new NoticeData(
                    NoticeType.INFO,
                    "Nothalt freigabe",
                    "Es wurde ein Nothalt freigegeben"
                )
            )
        );
        this.emergencyStop = false;
    }
}