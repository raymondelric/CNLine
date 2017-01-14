var fs = require('fs');
var net = require('net');
var sleep = require('sleep');

var HOST = '140.112.30.44';
var PORT = 9001;
    
console.log("[File Server Start]");

net.createServer( function(sock) {
    
    console.log("[Connected File Server]");

    sock.on('data',function (data){
        
        msg = data.toString();
        //var input = msg.split(/[\/,\n]+/);
        //var data = fs.readFileSync("./file/"+input[0]);
        console.log("[FS Data] " + data);
        fs.writeFileSync("./file/client_request",data);

        console.log("[Pass the req to Slave]");
        var child = require('child_process').fork(__dirname + '/client_request'); 
        child.send('socket',sock);

        sleep.sleep(1000);

    });

    sock.on('close',function(data){
            console.log("[Remove]");
    });

}).listen(PORT,HOST);


process.on('uncaughtException',function(err){
    console.log(err.throw);
    console.log("[Abort]");
});
