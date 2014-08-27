/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * Container of all the UI of the olap engine.<br>
 * It contains:
 * <ul>
 *		<li>View definition tools</li>
 *		<li>Table/Chart</li>
 *		<li>Options</li>
 *	</ul>
 * 
 *     
 *  @author
 *  Monica Franceschini
 */
 
  
Ext.define('Sbi.datamining.DataMiningPanel', {
	extend: 'Ext.panel.Panel',
	layout: {
        type: 'vbox',
        align: 'left'
    },
	
	config:{

	},
	
	resultPanel: null,
	uploadPanel: null,
	executeScriptBtn: null,
	isResultReadyPar: true,
	
	constructor : function(config) {
		this.initConfig(config||{});
		
		this.resultPanel = Ext.create('Sbi.datamining.ResultPanel',{itsParent: this}); 
		this.uploadPanel = Ext.create('Sbi.datamining.UploadPanel',{itsParent: this});
		this.executeScriptBtn = Ext.create('Ext.Button', {
		    text: 'Execute script',
		    scope: this,
		    //hidden: true,
		    handler: function() {
		        alert('You clicked the button!');
		        this.resultPanel.getResult();
		    }
		});
		
		this.callParent(arguments);
	},

	initComponent: function() {
		Ext.apply(this, {
			items: [this.uploadPanel, this.resultPanel, this.executeScriptBtn]
		});
	
		this.isResultReady();
		this.callParent();
	}

	, isResultReady: function(){
		
		var thisPanel = this;
		
		var service = Ext.create("Sbi.service.RestService",{
			url: "result"
			,subPath: "needsResultAtForstExec"
		});
		
		var functionSuccess = function(response){
			
			if(response != null && response.responseText !== undefined && response.responseText !== null && response.responseText !== ''){
			
				var res = Ext.decode(response.responseText);
				if(res.result == 'ok'){
					this.resultPanel.getResult();
				}
				
			}
		};
		
		
		
		service.callService(this, functionSuccess);
	}
	
	
});