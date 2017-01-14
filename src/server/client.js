var io = require('socket.io').listen(3000);
var fs = require('fs');


io.on('connection', function(socket){
    
    socket.on('REG', function(id, pwd){

        console.log("start REG " + id + ":" + pwd);
        // do the register stuff

    });


    socket.on('LOGIN', function(id, pwd){

        console.log("in login " + id + ":" + pwd);
        //check the pwd
        //sent the history

    });

    socket.on('MSG', function(){

    });

});
