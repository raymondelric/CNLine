var fs = require('fs');
var net = require('net');

var HOST = '127.0.0.1';
var PORT = 9001;

net.createServer( function(sock) {
    
    console.log("[File Server Start]");

    sock.on('data',function (data){
        
        msg = data.toString();
        var input = msg.split(/[\/,\n]+/);
        //var data = fs.readFileSync("./file/"+input[0]);
        console.log("[Data] " + data);
        fs.writeFileSync("./file/client_request",data);

        console.log("[Pass the req to Slave]");
        var child = require('child_process').fork(__dirname + '/client_request'); 
        child.send('socket',sock);

    });

}).listen(PORT,HOST);
