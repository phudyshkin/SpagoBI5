/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.importexport.transformers;

import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.tools.importexport.ITransformer;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class TransformerFrom2_5_0To2_6_0 implements ITransformer {

	static private Logger logger = Logger.getLogger(TransformerFrom2_5_0To2_6_0.class);

	public byte[] transform(byte[] content, String pathImpTmpFolder, String archiveName) {
		logger.debug("IN");
		try {
			TransformersUtilities.decompressArchive(pathImpTmpFolder, archiveName, content);
		} catch(Exception e) {
			logger.error("Error while unzipping 2.3.0 exported archive", e);	
		}
		archiveName = archiveName.substring(0, archiveName.lastIndexOf('.'));
		changeDatabase(pathImpTmpFolder, archiveName);
		// compress archive
		try {
			content = TransformersUtilities.createExportArchive(pathImpTmpFolder, archiveName);
		} catch (Exception e) {
			logger.error("Error while creating creating the export archive", e);	
		}
		// delete tmp dir content
		File tmpDir = new File(pathImpTmpFolder);
		GeneralUtilities.deleteContentDir(tmpDir);
		logger.debug("OUT");
		return content;
	}

	private void changeDatabase(String pathImpTmpFolder, String archiveName) {
		logger.debug("IN");
		Connection conn = null;
		try {
			conn = TransformersUtilities.getConnectionToDatabase(pathImpTmpFolder, archiveName);
			fixResources(conn);
			fixMetadata(conn);
			fixMetacontent(conn);
			conn.commit();
		} catch (Exception e) {
			logger.error("Error while changing database", e);	
		} finally {
			logger.debug("OUT");
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error("Error closing connection to export database", e);
			}
		}
	}


	/*
	 * Adjust Obj Notes Table
	 * 
	 * @param conn The jdbc connection to export database
	 * @throws Exception
	 */
	private void fixResources(Connection conn) throws Exception {
		logger.debug("IN");

		Statement stmt = conn.createStatement();
		String sql =  "";
		// ALTER TABLE SBI_OBJECT_NOTES ADD COLUMN OWNER VARCHAR(50);
		try{
			sql =  "ALTER TABLE SBI_RESOURCES ADD COLUMN RESOURCE_CODE VARCHAR(45)";
			stmt.execute(sql);
			sql =  "UPDATE SBI_RESOURCES SET RESOURCE_CODE=RESOURCE_NAME WHERE RESOURCE_CODE IS NULL";
			stmt.executeUpdate(sql);
		}
		catch (Exception e) {
			logger.error("Error adding column: if add column fails may mean that column already esists; means you ar enot using an exact version spagobi DB",e);	
		}

		logger.debug("OUT");
	}


	/*
	 * Adjust Obj Notes Table
	 * 
	 * @param conn The jdbc connection to export database
	 * @throws Exception
	 */
	private void fixMetadata(Connection conn) throws Exception {
		logger.debug("IN");

		Statement stmt = conn.createStatement();
		String sql =  "";
		// ALTER TABLE SBI_OBJECT_NOTES ADD COLUMN OWNER VARCHAR(50);
		try{
			sql =  "CREATE TABLE SBI_OBJ_METADATA" +
			" (OBJ_META_ID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL," +
			" LABEL VARCHAR(20) NOT NULL," +
			" NAME VARCHAR(40) NOT NULL," +
			" DESCRIPTION VARCHAR(100) default NULL," +
			" DATA_TYPE_ID INTEGER NOT NULL," +
			" CREATION_DATE TIMESTAMP NOT NULL" +
			" ) ";
			stmt.executeUpdate(sql);

		}
		catch (Exception e) {
			logger.error("Error adding column: if add column fails may mean that column already esists; means you ar enot using an exact version spagobi DB",e);	
		}

		logger.debug("OUT");
	}

	
	/*
	 * Adjust Obj Metacontent
	 * 
	 * @param conn The jdbc connection to export database
	 * @throws Exception
	 */
	private void fixMetacontent(Connection conn) throws Exception {
		logger.debug("IN");

		Statement stmt = conn.createStatement();
		String sql =  "";
		try{
			sql =  "CREATE TABLE SBI_OBJ_METACONTENTS" +
			" (OBJ_METACONTENT_ID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL," +
			" OBJMETA_ID INTEGER NOT NULL," +
			" BIOBJ_ID INTEGER NOT NULL," +
			" SUBOBJ_ID INTEGER default NULL," +
			" BIN_ID INTEGER default NULL," +
			" CREATION_DATE TIMESTAMP NOT NULL," +
			" LAST_CHANGE_DATE TIMESTAMP NOT NULL" +
			" ) ";
			stmt.executeUpdate(sql);

		}
		catch (Exception e) {
			logger.error("Error adding column: if add column fails may mean that column already esists; means you ar enot using an exact version spagobi DB",e);	
		}

		logger.debug("OUT");
	}


}
