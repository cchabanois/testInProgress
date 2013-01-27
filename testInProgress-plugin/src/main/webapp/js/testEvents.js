/*
 * TestEvents
 * 
 */
var TestRun = (function($) {

	TestRun.index = 0;

	// possible icon skins for a node
	TestRun.IconSkin = {
		TESTSUITE : "testSuite",
		TESTSUITE_RUN : "testSuiteRun",
		TESTSUITE_PASSED : "testSuitePassed",
		TESTSUITE_FAILED : "testSuiteFailed",
		TEST : "test",
		TEST_RUN : "testRun",
		TEST_IGNORED : "testIgnored",
		TEST_PASSED : "testPassed",
		TEST_FAILED : "testFailed",
		TEST_ERROR : "testError"
	}

	// possible status for a test/test suite
	TestRun.TestStatus = {
		UNKNOWN : "unknown",
		IGNORED : "ignored",
		RUNNING : "running",
		PASSED : "passed",
		FAILED : "failed",
		ERROR : "error"
	}

	function TestRun(elementId, runId) {
		console.log("Test run : " + runId);
		TestRun.index++;
		this.tree = null;
		this.runId = runId;
		this.treeEvents = [];
		this.elementId = elementId;
		this.testCount = 0;
		this.testStarted = 0;
		this.testIgnored = 0;
		this.testEnded = 0;
		this.errors = 0;
		this.failures = 0;
		this.testMessageId = "testMessage-" + TestRun.index;
		this.progressId = "progress-" + TestRun.index;
		this.treeId = "tree-" + TestRun.index;
		this.runsId = "runs-" + TestRun.index;
		this.errorsId = "errors-" + TestRun.index;
		this.failuresId = "failures-" + TestRun.index;
		this.panelStackTraceId = "panel-stackTrace-" + TestRun.index;
		this.stackTraceId = "stackTrace-" + TestRun.index;
		$('#' + this.elementId).html(
				"<div class='testpanel'>" + "<fieldset><legend><span></span>" + runId
						+ "</legend>" + "<fieldset>" + "<div id='"
						+ this.testMessageId + "'></div>"
						+ "Runs : <span class='stat' id='" + this.runsId
						+ "'></span>" + "Errors: <span class='stat' id='"
						+ this.errorsId + "'></span>"
						+ "Failures : <span class='stat' id='"
						+ this.failuresId + "'></span><div id='"
						+ this.progressId + "'></div></fieldset>"
						+ "<div><div id='" + this.treeId
						+ "' class='ztree'></div>"
						+ "<fieldset class='stacktrace' id='"
						+ this.panelStackTraceId + "'><div id='"
						+ this.stackTraceId + "'></div></div></fieldset>"
						+ "</fieldset></div>");
		this.createScrollLockButton();
	}
	TestRun.prototype = {

		createScrollLockButton : function() {
			this.scrollLockId = "scrollLock-" + TestRun.index;
			$('#' + this.elementId + " > div.testpanel > fieldset > fieldset")
					.after(
							'<input type="checkbox" class="scrollLock" id="'
									+ this.scrollLockId + '" /><label for="'
									+ this.scrollLockId
									+ '">Scroll Lock</label>');
			$("#" + this.scrollLockId).button({
				text : true,
				icons : {
					primary : "lock"
				}
			});
		},
		handleTestEvents : function(events) {
			if (events.length == 0) {
				return;
			}
			if (this.tree == null) {
				this.treeWillBeRefreshed = true;
			} else {
				this.treeWillBeRefreshed = false;
			}
			for ( var i = 0; i < events.length; i++) {
				this.handleTestEvent(events[i]);
			}
			if (this.tree != null && this.treeWillBeRefreshed == true) {
				this.tree.refresh();
			}
			this.updateSuiteIcon();
		},
		handleTestEvent : function(event) {
			switch (event.type) {
			case "TESTC":
				this.handleRunStartEvent(event);
				break;
			case "TSTTREE":
				this.treeEvents.push(event);
				break;
			case "FAILED":
				this.handleTestFailedEvent(event);
				break;
			case "ERROR":
				this.handleTestErrorEvent(event);
				break;
			case "TESTS":
				this.handleTestStartEvent(event);
				break;
			case "TESTE":
				this.handleTestEndEvent(event);
				break;
			case "RUNTIME":
				this.handleRunStopEvent(event);
				break;
			default:
				break;
			}
			this.updateStats();
		},
		updateStats : function() {
			var runs = this.testStarted + "/" + this.testCount;
			if (this.testIgnored > 0) {
				runs += " (" + this.testIgnored + " ignored)";
			}
			$("#" + this.runsId).text(runs);
			$("#" + this.errorsId).text(this.errors);
			$("#" + this.failuresId).text(this.failures);
		},
		setMessage : function(message) {
			$("#" + this.testMessageId).text(message);
		},
		handleRunStartEvent : function(event) {
			this.testCount = event.testCount;
			$("#" + this.progressId).progressbar({
				value : 0,
				max : this.testCount
			});
			$("#" + this.progressId + " > div").css({
				'background' : 'green'
			});
		},
		handleRunStopEvent : function(event) {
			this.setMessage("Finished after "
					+ (event.elapsedTime / 1000).toFixed(2) + " seconds");
		},
		handleTestStartEvent : function(event) {
			this.testStarted++;
			if (event.ignored) {
				this.testIgnored++;
			}
			if (this.tree == null) {
				this.createTreeView();
			}
			var node = this.getNodeByTestId(event.testId);
			node.testStatus = TestRun.TestStatus.RUNNING;
			this.updateNode(node);
			this.updateParentNode(node);
			this.expandParent(node);
			if (!$('#' + this.scrollLockId).is(':checked')) {
				this.tree.selectNode(node);
			}
		},
		updateNode : function(node) {
			if (node.elapsedTime != null) {
				var elapsedTimeInSeconds = numeral(node.elapsedTime / 1000)
						.format('0,0.000');
				node.name = node.testName + "<span class='testElapsedTime'> ("
						+ elapsedTimeInSeconds + " s)</span";
			} else {
				node.name = node.testName;
			}
			if (node.suite) {
				switch (node.testStatus) {
				case TestRun.TestStatus.UNKNOWN:
					node.iconSkin = TestRun.IconSkin.TESTSUITE;
					break;
				case TestRun.TestStatus.RUNNING:
					node.iconSkin = TestRun.IconSkin.TESTSUITE_RUN;
					break;
				case TestRun.TestStatus.PASSED:
					node.iconSkin = TestRun.IconSkin.TESTSUITE_PASSED;
					break;
				case TestRun.TestStatus.FAILED:
					node.iconSkin = TestRun.IconSkin.TESTSUITE_FAILED;
					break;
				}
			} else {
				switch (node.testStatus) {
				case TestRun.TestStatus.UNKNOWN:
					node.iconSkin = TestRun.IconSkin.TEST;
					break;
				case TestRun.TestStatus.IGNORED:
					node.iconSkin = TestRun.IconSkin.TEST_IGNORED;
					break;
				case TestRun.TestStatus.RUNNING:
					node.iconSkin = TestRun.IconSkin.TEST_RUN;
					break;
				case TestRun.TestStatus.PASSED:
					node.iconSkin = TestRun.IconSkin.TEST_PASSED;
					break;
				case TestRun.TestStatus.FAILED:
					node.iconSkin = TestRun.IconSkin.TEST_FAILED;
					break;
				case TestRun.TestStatus.ERROR:
					node.iconSkin = TestRun.IconSkin.TEST_ERROR;
					break;
				}
			}
			if (this.treeWillBeRefreshed == false) {
				this.tree.updateNode(node);
			}
		},
		expandParent : function(node) {
			var parent = node.getParentNode();
			while (parent != null && parent.open == false) {
				if (this.treeWillBeRefreshed == true) {
					parent.open = true;
				} else {
					this.tree.expandNode(parent, true, false, false, false);
				}
				parent = parent.getParentNode();
			}
		},
		collapseParentIfPassed : function(node) {
			var parent = node.getParentNode();
			while (parent != null) {
				if (parent.testStatus == TestRun.TestStatus.PASSED) {
					if (this.treeWillBeRefreshed == true) {
						parent.open = false;
					} else {
						this.tree
								.expandNode(parent, false, false, false, false);
					}
				} else {
					break;
				}
				parent = parent.getParentNode();
			}
		},
		handleTestFailedEvent : function(event) {
			this.failures++;
			var node = this.getNodeByTestId(event.testId);
			node.testStatus = TestRun.TestStatus.FAILED;
			this.updateNode(node);
			node.trace = event.trace;
			$("#" + this.progressId + " > div").css({
				'background' : 'darkred'
			});
		},
		updateSuiteIcon : function() {
			var legendClass = "";
			if (this.errors > 0 | this.failures > 0) {
				if (this.testEnded == this.testCount) {
					legendClass = "runFailureIcon";
				} else {
					legendClass = "runProgressIcon runProgressFailure"
							+ (Math.floor(1 + this.testEnded * 9
									/ this.testCount)) + "Icon";
				}
			} else {
				if (this.testEnded == this.testCount) {
					legendClass = "runSuccessIcon";
				} else {
					legendClass = "runProgressIcon runProgressSuccess"
							+ (Math.floor(1 + this.testEnded * 9
									/ this.testCount)) + "Icon";
				}
			}
			$('#' + this.elementId + "> .testpanel > fieldset legend span").attr(
					"class", legendClass);
		},
		handleTestErrorEvent : function(event) {
			this.errors++;
			var node = this.getNodeByTestId(event.testId);
			node.testStatus = TestRun.TestStatus.ERROR;
			this.updateNode(node);
			node.trace = event.trace;
			$("#" + this.progressId + " > div").css({
				'background' : 'darkred'
			});
		},
		handleTestEndEvent : function(event) {
			this.testEnded++;
			var node = this.getNodeByTestId(event.testId);
			node.elapsedTime = event.elapsedTime;
			if (node.testStatus == TestRun.TestStatus.RUNNING) {
				if (event.ignored) {
					node.testStatus = TestRun.TestStatus.IGNORED;
				} else {
					node.testStatus = TestRun.TestStatus.PASSED;
				}
			}
			this.updateNode(node);
			$("#" + this.progressId).progressbar("value", this.testEnded);
			this.updateParentNode(node);
			this.collapseParentIfPassed(node);
		},
		updateParentNode : function(childNode) {
			var parentNode = childNode.getParentNode();
			if (parentNode == null) {
				return;
			}
			var parentTestStatus = TestRun.TestStatus.UNKNOWN;
			var elapsedTime = 0;
			for ( var i = 0; i < parentNode.children.length; i++) {
				var childNode = parentNode.children[i];
				if (childNode.testStatus == TestRun.TestStatus.RUNNING
						|| childNode.testStatus == TestRun.TestStatus.UNKNOWN) {
					parentTestStatus = TestRun.TestStatus.RUNNING;
					break;
				}
				if (parentTestStatus == TestRun.TestStatus.UNKNOWN) {
					if (childNode.testStatus == TestRun.TestStatus.IGNORED
							|| childNode.testStatus == TestRun.TestStatus.PASSED) {
						parentTestStatus = TestRun.TestStatus.PASSED;
					}
				}
				if (childNode.testStatus == TestRun.TestStatus.FAILED
						|| childNode.testStatus == TestRun.TestStatus.FAILED.ERROR) {
					parentTestStatus = TestRun.TestStatus.FAILED;
				}
				elapsedTime += childNode.elapsedTime;
			}
			parentNode.testStatus = parentTestStatus;
			if ((parentTestStatus == TestRun.TestStatus.PASSED || parentTestStatus == TestRun.TestStatus.FAILED)
					&& (parentNode.elapsedTime == null)) {
				parentNode.elapsedTime = elapsedTime;
			}
			this.updateNode(parentNode);
			this.updateParentNode(parentNode);
		},
		createTreeNodes : function(treeEvents) {
			var eventIndex = 0;
			var testRun = this;
			function createTreeNode() {
				var event = treeEvents[eventIndex];
				if (event.suite == false) {
					var testName = testRun.getShortTestName(event);
					var newNode = {
						name : testName,
						iconSkin : TestRun.IconSkin.TEST,
						// our properties :
						id : event.testId,
						suite : false,
						testName : testName,
						testStatus : TestRun.TestStatus.UNKNOWN
					};
					eventIndex++;
					return newNode;
				} else {
					var newNode = {
						name : event.testName,
						iconSkin : TestRun.IconSkin.TESTSUITE,
						children : [],
						// our properties :
						id : event.testId,
						suite : true,
						testName : event.testName,
						testStatus : TestRun.TestStatus.UNKNOWN
					};
					eventIndex++;
					for ( var i = 0; i < event.testCount; i++) {
						newNode.children.push(createTreeNode());
					}
					return newNode;
				}
			}
			return [ createTreeNode() ];
		},
		createTreeView : function() {
			var treeNodes = this.createTreeNodes(this.treeEvents);
			$.fn.zTree.init($("#" + this.treeId), {
				view : {
					nameIsHTML : true
				},
				callback : {
					onClick : $.proxy(function(event, treeId, treeNode,
							clickFlag) {
						var trace = treeNode.trace;
						if (trace == null) {
							trace = "";
						}
						$('#' + this.stackTraceId).html(trace);
					}, this)
				}
			}, treeNodes);
			this.tree = $.fn.zTree.getZTreeObj(this.treeId);
		},
		getNodeByTestId : function(testId) {
			var node = this.tree.getNodeByParam("id", testId, null);
			return node;
		},
		getShortTestName : function(event) {
			var index = event.testName.indexOf('(');
			if (index == -1) {
				return;
			}
			return event.testName.slice(0, index);
		}
	};

	return TestRun;
}(jQuery));

var TestRuns = (function($) {

	function TestRuns(elementId) {
		this.elementId = elementId;
		this.eventsCount = 0;
		this.testRuns = [];
		this.index = 0;
	}

	TestRuns.prototype = {
		start : function(event) {
			var $this = this;
			(function() {
				$this.handleNextTestEvents();
				// call the function again in 2 seconds
				setTimeout(arguments.callee, 2000);
			})();
		},
		handleNextTestEvents : function() {
			var $this = this;
			remoteAction.getTestEvents(this.eventsCount, function(t) {
				var testEvents = t.responseObject();
				$this.handleTestEvents(testEvents);
				$this.eventsCount += testEvents.length;
			});
		},
		handleTestEvents : function(buildEvents) {
			var runIdToRunEvents = {};
			for ( var i = 0; i < buildEvents.length; i++) {
				var buildEvent = buildEvents[i];
				if (runIdToRunEvents[buildEvent.runId] == null) {
					runIdToRunEvents[buildEvent.runId] = [];
				}
				runIdToRunEvents[buildEvent.runId]
						.push(buildEvent.runTestEvent);
			}
			for ( var runId in runIdToRunEvents) {
				var testRun = this.testRuns[runId];

				if (testRun == null) {
					this.index++;
					$('#' + this.elementId).append(
							"<div id='runId-" + this.index + "'></div>");
					testRun = new TestRun("runId-" + this.index, runId);
					this.testRuns[runId] = testRun;
				}
				testRun.handleTestEvents(runIdToRunEvents[runId]);
			}
		}
	};

	return TestRuns;
}(jQuery));