var net = require('net');
var fs = require('fs');
var sleep = require('sleep');

'use strict';
var crypto = require('crypto');

var HOST = '140.112.30.44' ; // parameterize the IP of the Listen 
var PORT = 9000 ; // TCP LISTEN port 

const CONNECT = '00'
const REGISTER = '01'
const LOGIN = '02'
const LOGOUT = '03'
const MESSAGE = '04'
const CREATEROOM = '05'
const NEW_MSG = '06'
const RECORD = '07'
const CHECKID = '08'
const FILESEND = '09'

const CONNECT_OK = '10'
const CONNECT_FAIL = '11'

const REGISTER_OK = '20'
const REGISTER_DUP = '21'
const REGISTER_ILE = '22'
const REGISTER_FAIL = '23'

const LOGIN_OK = '30'
const LOGIN_IDNF = '31'
const LOGIN_WRPS = '32'
const LOGIN_ALRD = '33'
const LOGIN_FAIL = '34'

const LOGOUT_OK = '40'
const LOGOUT_FAIL = '41'

const ROOM_OK = '50'
const ROOM_FAIL = '51'

const CHECK_OK = '80'
const CHECK_FALSE = '81'

const FILE_NOTIFY = '97'
const LOGIN_NOTIFY = '98'
const LOGOUT_NOTIFY = '99'

var id;
var pass;

var client_socket = [];
var client_account = [];
var acc = [];
var pwd = [];
var filecount = 0;
var num_account = 0;
var num_room = 0;

var chat_msg_num = [];

require('dns').lookup(require('os').hostname(),function(err,add,fam){
    console.log("[HOST] "+add);
    if (add != HOST)
        console.log("[ERR] Please modify the add in server.js and download.js");
})

console.log("[Loading Acc and Pwd]");
load_acc();
console.log("[Done loading "+num_account+" accounts]");

console.log("[Checking Rooms]");
room_setup();
console.log("[Done loading "+num_room+" rooms]");

net.createServer ( function ( sock ) { 
	console.log ( 'CONNECTED: '+sock.remoteAddress +':'+ sock.remotePort ) ;

    client_socket.push(sock);
   
	sock.on ( 'data', function ( data ) {
        console.log("[Main Server] " + data.toString());
		var packet = data.toString();
		var type = packet.substring(0,2);
		var msg = clean(packet.substring(3));
		var ret = '';

        console.log(data.toString());
		
		if(type === CONNECT){
			ret = CONNECT_OK;
			console.log( msg.split(/[\/,\n]+/)[0] );
		    sock.write ( ret ) ; 
		}
		else if(type === REGISTER){
			var strs = msg.split(/[\/,\n]+/);
			var _id = strs[0];
			var _pass = strs[1];
			ret = REGISTER_OK;
			console.log( '[Register] ' + _id + ', ' + _pass );
            // handle register
            ret = register(sock,_id,_pass);
		    sock.write ( ret ) ; 
		}
		else if(type === LOGIN){

			var strs = msg.split(/[\/,\n]+/);
			id = strs[0];
			pass = strs[1];
			ret = LOGIN_OK;
			console.log( '[Login] ' + id + ', ' + pass );
            ret = login(sock,id,pass);
            
            if (ret == LOGIN_OK){
                chat_msg_num = [];
                login_notify(sock,id);
                send_history_info(sock,id);
                for (var i = 0;i < chat_msg_num.length; i++){
                    ret += "/";
                    ret += chat_msg_num[i].toString();
                }
		    
                console.log("[Login return] " + ret);
                sock.write ( ret ) ; 
                console.log("[Send history] " + id);
                send_history(sock,id);
            }else{
                sock.write ( ret ) ; 
            }
			
		}
        else if(type === MESSAGE){
	        var strs = msg.split(/[\/,\n]+/);
            console.log("[Clinet Sending Message] Client : " +
                          client_account[client_socket.indexOf(sock)]); 
            ret = message(client_account[client_socket.indexOf(sock)],strs);
            console.log("[Message result] " + ret);
        }
        else if(type === CREATEROOM){
	        var strs = msg.split(/[\/,\n]+/);
            console.log("[Creating Room]"); 
            ret = create_room(strs);
            console.log("[Room result] " + ret); 
        }
        else if(type === FILESEND ){
	        var strs = msg.split(/[\/,\n]+/);
            console.log("[File recieved] " + strs[2]);
            file_transfer(client_account[client_socket.indexOf(sock)],strs);
        }
        else if (type === CHECKID){
	        var strs = msg.split(/[\/,\n]+/);
            console.log("[Check ID] " + strs[0]);
            ret = checkid(strs[0]);
		    sock.write ( ret ) ; 
        }
		else if(type === LOGOUT){
	        var strs = msg.split(/[\/,\n]+/);
			ret = LOGOUT_OK;
			console.log( '[Logout] user: ' + strs[0] );
		    sock.write ( ret ) ;
            logout_notify(strs[0]);
		}

	} ) ; 
	
	sock.on ( 'close', function ( data ) { 
		console.log ( 'CLOSED: '+sock.remoteAddress +' '+ sock.remotePort ) ;
        var index = client_socket.indexOf(sock);
        client_socket.splice(index,1);
        console.log("remove "+index);
   	} ) ; 

} ) .listen ( PORT, HOST ) ;

console.log ( 'Server listening on ' + HOST +':'+ PORT ) ;

function load_acc(){
    
    var  count = 0;
    fs.readFileSync("./pwd").toString().split('\n').forEach(
    function(line){

        if(line != ''){
            var acc_pwd = line.split(/[\/,\n]+/);
            console.log("acc: "+acc_pwd[0]+"  pwd: "+acc_pwd[1]);
            acc.push(acc_pwd[0]);
            pwd.push(acc_pwd[1]);
            count++;
        }

    });

    num_account = count;
}

function room_setup(){
    var roomdir = "./room/";
    var num = fs.readdirSync(roomdir);
    //num.splice(".gitkeep");
    console.log("[ROOM] " + num);
    
    // consider gitkeep
    num_room = (num.length-1) / 2;  
    console.log(num.length);
}

function register(sock,_id,_pwd){

    for(var i = 0;i < acc.length; i++){
        if (acc[i] === _id)
            return REGISTER_DUP;
    }

    var en_pwd = hash_and_salt(_id,_pwd);
    var content = fs.appendFileSync("./pwd",_id+"/"+en_pwd+"\n");
    acc.push(_id);
    pwd.push(en_pwd);
    content = fs.writeFileSync("./user/"+_id,_id+"/"+en_pwd+"\n");

    console.log("[Register done]");
    
    return REGISTER_OK;
}

function login(sock,_id,_pwd){

    console.log(_id+"  "+_pwd);
    var en_pwd = hash_and_salt(_id,_pwd);
    for(var i = 0;i < acc.length; i++){
        console.log(acc[i]+"  "+pwd[i]);
        if (acc[i] === _id && pwd[i] === en_pwd){
             
            var index = client_socket.indexOf(sock);
            client_account[index] = _id;
            console.log("[Login Success] Account id : " + client_account[index]);
            
            return LOGIN_OK;
        }
    }
    console.log("[Login Fail]");

    return LOGIN_FAIL;
}

function message(name,input){
    
    var room_id = input[0];
    var body = input[1];
    console.log("[Room ID] " + room_id);
    console.log("[Body] " + body);

    fs.readFileSync("./room/"+room_id+".user").toString().split('\n').forEach(
        function(line){
            if(line != ''){

                var room_user = line.split(/[\/,\n]+/);
                var index = room_user.indexOf('');
                if (index != -1)    room_user.splice(index,1);
                console.log("[All room user]" + room_user);

                broadcast(client_socket,room_user,body,room_id,name,body);

                // need to write in log
                fs.appendFileSync("./room/" + room_id + ".history",name
                                  + "/" + body.toString() + "\n");
        }
    });
}

function broadcast( all_socket, room_user, body, room_id, owner){
    
    for (var i = 0; i < room_user.length ; i++){
        var index = client_account.indexOf(room_user[i]);
        console.log("[Index, Username, Socket] "+ index + 
                    ", " + client_account[index] + ", " + client_socket[index]);
        console.log( "[Server Broadcast] " + NEW_MSG + "/" + room_id + "/"  + owner + "/" + body );
        if ( index != -1 ){
            console.log("[Find a user online]");
            client_socket[index].write( NEW_MSG + "/" + room_id + "/"  + owner.toString() + "/" + body );
        }
    } 
}


function file_transfer(name,input){
    
    var msg_buffer = [];
    console.log(input);
    msg_buffer[0] = input[0]; // room_id
    msg_buffer[1] = input[1]; // filename
    file_message(name,msg_buffer);

    console.log("[File ID] " + filecount);
    var child = require('child_process').fork(__dirname + '/save_file');
    var send_buffer = [];
    send_buffer.push(input[1]); //file name
    send_buffer.push(input[2]); //file data

    child.send(send_buffer); //file name & data
    console.log("[input] " + send_buffer);

    filecount++;
}

function file_message(name,input){
    
    var room_id = input[0];
    var body = input[1];
    console.log("[Room ID] " + room_id);
    console.log("[Body] " + body);

    fs.readFileSync("./room/"+room_id+".user").toString().split('\n').forEach(
        function(line){
            if(line != ''){

                var room_user = line.split(/[\/,\n]+/);
                var index = room_user.indexOf('');
                if (index != -1)    room_user.splice(index,1);
                console.log("[All room user]" + room_user);

                file_broadcast(client_socket,room_user,body,room_id,name);

        }
    });
}

function file_broadcast( all_socket, room_user, body, room_id, owner){
    
    for (var i = 0; i < room_user.length ; i++){
        var index = client_account.indexOf(room_user[i]);
        console.log("[Index, Username, Socket] "+ index + 
                    ", " + client_account[index] + ", " + client_socket[index]);
        console.log( "[Server Broadcast] " + FILE_NOTIFY + "/" + room_id + "/"  + owner + "/" + body );
        if ( index != -1 ){
            console.log("[Find a user online]");
            client_socket[index].write( FILE_NOTIFY + "/" + room_id + "/"  + owner.toString() + "/" + body );
        }
    } 
}

function create_room( current_user ){

    var fail = 0;
    // remove the '' entry
    var index = current_user.indexOf('');
    current_user.splice(index,1);

    for(var i = 1;i <= num_room;i++){

        console.log("reading "+i);

        fs.readFileSync("./room/"+i+".user").toString().split('\n').forEach(
        function(line){
            if(line != ''){

                var cmp_user = line.split(/[\/,\n]+/);
                index = cmp_user.indexOf('');
                if (index != -1)    cmp_user.splice(index,1);
                console.log(cmp_user);
                console.log(current_user);
 
                if (current_user.length == cmp_user.length){

                    for(var i = 0; i < current_user.length; i++){

                        console.log("i"+current_user[i]+cmp_user[i]);

                        if (current_user[i] != cmp_user[i]){
                            console.log("i"+current_user[i]+cmp_user[i]);
                            break;
                        }
                        if (i == current_user.length - 1){
                            console.log("same"+current_user[i]+cmp_user[i]);
                            fail = 1;
                        }
                    }

                }

            }

        });
    }
                
    if (fail === 1)         return ROOM_FAIL;
    else{
        var buffer = "";
        for(var i = 0;i < current_user.length;i++){
            buffer += (current_user[i].toString() + "/");
        }
        num_room += 1;
        fs.writeFileSync("./room/" + num_room + ".user",buffer);
        fs.writeFileSync("./room/" + num_room + ".history","System/Welcome" + '\n');
        
        room_create_message(num_room,buffer);

        var result = ROOM_OK + "/" + num_room.toString();
       
        return result;
    }
}

function room_create_message(room_id,body){
    
    //var room_id = input[0];
    //var body = input[1];
    console.log("[Room ID] " + room_id);
    console.log("[Body] " + body);

    fs.readFileSync("./room/"+room_id+".user").toString().split('\n').forEach(
        function(line){
            if(line != ''){

                var room_user = line.split(/[\/,\n]+/);
                var index = room_user.indexOf('');
                if (index != -1)    room_user.splice(index,1);
                console.log("[All room user]" + room_user);

                room_create_broadcast(client_socket,room_user,body,room_id);
        }
    });
}

function room_create_broadcast( all_socket, room_user, body, room_id){
    
    for (var i = 0; i < room_user.length ; i++){
        var index = client_account.indexOf(room_user[i]);
        console.log("[Index, Username, Socket] "+ index + 
                    ", " + client_account[index] + ", " + client_socket[index]);
        console.log( "[Server New Room Broadcast] " + ROOM_OK + "/" + room_id + "/"   + body.toString() );
        if ( index != -1 ){
            console.log("[Find a user online]");
            client_socket[index].write( ROOM_OK + "/" + room_id + "/" + body.toString() );
        }
    } 
}


function send_history_info(sock, name){

    var chat_room = [];
    
    for(var i = 1; i <= num_room; i++){
        fs.readFileSync("./room/"+i+".user").toString().split('\n').forEach(
        function(line){
            if (line != ' '){ 
                var cmp_user = line.split(/[\/,\n]+/);
                for(var j = 0; j < cmp_user.length;j++){
                    if( cmp_user[j] == name )   
                        chat_room.push(i);
                }
            }
        });
    }

    for(var i = 0; i < chat_room.length; i++){
        chat_msg_num[i] = 0;
        fs.readFileSync("./room/"+chat_room[i]+".history").toString().split('\n').forEach(function(line){
            if(line != ''){
                chat_msg_num[i] += 1;
            }
        });
    }

}

function send_history(sock, name){

    var chat_room = [];
    var hist_buf = RECORD + "";

    if(chat_msg_num.length > 0){
        hist_buf += "/";
    }
    
    for(var i = 1; i <= num_room; i++){
        fs.readFileSync("./room/"+i+".user").toString().split('\n').forEach(
        function(line){
            if (line != ' '){ 
                var cmp_user = line.split(/[\/,\n]+/);
                for(var j = 0; j < cmp_user.length;j++){
                    if( cmp_user[j] == name )   
                        chat_room.push(i);
                }
            }
        });
    }

    console.log("[Room to send] " + chat_room);

    for(var i = 0; i < chat_room.length; i++){

        console.log("[Debug] " + hist_buf);

        // append room id
        hist_buf += chat_room[i];
        hist_buf += '/';
        
        console.log("[Append Room] " + hist_buf);

        // append user 
        fs.readFileSync("./room/"+chat_room[i]+".user").toString().split('\n').forEach(function(line){
            if(line != ''){
                var room_user = line.split(/[\/,\n]+/);
                console.log("[Debug arr] "+ room_user);
                for(var i = 0;i < room_user.length;i++){
                    if ( i < room_user.length - 1 && room_user[i+1] != '' ){
                        hist_buf += room_user[i];
                        hist_buf += '|';
                    }else if( room_user[i] != '' )
                        hist_buf += room_user[i];

                }
            }
        });
        hist_buf += '/';
        
        console.log("[Append Users] " + hist_buf);

        var reverse_send = [];

        fs.readFileSync("./room/"+chat_room[i]+".history").toString().split('\n').forEach(function(line){
            if(line != ''){
                reverse_send.push(line);
                //sock.write( chat_room[i].toString + "/" + line );
            }
        });

        for(var j = reverse_send.length - 1; j >= 0;j--){
            //sock.write( RECORD+"/"+reverse_send[j] );
            console.log("[Before Rep] "+reverse_send[j] );
            reverse_send[j] = reverse_send[j].replace("/","$");
            console.log("[After Rep] "+reverse_send[j] );

            console.log("[Debug] j & l  " + j + "  " + (reverse_send.length-1));
            if (j >= 1 && reverse_send[j-1] != '' ){
                hist_buf += reverse_send[j];
                hist_buf += '|';
            }else if(reverse_send[i] != ''){
                hist_buf += reverse_send[j];
            }
            console.log("[Send] " + reverse_send[j].toString()+'\n');
            //sleep.usleep(10000);
        }
        reverse_send = [];
        if (i < num_room - 1){
            hist_buf += '/';
        }
    }

    hist_buf[hist_buf.length - 1] = '\0';
    console.log("[Send]" + hist_buf );

    sock.write(hist_buf);

}

function checkid(name){

    console.log("[CheckID] " + name);

    for(var i = 0;i < acc.length;i++){

        if (name.toString() === acc[i].toString()){
            console.log("[Exist] "  + acc[i].toString());
            return CHECK_OK;
        }
    }

    return CHECK_FALSE;

}

function clean(input){
    return input.replace("\r","");
}

function remove_empty(input){
    for(var i = 0;i < input.length;i++){
        // not done here
    }
}

process.on('uncaughtException',function(err){
    
    console.log(err.throw);
    console.log("[Connect abort]");

});

function hash_and_salt(id,pwd){

    console.log("[Encryption start]");
    const salt = "cn_line";
    var Cryptr = require('cryptr');
    var cryptr = new Cryptr(id);
    var hash = cryptr.encrypt(pwd,'sha256');
    console.log("[Encryption PWD] " + hash);
    return hash;
}

function login_notify(sock, id){

    console.log("[Notify]");

    for(var i = 0;i < client_socket.length;i++){
        if (client_socket[i] != sock)
            client_socket[i].write(LOGIN_NOTIFY + "/" + id);
    }
}

function logout_notify(id){
    for(var i = 0;i < client_socket.length;i++){
            client_socket[i].write(LOGOUT_NOTIFY + "/" + id);
    }
}
