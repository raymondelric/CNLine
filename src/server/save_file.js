var fs = require('fs');

process.on('message',function(input){

    console.log("[input] " + input);

    console.log("[Filename] " + input[0]);//name
    console.log("[Data] " + input[1]);//date

    fs.writeFileSync("./file/" + input[0],input[1]);

    //process.send("hello" + input);

});
