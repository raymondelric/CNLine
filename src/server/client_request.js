fs = require('fs');

process.on('message',function(m,sock){

    console.log("[Slave working]");
   
     
    var read_buf = fs.readFileSync("./file/client_request");
    
    read_buf = clean1(read_buf);
    read_buf = clean2(read_buf);
    
    var send_buf = fs.readFileSync("./file/"+read_buf);
    sock.write(send_buf);

    sock.on('data',function(input){
        
        console.log(input.toString());
        //read_buf = fs.readFileSync("./file/client_request");
        read_buf = clean1(input);
        read_buf = clean2(read_buf);
        send_buf = fs.readFileSync("./file/"+read_buf);
        //read_buf = fs.readFileSync("./file/" + input.toString());
        sock.write(send_buf);
    });

});

function clean1(input){
       return input.toString().replace("\r\n","");
}

function clean2(input){
       return input.toString().replace("\r","");
}
