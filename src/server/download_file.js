var fs = require('fs');
var net = require('net');

var HOST = '127.0.0.1';
var PORT = 9001;

process.on('message',function(name,m,sock){
    
    
    console.log("[Hello] " + name + m + sock);

    //var name = input1[0];
    //var data = input1[1];
    var data = input1;

    //console.log("[Name] " + name);
    //console.log("[Sock] " + data);

    //var data = fs.readFileSync("./file/" + name).toString();

    //sock.write( DOWNLOAD + "/" + name + "/" + data + "\n");

    //process.send("hello" + input);

});

net.createServer( function(sock) ){
    
    console.log("[File Server Start]");

    sock.on('data',function (data){
        
        var input = data.split(/[\/,\n]+/)[0];
        var data = fs.readFileSync("./file/"+input[0]);
        console.log("[Data] " + data);
        writeFileSync("./file/client_request",data);

        console.log([Pass the req to Slave]);
        var child = require().fork(__dirname + '/client_request'); 
        child.send('socket',sock);

    });

}
