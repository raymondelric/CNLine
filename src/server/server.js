var net = require('net');
var fs = require('fs');
var HOST = '127.0.0.1' ; // parameterize the IP of the Listen 
var PORT = 8000 ; // TCP LISTEN port 

const CONNECT = '00'
const REGISTER = '01'
const LOGIN = '02'
const LOGOUT = '03'

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

var IsLoggedIn = false;
var id;
var pass;

var client_socket = [];
var client_account = [];
var acc = [];
var pwd = [];
var num_account = 0;

console.log("[Loading Acc and Pwd]");
load_acc();
console.log("[Done loading "+num_account+" accounts]");


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
		var msg = packet.substring(2);
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
				console.log( 'current user: ' + id );
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

function register(sock,_id,_pwd){

    for(var i = 0;i < acc.length; i++){
        if (acc[i] === _id)
            return "Fail\n";
    }

    var content = fs.appendFileSync("./pwd",_id+"/"+_pwd+"\n");
    acc.push(_id);
    pwd.push(_pwd);
    console.log("[Register done]");

    return "Success\n";
}

function login(sock,_id,_pwd){

    console.log(_id+"  "+_pwd);
    for(var i = 0;i < acc.length; i++){
        console.log(acc[i]+"  "+pwd[i]);
        if (acc[i] === _id && pwd[i] === _pwd){
             
            var index = client_socket.indexOf(sock);
            client_account[index] = _id;
            
            return "Success\n";
        }
    }

    return "Fail\n";
}


