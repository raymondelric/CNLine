fs = require('fs');

process.on('message',function(m,sock){
    
    var read_buf = fs.readFileSync("./file/client_request");
    sock.write(read_buf);

    sock.on('data',function(input){
        console.log(input);
        read_buf = fs.readFileSync("./file/" + input.toString());
        sock.write(read_buf);
    });

});
