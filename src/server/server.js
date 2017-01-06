var net = require('net');
var fs = require('fs');
var sleep = require('sleep');

var HOST = '127.0.0.1' ; // parameterize the IP of the Listen 
var PORT = 9000 ; // TCP LISTEN port 

const CONNECT = '00'
const REGISTER = '01'
const LOGIN = '02'
const LOGOUT = '03'
const MESSAGE = '04'
const CREATEROOM = '05'

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

var IsLoggedIn = false;
var id;
var pass;

var client_socket = [];
var client_account = [];
var acc = [];
var pwd = [];
var num_account = 0;
var num_room = 0;

console.log("[Loading Acc and Pwd]");
load_acc();
console.log("[Done loading "+num_account+" accounts]");

console.log("[Checking Rooms]");
room_setup();
console.log("[Done loading "+num_room+" rooms]");




var key_arr = [];
var inner_arr = [];

inner_arr.push(1,2,3,4,5);
key_arr['apple'] = inner_arr;
inner_arr = [];
inner_arr.push(1,3,5);
key_arr['b'] = inner_arr;

for(var i = 0;i < key_arr['apple'].length;i++)
    console.log(key_arr['apple'][i]);
for(var i = 0;i < key_arr['b'].length;i++)
    console.log(key_arr['b'][i]);




net.createServer ( function ( sock ) { 
	console.log ( 'CONNECTED: '+sock.remoteAddress +':'+ sock.remotePort ) ;

    client_socket.push(sock);
   
	sock.on ( 'data', function ( data ) {
        console.log(data.toString());
		var packet = data.toString();
		var type = packet.substring(0,2);
		var msg = packet.substring(3);
		var ret = '';
		
		if(type === CONNECT){
			ret = CONNECT_OK;
			console.log( msg.split(/[\/,\n]+/)[0] );
		}
		else if(type === REGISTER){
			var strs = msg.split(/[\/,\n]+/);
			var _id = strs[0];
			var _pass = strs[1];
			ret = REGISTER_OK;
			console.log( 'register( ' + _id + ', ' + _pass + ' )' );
            // handle register
            ret = register(sock,_id,_pass);

		}
		else if(type === LOGIN){
			if(IsLoggedIn){
				ret = LOGIN_ALRD;
				//console.log( 'current user: ' + id );
			}
			else{
				var strs = msg.split(/[\/,\n]+/);
				id = strs[0];
				pass = strs[1];
				IsLoggedIn = true;
				ret = LOGIN_OK;
				console.log( 'login( ' + id + ', ' + pass + ' )' );
                ret = login(sock,id,pass);
			}
		}
        else if(type === MESSAGE){
	        var strs = msg.split(/[\/,\n]+/);
            console.log("[Clinet Sending Message] Client : " +
                          client_account[sock]); 

            ret = message(client_account[sock],strs);
            console.log("[Message result] " + ret);
        }
        else if(type === CREATEROOM){
	        var strs = msg.split(/[\/,\n]+/);
            console.log("[Creating Room]"); 
            ret = create_room(strs);
            console.log("[Room result] " + ret); 


        }
		else if(type === LOGOUT){
			IsLoggedIn = false;
			ret = LOGOUT_OK;
			console.log( 'logout( ' + id + ', ' + pass + ' )' );
		}

		sock.write ( ret ) ; 
	} ) ; 
	
	sock.on ( 'close', function ( data ) { 
		IsLoggedIn = false;
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
    console.log(num_room);
    num_room = num.length / 2;
    console.log(num.length);
}

function register(sock,_id,_pwd){

    for(var i = 0;i < acc.length; i++){
        if (acc[i] === _id)
            return REGISTER_DUP;
    }

    var content = fs.appendFileSync("./pwd",_id+"/"+_pwd+"\n");
    acc.push(_id);
    pwd.push(_pwd);
    content = fs.writeFileSync("./user/"+_id,_id+"/"+_pwd+"\n");

    console.log("[Register done]");

    return REGISTER_OK;
}

function login(sock,_id,_pwd){

    console.log(_id+"  "+_pwd);
    for(var i = 0;i < acc.length; i++){
        console.log(acc[i]+"  "+pwd[i]);
        if (acc[i] === _id && pwd[i] === _pwd){
             
            var index = client_socket.indexOf(sock);
            client_account[index] = _id;
            console.log("[Login] Account id : " + client_account[index]);
            
            return LOGIN_OK;
        }
    }

    return LOGIN_FAIL;
}

function message(name,input){
    
    var room_id = input[0];
    var body = input[1];

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
        fs.writeFileSync("./room/" + num_room + ".history","");
        
        var result = ROOM_OK + "/" + nm_room.toString();
       
        return result;
    }
}


