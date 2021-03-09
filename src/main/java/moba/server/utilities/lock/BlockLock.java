/*
 *  Project:    moba-server
 *
 *  Copyright (C) 2021 Stefan Paproth <pappi-@gmx.de>
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

package moba.server.utilities.lock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import moba.server.database.Database;
import moba.server.datatypes.enumerations.ErrorId;
import moba.server.utilities.exceptions.ErrorException;

public final class BlockLock extends AbstractLock {

    protected Database database = null;

    public BlockLock(Database database) {
        this.database = database;
    }

    @Override
    public void resetAll() {
        try {
            Connection con = database.getConnection();

            try(PreparedStatement pstmt = con.prepareStatement("UPDATE `BlockSections` SET `Locked` = NULL")) {
                pstmt.executeUpdate();
                getLogger().log(Level.INFO, pstmt.toString());
            }
        } catch(SQLException e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }

    @Override
    public void resetOwn(long appId) {
        try {
            Connection con = database.getConnection();

            try(PreparedStatement pstmt = con.prepareStatement("UPDATE `BlockSections` SET `Locked` = NULL WHERE `Locked` = ?")) {
                pstmt.setLong(1, appId);
                pstmt.executeUpdate();
                getLogger().log(Level.INFO, pstmt.toString());
            }
        } catch(SQLException e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }

    @Override
    public void tryLock(long appId, Object data)
    throws ErrorException {
        try {
            ArrayList<Object> list = (ArrayList<Object>)data;

            if(isLockedByApp(appId, data)) {
                return;
            }

            Connection con = database.getConnection();
            con.setAutoCommit(false);

            String q = "UPDATE `BlockSections` SET `locked` = ? WHERE `locked` IS NULL AND `id` IN (?)";

            try(PreparedStatement pstmt = con.prepareStatement(q)) {
                pstmt.setLong(1, appId);
                pstmt.setArray(2, con.createArrayOf("INTEGER", list.toArray()));

                getLogger().log(Level.INFO, pstmt.toString());

                if(pstmt.executeUpdate() != list.size()) {
                    con.rollback();
                    throw new ErrorException(ErrorId.DATASET_LOCKED, "object is already locked");
                }
                con.commit();
            }
        } catch(SQLException e) {
            throw new ErrorException(ErrorId.DATABASE_ERROR, e.getMessage());
        }
    }

    @Override
    public void unlock(long appId, Object data)
    throws ErrorException {
       try {
            ArrayList<Object> list = (ArrayList<Object>)data;

            if(!isLockedByApp(appId, data)) {
                return;
            }

            Connection con = database.getConnection();
            con.setAutoCommit(false);

            String q = "UPDATE `BlockSections` SET `locked` = NULL WHERE `locked` IS ? AND `id` IN (?)";

            try(PreparedStatement pstmt = con.prepareStatement(q)) {
                pstmt.setLong(1, appId);
                pstmt.setArray(2, con.createArrayOf("INTEGER", list.toArray()));

                getLogger().log(Level.INFO, pstmt.toString());

                if(pstmt.executeUpdate() != list.size()) {
                    con.rollback();
                    throw new ErrorException(ErrorId.DATASET_MISSING, "no blocks found");
                }
                con.commit();
            }
        } catch(SQLException e) {
            throw new ErrorException(ErrorId.DATABASE_ERROR, e.getMessage());
        }
    }

    @Override
    public boolean isLockedByApp(long appId, Object data)
    throws ErrorException {
        try {
            Connection con = database.getConnection();

            String q = "SELECT `locked` FROM `BlockSections` WHERE `Id` IN (?) GROUP BY `locked`";

            try(PreparedStatement pstmt = con.prepareStatement(q)) {
                Array array = con.createArrayOf("INTEGER", ((ArrayList<Object>)data).toArray());
                pstmt.setArray(1, array);
                getLogger().log(Level.INFO, pstmt.toString());

                ResultSet rs = pstmt.executeQuery();

                long lockedBy = -1;

                while(rs.next()) {
                    long current = rs.getLong("locked");
                    if(current != 0 && current != appId) {
                        throw new ErrorException(ErrorId.DATASET_LOCKED, "object is locked");
                    }

                    if(current == 0) {
                        lockedBy = 0;
                        continue;
                    }
                    lockedBy = appId;
                }

                if(lockedBy == 0) {
                    return false;
                }
                if(lockedBy == appId) {
                    return true;
                }
                throw new ErrorException(ErrorId.DATASET_MISSING, "no record set found");
            }
        } catch(SQLException e) {
            throw new ErrorException(ErrorId.DATABASE_ERROR, e.getMessage());
        }
    }
}
