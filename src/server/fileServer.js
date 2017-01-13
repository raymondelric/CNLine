var net = require('net');
var fs = require('fs');
var sleep = require('sleep');
var child = require('child_process').fork(__dirname + '/worker');

var HOST = '127.0.0.1';
var PORT = 9001;
var file_list = [];

child.on('message',function(input){
    console.log("recieve " + input );
});


for(var i = 0;i < 10;i++){
    file_list[i] = require('child_process').fork(__dirname + '/worker');
    child.send( 'aaa'+i );
    sleep.usleep(10000);
}


file_list[0].on('message',function(input){
    console.log("recieve " + input );
});
