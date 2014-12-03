function Game(port) {
	this.ws = new WebSocket('ws://localhost:'+port);
	var that = this;
	this.ws.onopen = function() {
		console.log('open');
		that.ws.send('Hello');  // Sends a message.
	}
	this.ws.onmessage = function(e) {
		// Receives a message.
		console.log('message', e.data);
	}
	this.ws.onclose = function() {
		console.log('close');
	}
}
