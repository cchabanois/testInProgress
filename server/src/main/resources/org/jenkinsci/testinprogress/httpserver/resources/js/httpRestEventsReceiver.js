/*
 * Test Events Receiver
 * 
 */
var HttpRestEventsReceiver = (function($) {

	function HttpRestEventsReceiver(testRuns) {
		this.testRuns = testRuns;
		this.index = 0;
		this.time1 = 0;
		this.time2 = 0;
		this.time3 = 0;
	}

	HttpRestEventsReceiver.prototype = {
		start : function() {
			this.handlePreviousBuildTestEvents($.proxy(this.handleCurrentBuildTestEvents,this));
		},
		handlePreviousBuildTestEvents: function(previousBuildTestEventsHandled) {
			var handler = $.proxy(function(t) {
				this.time2 = (new Date()).getTime();
				console.log("Time to get events : " + (this.time2 - this.time1));
				var buildTestEventsList = t;
				this.testRuns.handlePreviousBuildTestEvents(buildTestEventsList);
				this.time3 = (new Date()).getTime();
				console.log("Time to handle events : " + (this.time3 - this.time2));
				previousBuildTestEventsHandled();
			}, this);
			var callFunction = $.proxy(function() {
				this.time1 = (new Date()).getTime();
				$.ajax({
					type : 'GET',
					url : "/previousBuildTestEvents",
					dataType : "json", // data type of response
					success : handler
				});

			}, this);
			callFunction();
		},
		handleCurrentBuildTestEvents: function() {
			var handler = $.proxy(function(t) {
				this.time2 = (new Date()).getTime();
				console.log("Time to get events : " + (this.time2 - this.time1));
				var buildTestEventsList = t;
				this.testRuns.handleTestEvents(buildTestEventsList);
				this.index += buildTestEventsList.length;
				this.time3 = (new Date()).getTime();
				console.log("Time to handle events : " + (this.time3 - this.time2));
				// call the function again in 2 seconds
				setTimeout(callFunction, 2000);
			}, this);
			var callFunction = $.proxy(function() {
				this.time1 = (new Date()).getTime();
				$.ajax({
					type : 'GET',
					url : "/buildTestEvents?fromIndex=" + this.index,
					dataType : "json", // data type of response
					success : handler
				});

			}, this);
			callFunction();
		}
	};

	return HttpRestEventsReceiver;
}(jQuery));