

process.on('message',function(m,sock){
    
    var read_buf = readFileSync("./file/client_request");
    sock.write(read_buf);

});
