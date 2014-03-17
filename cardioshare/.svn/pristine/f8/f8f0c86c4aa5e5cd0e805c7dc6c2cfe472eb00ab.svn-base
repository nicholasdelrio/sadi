Ext.onReady(function() {
	Ext.state.Manager.setProvider(new Ext.state.CookieProvider({
		expires: new Date( new Date().getTime()+(1000*60*60) ) // one hour...
	}));
	Ext.QuickTips.init();

	var servlet = "./query";
	var formId = "query-form";
	var statusId = "query-status-bar";
	var taskId;
	var errorData;
	var gridPanel;
	var submit;
	var dialog;

	var renderLinks = function(value, metadata) {
		if (value.match(/^http:/)) {
			return String.format("<a href='{0}' target='_blank'>{1}</a>", value, value);
		} else {
			return value;
		}
	};
	
	var renderResults = function(taskId, result) {
		var resultModelLink = "<a href='/tmp/" + taskId + "' target='_blank'>View results as RDF</a>. ";
		var errorMessage = "";
		var style = "dummy";
		if (result.error.length || result.warnings.length) {
			errorData = result;
			if (result.error.length) {
				errorMessage = "There were errors executing the query. Click for details.";
				style = "x-status-error";
			} else {
				errorMessage = "There were warnings executing the query. Click for details.";
				style = "warning";
			}
		}
		if (result.rows.length === 0) {
			statusBar.setStatus({
				text: "Query returned no results. " + resultModelLink + errorMessage,
				iconCls: style
			});
			return;
		} else {
			statusBar.setStatus({
				text: resultModelLink + errorMessage,
				iconCls: style
			});
		}

		var store = new Ext.data.SimpleStore({
			fields: result.fields
		});
		store.loadData(result.rows);

		var columns = [];
		for (var i=0; i<result.fields.length; ++i) {
			columns.push({
				header: result.fields[i],
				dataIndex: result.fields[i],
				sortable: true,
				renderer: renderLinks
			});
		}
		gridPanel = new Ext.grid.GridPanel({
			store: store,
			columns: columns,
			viewConfig: { forceFit: true },
			stripeRows: true,
			width: 'auto',
			autoHeight: true,
			maxHeight: 600,
			title: "Query results"
		});

		gridPanel.render("result-table");
		var pos = formPanel.getPosition();
		window.scroll(pos[0], pos[1]);
	};

	var pollServlet = function(statusBar) {

		if (!(statusBar instanceof Object)) {
			statusBar = Ext.getCmp(statusBar);
			statusBar.showBusy();
		}

		Ext.Ajax.request({
			url: servlet,
			params: { poll: taskId },
			success: function(result, response) {
				var result = Ext.util.JSON.decode(result.responseText);
				if (result instanceof Object) {
					Ext.state.Manager.set("taskId", taskId);
					Ext.state.Manager.set("result", result);
					statusBar.clearStatus();
					submit.enable();
					renderResults(taskId, result);
				} else {
					if (result) {
						statusBar.setText( "<xmp style='margin: inherit; font-family: inherit;'>" + result + "</xmp>" );
					}
					(function(){ pollServlet(statusBar); }).defer(5000);
				}
			},
			failure: function(response, options) {
				statusBar.setStatus({
					text: "Error " + response.status + " (" + response.statusText + ") during request",
					iconCls: "x-status-error"
				});
				submit.enable();
			}
		});

	};

	var tabPanel = new Ext.TabPanel({
		renderTo: 'tabs',
		width: 'auto',
		activeTab: 0,
		frame: false,
		defaults: { autoHeight: true },
		items: [
		        { contentEl: 'query', title: 'Query' },
		        { contentEl: 'browse', title: 'Browse' }
		        ]
	});

	var errorTemplate = new Ext.XTemplate(
			"<div class='error-table-div'>",
			"<ul class='error-table'>",
			'<tpl for="error"><li class="error">{.}</li></tpl>',
			'<tpl for="warnings"><li class="warning">{.}</li></tpl>',
			"</ul>",
			"</div>"
	);
	var statusBar = new Ext.StatusBar({
		defaultText: "Ready.",
		defaultIconCls: "",
		id: "query-status-bar"
	});

	var formPanel = new Ext.FormPanel({
		frame: true,
		labelAlign: "top",
		items: [
			new Ext.form.TextArea({
				id: "queryBox",
				fieldLabel: "SPARQL query",
				name: "query",
				width: "99%",
				height: "8em"
			})
		],
		bbar: statusBar
	});

	submit = formPanel.addButton("Submit", function() {
		errorData = null;
		var query = document.getElementById('queryBox').value;
		Ext.state.Manager.set("query", query);
		formPanel.getForm().submit({
			url: servlet,
			success: function(form, action) {
				taskId = action.result.taskId;
				pollServlet("query-status-bar");
				submit.disable();
				if (gridPanel) {
					gridPanel.destroy();
				}
			},
			error: function(form, action) {
				alert(error);
			}
		});
	});

	formPanel.render(formId);
	var query = Ext.state.Manager.get("query");
	if (query) {
		document.getElementById('queryBox').value = query;
	}
	var taskId = Ext.state.Manager.get("taskId");
	var result = Ext.state.Manager.get("result");
	if (taskId && result) {
		renderResults(taskId, result);
	}
	statusBar.getEl().on("click", function() {
		if (errorData) {
			if (!dialog) {
				Ext.MessageBox.alert("Status", errorTemplate.apply(errorData));
			}
		}
	});
});
